package com.minenash.customhud.core;

import com.google.gson.*;
import com.minenash.customhud.core.data.Crosshairs;
import com.minenash.customhud.core.data.Profile;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;

public class ProfileHandler {

    public static Profile[] profiles = new Profile[3];
    public static int activeProfile = 1;
    public static boolean enabled = true;

    public static final Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud");
    public static WatchService profileWatcher;
    public static final Path CONFIG = CONFIG_FOLDER.resolve("config.json");
    public static final Logger LOGGER = LogManager.getLogger("CustomHud");

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void setup() {
        DefaultVariables.register();
    }

    public static void loadProfiles() {
        for (int i = 1; i <=3; i++ ) {
            profiles[i - 1] = Profile.parseProfile(getProfilePath(i), i);
        }
        onProfileChangeOrUpdate();
        try {
            profileWatcher = FileSystems.getDefault().newWatchService();
            CONFIG_FOLDER.register(profileWatcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadConfig();
    }

    public static Profile getActiveProfile() {
        return enabled ? profiles[activeProfile -1] : null;
    }

    public static Crosshairs getCrosshair() {
        return getActiveProfile() == null ? Crosshairs.NORMAL : getActiveProfile().crosshair;
    }

    public static Path getProfilePath(int i) {
        return CONFIG_FOLDER.resolve("profile" + i + ".txt");
    }

    public static void onProfileChangeOrUpdate() {
        FabricLoader.getInstance().getObjectShare().put("customhud:crosshair", profiles[activeProfile-1].crosshair.getName());
    }

    public static void saveConfig() {
        if(!Files.exists(CONFIG)) {
            try {
                Files.createFile(CONFIG);
            } catch (IOException e) {
                LOGGER.error("Couldn't create the config file");
                return;
            }
        }
        JsonObject config = new JsonObject();
        config.addProperty("enabled", enabled);
        config.addProperty("activeProfile", activeProfile);
        config.addProperty("latestKnownVersion", UpdateChecker.getLatestKnownVersionAsString());
        try {
            Files.write(CONFIG, gson.toJson(config).getBytes());
        } catch (IOException e) {
            LOGGER.error("Couldn't save the config file");
        }
    }
    public static void loadConfig() {
        if(!Files.exists(CONFIG)) {
            LOGGER.info("Couldn't find the config File, creating one");
            saveConfig();
            return;
        }
        boolean fix = false;
        try {
            JsonObject json = gson.fromJson(Files.newBufferedReader(CONFIG), JsonObject.class);
            enabled = json.get("enabled").getAsBoolean();
            activeProfile = json.get("activeProfile").getAsInt();
            if (activeProfile > 3 || activeProfile < 1) {
                activeProfile = 1;
                fix = true;
            }
            JsonElement latestKnownVersion = json.get("latestKnownVersion");
            if (latestKnownVersion != null)
                UpdateChecker.latestKnownVersion =latestKnownVersion.getAsString().split("\\.");
        } catch (JsonSyntaxException | NullPointerException e) {
            LOGGER.warn("Malformed Json, Fixing");
            fix = true;
        } catch (IOException e) {
            LOGGER.error("Couldn't read the config");
        }
        if (fix)
            saveConfig();
    }

}
