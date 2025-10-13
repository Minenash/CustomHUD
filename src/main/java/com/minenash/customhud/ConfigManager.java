package com.minenash.customhud;

import com.google.gson.*;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigManager {

    public static final Path CONFIG = CustomHud.CONFIG_FOLDER.resolve("config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static boolean readProfiles = false;
    public static void load() {
        try {
            Files.createDirectories(CustomHud.PROFILE_FOLDER);
        }
        catch (Exception e) {
            CustomHud.LOGGER.info("[CustomHud] Can't write to config dir");
            CustomHud.readProfiles();
            return;
        }
        if(!Files.exists(CONFIG)) {
            CustomHud.LOGGER.info("[CustomHud] Couldn't find the config File, creating one");

            if (isDirEmpty(CustomHud.PROFILE_FOLDER))
                populate();

            CustomHud.readProfiles();
            save();
            return;
        }
        readProfiles = false;
        try {
            read( GSON.fromJson(Files.newBufferedReader(CONFIG), JsonObject.class) );
        }
        catch (JsonSyntaxException | NullPointerException e) {
            CustomHud.LOGGER.warn("[CustomHud] Malformed Json, Fixing");
            if (!readProfiles)
                CustomHud.readProfiles();
            save();
        }
        catch (IOException e) {
            if (!readProfiles)
                CustomHud.readProfiles();
            CustomHud.LOGGER.error("[CustomHud] Couldn't read the config");
        }
    }

    private static boolean isDirEmpty(Path path) {
        try(Stream<Path> files = Files.list(path)) {
            return files.findAny().isEmpty();
        }
        catch (IOException e) {
            return false;
        }
    }

    private static void populate() {
        URL folder = Profile.class.getClassLoader().getResource("assets/custom_hud/profiles");
        try (Stream<Path> files = Files.list(Path.of(folder.toURI()))) {
            files.forEach(file -> {
                try (OutputStream writer = Files.newOutputStream(CustomHud.PROFILE_FOLDER.resolve(file.getFileName().toString()));
                     InputStream input = Files.newInputStream(file)){
                    input.transferTo(writer);
                }
                catch (Exception e) {
                    CustomHud.LOGGER.error("[CustomHud] Can't write default profiles");
                    CustomHud.LOGGER.catching(e);
                }
            });
        }
        catch (Exception e) {
            CustomHud.LOGGER.error("[CustomHud] Can't write default profiles");
            CustomHud.LOGGER.catching(e);
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
        if (version == 2)
            readV2(json);
        else if (version == 3)
            readV3(json);
        else
            CustomHud.LOGGER.warn("[CustomHud] Unknown Config Version. Not loading it");
    }

    public static void readV1AndConvert(JsonObject json) {
        CustomHud.LOGGER.info("[CustomHud] Config Version Not Found, Assuming Version 1, Converting");
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
                        try { Files.move(path, CustomHud.PROFILE_FOLDER.resolve(outName)); }
                        catch (IOException e) { CustomHud.LOGGER.catching(e); }
                    }
                }
        } catch (IOException e) {
            CustomHud.LOGGER.catching(e);
        }
        CustomHud.readProfiles();
        readProfiles = true;
        ProfileManager.fallback();
        save();
    }

    public static void readV2(JsonObject json) {
        String activeProfileName = json.get("activeProfile").getAsString();
        json.remove("activeProfile");
        json.addProperty("activeProfileName", activeProfileName);
        readV3(json);
    }

    public static void readV3(JsonObject json) {
        CustomHud.readProfiles();
        readProfiles = true;
        JsonElement lastVersion = json.get("latestKnownVersion");
        if (lastVersion != null)
            UpdateChecker.latestKnownVersion = lastVersion.getAsString().split("\\.");

        var profiles = ProfileManager.getProfiles().stream().collect(Collectors.toMap(p -> p.name, p -> p));

        if (json.has("debugMode"))
            CustomHud.DEBUG_MODE = json.get("debugMode").getAsBoolean();

        ProfileManager.enabled = json.get("enabled").getAsBoolean();

        JsonArray jsonProfiles = json.get("profiles").getAsJsonArray();
        List<Profile> order = new ArrayList<>();
        for (JsonElement element : jsonProfiles) {
            JsonObject obj = element.getAsJsonObject();
            String name = obj.get("name").getAsString();

            Profile p = profiles.get(name);
            if (p != null && !order.contains(p)) {
                String keyTranslation = obj.get("key").getAsString();
                p.keyBinding.setBoundKey(InputUtil.fromTranslationKey(keyTranslation));
                p.cycle = obj.get("cycle").getAsBoolean();
                order.add(p);
            }
        }
        ProfileManager.reorder(order, false);

        String activeProfileName = json.get("activeProfileName").getAsString();
        if (activeProfileName == null)
            ProfileManager.setActive(null);
        else {
            Profile profile = profiles.get(activeProfileName);
            if (profile != null)
                ProfileManager.setActive(profile);
        }

        JsonArray toggleBinds = json.get("toggleBinds").getAsJsonArray();
        for (JsonElement element : toggleBinds) {
            JsonObject obj = element.getAsJsonObject();
            String profileName = obj.get("profile").getAsString();
            Profile p = profiles.get(profileName);
            if (p != null) {
                String name = obj.get("name").getAsString();
                String keyTranslation = obj.get("key").getAsString();
                String modifierTranslation = obj.has("modifier") ? obj.get("modifier").getAsString() : null;

                if (p.toggles.containsKey(name))
                    p.toggles.get(name).key.setBoundKey(InputUtil.fromTranslationKey(keyTranslation));
                else {
                    KeyBinding key = new KeyBinding("customhud_toggle_" + UUID.randomUUID(), GLFW.GLFW_KEY_UNKNOWN, "customhud");
                    key.setBoundKey(InputUtil.fromTranslationKey(keyTranslation));
                    KeyBinding modifier = new KeyBinding("customhud_toggle_" + UUID.randomUUID(), GLFW.GLFW_KEY_UNKNOWN, "customhud");
                    if (modifierTranslation != null)
                        modifier.setBoundKey(InputUtil.fromTranslationKey(modifierTranslation));
                    p.toggles.put(name, new Toggle(name, false, -1, false, modifier, key));
                }
            }
        }
    }

    public static void save() {
        if(!Files.exists(CONFIG)) {
            try {
                Files.createFile(CONFIG);
            } catch (IOException e) {
                CustomHud.LOGGER.error("[CustomHud] Couldn't create the config file");
                return;
            }
        }
        JsonObject config = new JsonObject();
        config.addProperty("configVersion", 3);
        config.addProperty("debugMode", CustomHud.DEBUG_MODE);
        config.addProperty("enabled", ProfileManager.enabled);
        config.addProperty("activeProfileName", ProfileManager.getActive() == null ? "" : ProfileManager.getActive().name);
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

        JsonArray toggleBinds = new JsonArray();
        for (Profile profile : ProfileManager.getProfiles()) {
            for (Toggle toggle : profile.toggles.values()) {
                if (toggle.direct || toggle.key.isUnbound())
                    continue;
                JsonObject obj = new JsonObject();
                obj.addProperty("profile", profile.name);
                obj.addProperty("name", toggle.name);
                obj.addProperty("key", toggle.key.getBoundKeyTranslationKey());
                obj.addProperty("modifier", toggle.modifier.getBoundKeyTranslationKey());
                obj.addProperty("value", toggle.value);
                toggleBinds.add(obj);
            }
        }
        config.add("toggleBinds", toggleBinds);


        try {
            Files.write(CONFIG, GSON.toJson(config).getBytes());
//            CustomHud.LOGGER.info("[CustomHud] Saved config");
        } catch (IOException e) {
            CustomHud.LOGGER.error("[CustomHud] Couldn't save the config file");
        }
    }

}
