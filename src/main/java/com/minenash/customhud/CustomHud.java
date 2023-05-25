package com.minenash.customhud;

import com.google.gson.*;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.errors.ErrorScreen;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.mod_compat.BuiltInModCompat;
import com.minenash.customhud.render.CustomHudRenderer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Objects;

public class CustomHud implements ModInitializer {

	//Debug: LD_PRELOAD=/home/jakob/Programs/renderdoc_1.25/lib/librenderdoc.so
	private static final MinecraftClient client = MinecraftClient.getInstance();

	public static Profile[] profiles = new Profile[3];
	public static int activeProfile = 1;
	public static boolean enabled = true;

	public static final boolean INDEPENDENT_GIZMO_INSTALLED = FabricLoader.getInstance().isModLoaded("independent_gizmo");

	public static final Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud");
	public static WatchService profileWatcher;
	public static final Path CONFIG = CONFIG_FOLDER.resolve("config.json");
	public static final Logger LOGGER = LogManager.getLogger("CustomHud");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final KeyBinding kb_enable = registerKeyBinding("enable", GLFW.GLFW_KEY_UNKNOWN);
	private static final KeyBinding kb_cycleProfiles = registerKeyBinding("cycle_profiles", GLFW.GLFW_KEY_GRAVE_ACCENT);
	private static final KeyBinding kb_swapToProfile1 = registerKeyBinding("swap_to_profile1", GLFW.GLFW_KEY_UNKNOWN);
	private static final KeyBinding kb_swapToProfile2 = registerKeyBinding("swap_to_profile2", GLFW.GLFW_KEY_UNKNOWN);
	private static final KeyBinding kb_swapToProfile3 = registerKeyBinding("swap_to_profile3", GLFW.GLFW_KEY_UNKNOWN);

	private static final KeyBinding kb_showErrors = registerKeyBinding("show_errors", GLFW.GLFW_KEY_B);

	private static KeyBinding registerKeyBinding(String binding, int defaultKey) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.custom_hud." + binding, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.custom_hud"));
	}

	@Override
	public void onInitialize() {
		BuiltInModCompat.register();

		try {
			profileWatcher = FileSystems.getDefault().newWatchService();
			CONFIG_FOLDER.register(profileWatcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}
		loadConfig();
		UpdateChecker.check();

		HudRenderCallback.EVENT.register(CustomHudRenderer::render);



		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (UpdateChecker.updateMessage != null)
				client.getMessageHandler().onGameMessage(UpdateChecker.updateMessage, false);
		});

	}

	public static void loadProfiles() {
		for (int i = 1; i <=3; i++ ) {
			profiles[i - 1] = Profile.parseProfile(getProfilePath(i), i);
		}
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
		else if (kb_showErrors.wasPressed()) {
			client.setScreen(new ErrorScreen(client.currentScreen));
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
		} catch (JsonSyntaxException|NullPointerException e) {
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
						CustomHud.profiles[profile - 1] = Profile.parseProfile(original, profile);
						LOGGER.info("Updated Profile " + profile);
						showToast(profile, false);
						if (client.currentScreen instanceof ErrorScreen screen)
							screen.changeProfile(profile);
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

	public static void showToast(int profile, boolean mainMenu) {
		client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,
				Text.translatable("gui.custom_hud.profile_updated", profile).formatted(Formatting.WHITE),
				Errors.hasErrors(profile) ?
						Text.literal("§cFound " + Errors.getErrors(profile).size() + " errors")
								.append(client.currentScreen instanceof TitleScreen ?
										Text.literal("§7, view in config screen via modmenu ")
										: Text.literal("§7, press ")
										.append(((MutableText)kb_showErrors.getBoundKeyLocalizedText()).formatted(Formatting.AQUA))
										.append("§7 to view"))
						: Text.literal("§aNo errors found")
		));
	}


}
