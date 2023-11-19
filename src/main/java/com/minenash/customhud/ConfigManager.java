package com.minenash.customhud;

import com.google.gson.*;
import com.minenash.customhud.data.Profile;
import net.minecraft.client.util.InputUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigManager {

    public static final Path CONFIG = CustomHud.CONFIG_FOLDER.resolve("config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        try {
            Files.createDirectories(CustomHud.PROFILE_FOLDER);
        }
        catch (Exception e) {
            CustomHud.LOGGER.info("Can't write to config dir");
            return;
        }
        if(!Files.exists(CONFIG)) {
            CustomHud.LOGGER.info("Couldn't find the config File, creating one");
            save();
            return;
        }
        try {
            read( GSON.fromJson(Files.newBufferedReader(CONFIG), JsonObject.class) );
        }
        catch (JsonSyntaxException | NullPointerException e) {
            CustomHud.LOGGER.warn("Malformed Json, Fixing");
            save();
        }
        catch (IOException e) {
            CustomHud.LOGGER.error("Couldn't read the config");
        }
    }

    public static void read(JsonObject json) {
        JsonElement lastVersion = json.get("latestKnownVersion");
        if (lastVersion != null)
            UpdateChecker.latestKnownVersion = lastVersion.getAsString().split("\\.");

        if (!json.has("configVersion")) {
            readV1AndConvert(json);
            return;
        }

        int version = json.get("configVersion").getAsInt();
        if (version != 2) {
            //TODO: Do Toast
            System.out.println("Unknown Config Version. Not loading it");
        }
        readV2(json);
    }

    public static void readV1AndConvert(JsonObject json) {
        System.out.println("Config Version Not Found, Assuming Version 1, Converting");
        ProfileManager.enabled = json.get("enabled").getAsBoolean();

        try(Stream<Path> pathsStream = Files.list(CustomHud.CONFIG_FOLDER)) {
            for (Path path : pathsStream.collect(Collectors.toSet()))
                if (!Files.isDirectory(path)) {
                    String fileName = path.getFileName().toString();
                    if (fileName.endsWith(".txt")) {
                        String outName = switch (fileName) {
                            case "profile1.txt" -> "Profile 1.txt";
                            case "profile2.txt" -> "Profile 2.txt";
                            case "profile3.txt" -> "Profile 3.txt";
                            default -> fileName;
                        };
                        Files.move(path, CustomHud.PROFILE_FOLDER.resolve(outName));
                    }
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        CustomHud.readProfiles();
        ProfileManager.fallback();
        save();
    }

    public static void readV2(JsonObject json) {
        JsonElement lastVersion = json.get("latestKnownVersion");
        if (lastVersion != null)
            UpdateChecker.latestKnownVersion = lastVersion.getAsString().split("\\.");

        var profiles = ProfileManager.getProfiles().stream().collect(Collectors.toMap(p -> p.name, p -> p));

        //TODO
        ProfileManager.enabled = json.get("enabled").getAsBoolean();
        String activeProfileName = json.get("activeProfile").getAsString();
        if (activeProfileName == null)
            ProfileManager.setActive(null);
        else {
            Profile profile = profiles.get(activeProfileName);
            if (profile != null)
                ProfileManager.setActive(profile);
        }

        JsonArray array = json.get("profiles").getAsJsonArray();
        List<Profile> order = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();

            Profile p = profiles.get(name);
            if (p != null) {
                String keyTranslation = obj.get("key").getAsString();
                p.keyBinding.setBoundKey(InputUtil.fromTranslationKey(keyTranslation));
                p.cycle = obj.get("cycle").getAsBoolean();
                order.add(p);
            }
        }
        ProfileManager.reorder(order);
    }

    public static void save() {
        if(!Files.exists(CONFIG)) {
            try {
                Files.createFile(CONFIG);
            } catch (IOException e) {
                CustomHud.LOGGER.error("Couldn't create the config file");
                return;
            }
        }
        JsonObject config = new JsonObject();
        config.addProperty("configVersion", 2);
        config.addProperty("enabled", ProfileManager.enabled);
        config.addProperty("activeProfile", ProfileManager.getActive() == null ? null : ProfileManager.getActive().name);
        config.addProperty("latestKnownVersion", UpdateChecker.getLatestKnownVersionAsString());

        JsonArray profiles = new JsonArray();

        for (Profile profile : ProfileManager.getProfiles()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", profile.name);
            obj.addProperty("key", profile.keyBinding.getBoundKeyTranslationKey());
            obj.addProperty("cycle", profile.cycle);
            profiles.add(obj);
        }
        config.add("profiles", profiles);


        try {
            Files.write(CONFIG, GSON.toJson(config).getBytes());
        } catch (IOException e) {
            CustomHud.LOGGER.error("Couldn't save the config file");
        }
    }

}
