package com.minenash.customhud;

import com.google.gson.*;
import com.minenash.customhud.mod_compat.BuiltInModCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Objects;

public class CustomHud implements ModInitializer {


	public static Profile[] profiles = new Profile[3];
	public static int activeProfile = 1;
	public static boolean enabled = true;

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

		for (int i = 1; i <=3; i++ )
			profiles[i-1] = Profile.parseProfile(getProfilePath(i));

		try {
			Path path = getProfilePath(1);
			if (Files.newBufferedReader(path).readLine() == null) {
				try (OutputStream writer = Files.newOutputStream(path); InputStream input = getClass().getClassLoader().getResourceAsStream("assets/custom_hud/exampleProfile.txt")) {
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

		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
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
				case "config.json" : profile = 0; break;
				case "profile1.txt": profile = 1; break;
				case "profile2.txt": profile = 2; break;
				case "profile3.txt": profile = 3; break;
				default: continue;
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
							MinecraftClient.getInstance().player.sendMessage(Text.translatable("gui.custom_hud.profile_updated", profile), true);
						break;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		key.reset();
	}


}
