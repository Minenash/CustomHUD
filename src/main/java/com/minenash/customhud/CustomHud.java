package com.minenash.customhud;

import com.google.gson.*;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.mod_compat.BuiltInModCompat;
import com.minenash.customhud.render.CustomHudRenderer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import java.util.Objects;

public class CustomHud implements ModInitializer {

	//Debug: LD_PRELOAD=/home/jakob/Programs/renderdoc_1.25/lib/librenderdoc.so

	public static Profile[] profiles = new Profile[3];
	public static int activeProfile = 1;
	public static boolean enabled = true;

	public static final String version = "2.2.0";
	private static String latestKnownVersion = null;
	private static String updateMsg = null;

	public static final boolean INDEPENDENT_GIZMO_INSTALLED = FabricLoader.getInstance().isModLoaded("independent_gizmo");

	public static final Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud");
	public static WatchService profileWatcher;
	public static final Path CONFIG = CONFIG_FOLDER.resolve("config.json");
	public static final Logger LOGGER = LogManager.getLogger("CustomHud");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final KeyBinding kb_enable = registerKeyBinding("enable");
	private static final KeyBinding kb_cycleProfiles = registerKeyBinding("cycle_profiles");
	private static final KeyBinding kb_swapToProfile1 = registerKeyBinding("swap_to_profile1");
	private static final KeyBinding kb_swapToProfile2 = registerKeyBinding("swap_to_profile2");
	private static final KeyBinding kb_swapToProfile3 = registerKeyBinding("swap_to_profile3");

	private static KeyBinding registerKeyBinding(String binding) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.custom_hud." + binding, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.custom_hud"));
	}

	@Override
	public void onInitialize() {
		BuiltInModCompat.register();

		try {
			Path path = getProfilePath(1);
			if (Files.newBufferedReader(path).readLine() == null) {
				try (OutputStream writer = Files.newOutputStream(path); InputStream input = getClass().getClassLoader().getResourceAsStream("assets/custom_hud/example_profile.txt")) {
					input.transferTo(writer);
				}
				CustomHud.profiles[0] = Profile.parseProfile(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			profileWatcher = FileSystems.getDefault().newWatchService();
			CONFIG_FOLDER.register(profileWatcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadConfig();
		checkForUpdate();

		HudRenderCallback.EVENT.register(CustomHudRenderer::render);

		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
//		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
//			if (updateMsg == null)
//				return;
//			client.getMessageHandler().onGameMessage(
//					Text.literal("\nCustomHud v" + latestKnownVersion + " is now available!\n§7" + updateMsg + "\n§8[")
//						.append(Text.literal("§eChangelog").setStyle(Style.EMPTY
//							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/customhud/changelog"))
//							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("§eOpen link to changelog")))))
//						.append("§8] [")
//						.append(Text.literal("§eDownload").setStyle(Style.EMPTY
//							.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/customhud"))
//							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("§eOpen link to download page on §aModrinth"))))
//						.append("§8]\n")
//						), false);
//		});

	}

	public static void loadProfiles() {
		for (int i = 1; i <=3; i++ )
			profiles[i-1] = Profile.parseProfile(getProfilePath(i));
		FabricLoader.getInstance().getObjectShare().put("independent_gizmo:enable", profiles[activeProfile-1].debugCrosshair);
	}

	private static ComplexData.Enabled previousEnabled = ComplexData.Enabled.DISABLED;
	public static boolean justSaved = false;
	private static int saveDelay = -1;
	private static void onTick(MinecraftClient client) {
		if (saveDelay > 0)
			saveDelay--;
		else if (saveDelay == 0) {
			saveConfig();
			saveDelay = -1;
		}

		updateProfiles();
		Profile profile = CustomHud.getActiveProfile();
		if (profile != null && client.cameraEntity != null) {
			if (!Objects.equals(previousEnabled,profile.enabled)) {
				ComplexData.reset();
				previousEnabled = profile.enabled;
			}
			ComplexData.update(profile);
		}


		if (kb_enable.wasPressed()) {
			enabled = !enabled;
			CustomHud.justSaved = true;
			saveDelay = 100;
			return;
		}


		if (kb_cycleProfiles.wasPressed()) {
			activeProfile = activeProfile == 3 ? 1 : activeProfile + 1;
			if (!enabled) enabled = true;
		}
		else if (kb_swapToProfile1.wasPressed()) {
			activeProfile = 1;
			if (!enabled) enabled = true;
		}
		else if (kb_swapToProfile2.wasPressed()) {
			activeProfile = 2;
			if (!enabled) enabled = true;
		}
		else if (kb_swapToProfile3.wasPressed()) {
			activeProfile = 3;
			if (!enabled) enabled = true;
		}
		else
			return;

		FabricLoader.getInstance().getObjectShare().put("independent_gizmo:enable", profiles[activeProfile-1].debugCrosshair);

		CustomHud.justSaved = true;
		saveDelay = 100;
	}

	public static Profile getActiveProfile() {
		return enabled ? profiles[activeProfile -1] : null;
	}

	public static Path getProfilePath(int i) {
		return CONFIG_FOLDER.resolve("profile" + i + ".txt");
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
		config.addProperty("latestKnownVersion", latestKnownVersion);
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
			CustomHud.latestKnownVersion = latestKnownVersion == null ? version : latestKnownVersion.getAsString();
		} catch (JsonSyntaxException e) {
			LOGGER.warn("Malformed Json, Fixing");
			fix = true;
		} catch (IOException e) {
			LOGGER.error("Couldn't read the config");
		}
		if (fix)
			saveConfig();
	}

	private static void updateProfiles() {
		WatchKey key = CustomHud.profileWatcher.poll();
		if (key == null)
			return;
		for (WatchEvent<?> event : key.pollEvents()) {
			if (CustomHud.justSaved) {
				CustomHud.justSaved = false;
				break;
			}
			int profile;
			switch (event.context().toString()) {
				case "config.json" -> profile = 0;
				case "profile1.txt" -> profile = 1;
				case "profile2.txt" -> profile = 2;
				case "profile3.txt" -> profile = 3;
				default -> { continue; }
			}
			Path changed = CustomHud.CONFIG_FOLDER.resolve((Path) event.context());
			Path original = profile == 0 ? CustomHud.CONFIG : CustomHud.getProfilePath(profile);
			try {
				if (Files.exists(changed) && Files.isSameFile(changed, original)) {
					if (profile == 0) {
						CustomHud.LOGGER.info("Reloading Config");
						CustomHud.loadConfig();
					}
					else {
						CustomHud.profiles[profile - 1] = Profile.parseProfile(original);
						LOGGER.info("Updated Profile " + profile);
						if (MinecraftClient.getInstance().player != null)
							MinecraftClient.getInstance().player.sendMessage(MutableText.of(new TranslatableTextContent("gui.custom_hud.profile_updated", profile)), true);
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FabricLoader.getInstance().getObjectShare().put("independent_gizmo:enable", profiles[activeProfile-1].debugCrosshair);
		key.reset();
	}

	private static void checkForUpdate() {
		try {
			JsonObject updateData = getUpdateData();
			String version = updateData.get("latest").getAsString();
			if (!version.equals(latestKnownVersion)) {
				latestKnownVersion = version;
				if (CustomHud.version.compareTo(version) < 0) {
					updateMsg = updateData.get("info").getAsString();
					saveConfig();
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static JsonObject getUpdateData() throws IOException {
		URL url = new URL("https://raw.githubusercontent.com/Minenash/CustomHUD/1.19/updateInfo.json");

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();

		int responseCode = conn.getResponseCode();

		if (responseCode != 200)
			throw new RuntimeException("HttpResponseCode: " + responseCode);

		return JsonParser.parseString( new String(conn.getInputStream().readAllBytes()) ).getAsJsonObject();
	}


}
