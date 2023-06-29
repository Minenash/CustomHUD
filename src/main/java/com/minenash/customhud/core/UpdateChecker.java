package com.minenash.customhud.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class UpdateChecker {

//    private static final String mcVersion = MinecraftClient.getInstance().getGameVersion();
    private static final String currentVersion;
    static {
        String modVersionRaw = FabricLoader.getInstance().getModContainer("custom_hud").get().getMetadata().getVersion().getFriendlyString();
        currentVersion = modVersionRaw.substring(0, modVersionRaw.indexOf("+"));
    }

    public static String[] latestKnownVersion = null;

    public static Text updateMessage = null;

    public static void check(String mcVersion) {
        if (currentVersion.contains("beta") || currentVersion.contains("alpha"))
            return;

        String[] modVersion = currentVersion.split("\\.");

        if (latestKnownVersion == null)
            latestKnownVersion = modVersion;


        JsonObject updateInfo = getUpdateData();
        if (updateInfo == null)
            return;

        System.out.println("VERSION: " + mcVersion);

        JsonObject info = updateInfo.getAsJsonObject(mcVersion);
        if (info == null)
            return;


        String versionRaw = info.get("version").getAsString();
        String[] version = versionRaw.split("\\.");
        if (version.length != latestKnownVersion.length || Arrays.equals(latestKnownVersion, version))
            return;
        if (!compareVersions(latestKnownVersion, version))
            return;

        latestKnownVersion = version;
        updateMessage = Text.literal("§eCustomHUD v" + versionRaw + " is available! ")
                .append(Text.literal("[Modrinth]").setStyle(Style.EMPTY
                        .withFormatting(Formatting.GREEN, Formatting.UNDERLINE)
                        .withClickEvent( new ClickEvent(ClickEvent.Action.OPEN_URL, info.get("link").getAsString()) )
                        .withHoverEvent( new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Download on Modrinth")) )
                )).append("\nWhat's New:\n §7" + info.get("msg").getAsString());
        ProfileHandler.saveConfig();

    }

    public static boolean compareVersions(String[] known, String[] latest) {
        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(latest[i]) > Integer.parseInt(known[i]))
                return true;
        }
        return false;


    }

    public static JsonObject getUpdateData() {
        try {
            URL url = new URL("https://customhud.dev/updateInfo.json");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200)
                throw new RuntimeException("HttpResponseCode: " + responseCode);

            return JsonParser.parseString(new String(conn.getInputStream().readAllBytes())).getAsJsonObject();
        }
        catch (Exception e) {
            System.out.println("[CustomHUD] Could not get update info");
            e.printStackTrace();
            return null;
        }
    }

    public static String getLatestKnownVersionAsString() {
        if (latestKnownVersion == null)
            return currentVersion;
        StringBuilder version = new StringBuilder();
        for (String part : latestKnownVersion)
            version.append(part).append(".");
        return version.substring(0, version.length()-1);
    }

}
