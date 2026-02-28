package com.minenash.customhud;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.complex.EstimatedTick;
import com.minenash.customhud.data.DisableElement;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.gui.ErrorsScreen;
import com.minenash.customhud.gui.NewConfigScreen;
import com.minenash.customhud.gui.TogglesScreen;
import com.minenash.customhud.render.CustomHudRenderer3;
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
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomHud implements ModInitializer {

	//Debug: LD_PRELOAD=/home/jakob/Programs/renderdoc_1.25/lib/librenderdoc.so
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger("CustomHud");
	public static boolean MODMENU_INSTALLED = false;

	public static boolean DEBUG_MODE = false;

	public static final Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud");
	public static final Path PROFILE_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud/profiles");
	public static WatchService profileWatcher;

	public static final KeyBinding.Category MAIN_KB_CAT = KeyBinding.Category.create(Identifier.of("customhud", "customhud"));
	public static final KeyBinding.Category TOGGLES_KB_CAT = KeyBinding.Category.create(Identifier.of("customhud", "toggles"));

	public static final KeyBinding kb_enable = registerKeyBinding("enable", GLFW.GLFW_KEY_UNKNOWN);
	public static final KeyBinding kb_cycleProfiles = registerKeyBinding("cycle_profiles", GLFW.GLFW_KEY_GRAVE_ACCENT);
	public static final KeyBinding kb_showErrors = registerKeyBinding("show_errors", GLFW.GLFW_KEY_B);
	public static final KeyBinding kb_refreshProfilerTimings = registerKeyBinding("refresh_profiler_timings", GLFW.GLFW_KEY_UNKNOWN);

	private static KeyBinding registerKeyBinding(String binding, int defaultKey) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.custom_hud." + binding, InputUtil.Type.KEYSYM, defaultKey, MAIN_KB_CAT));
	}

	@Override
	public void onInitialize() {
//		UpdateChecker.check();

		HudRenderCallback.EVENT.register(CustomHudRenderer3::render);


		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (UpdateChecker.updateMessage != null)
				client.getMessageHandler().onGameMessage(UpdateChecker.updateMessage, false);
			EstimatedTick.reset();

			var profile = ProfileManager.getActive();
			if (profile != null)
				profile.boolEvents.add("join");
		});


	}

	public static void delayedInitialize() {
		MODMENU_INSTALLED = FabricLoader.getInstance().isModLoaded("modmenu");


		ConfigManager.load();
		updateCrosshairObjectShare();

		ConfigManager.save();

		try {
			profileWatcher = FileSystems.getDefault().newWatchService();
			PROFILE_FOLDER.register(profileWatcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
		} catch (IOException e) {
			CustomHud.LOGGER.catching(e);
		}
	}

	public static void readProfiles() {
		try(Stream<Path> pathsStream = Files.list(PROFILE_FOLDER).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
			for (Path path : pathsStream.toList())
				if (!Files.isDirectory(path)) {
					String name = path.getFileName().toString();
					if (name.endsWith(".txt"))
						ProfileManager.add(Profile.parseProfile(path, name.substring(0, name.length()-4)));
				}
		} catch (IOException e) {
			CustomHud.LOGGER.catching(e);
		}
	}


	private static ComplexData.Enabled previousEnabled = ComplexData.Enabled.DISABLED;
	private static int saveDelay = -1;
	private static void onTick(MinecraftClient client) {
		if (saveDelay > 0)
			saveDelay--;
		else if (saveDelay == 0) {
			ConfigManager.save();
			saveDelay = -1;
		}

		updateProfiles();
		Profile profile = ProfileManager.getActive();
		if (profile != null && client.getCameraEntity() != null) {
			if (!Objects.equals(previousEnabled,profile.enabled)) {
				ComplexData.reset();
				previousEnabled = profile.enabled;
			}
			ComplexData.update(profile);
		}


//		while (SWITCH_RENDERER.wasPressed()) {
//			useNewRenderer = !useNewRenderer;
//		}
		while (kb_refreshProfilerTimings.wasPressed()) {
			ComplexData.refreshTimings = true;
		}
		while (kb_enable.wasPressed()) {
			ProfileManager.enabled = !ProfileManager.enabled;
			saveDelay = 100;
		}
		while (kb_cycleProfiles.wasPressed()) {
			ProfileManager.cycle();
			saveDelay = 100;
		}
		for (Profile p : ProfileManager.getProfiles()) {
			while (p.keyBinding.wasPressed()) {
				ProfileManager.setActive(p);
				ProfileManager.enabled = true;
				saveDelay = 100;
			}
		}
		// Only check toggles for the active profile to avoid consuming key presses
		Profile activeProfile = ProfileManager.getActive();
		if (activeProfile != null) {
			for (Toggle t : activeProfile.toggles.values()) {
				boolean wasPressed = t.key.wasPressed();
				if (isKeybindPressed(t.modifier) && wasPressed)
					t.toggle();
			}
		}

		while (kb_showErrors.wasPressed()) {
			if (client.currentScreen == null)
				if (ProfileManager.getActive() != null && Errors.hasErrors(ProfileManager.getActive().name))
					CLIENT.setScreen(new ErrorsScreen(null));
				else
					CLIENT.setScreen(new NewConfigScreen(null));
		}
	}

	public static final Map<Integer,Boolean> IS_MOUSE_DOWN = new HashMap<>(6);
	public static boolean isKeybindPressed(KeyBinding key) {
		if (key.isUnbound())
			return true;
		if (key.boundKey.type == InputUtil.Type.MOUSE)
			return IS_MOUSE_DOWN.getOrDefault(KeyBindingHelper.getBoundKeyOf(key).getCode(), false);
		return InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), KeyBindingHelper.getBoundKeyOf(key).getCode());
	}

	public static boolean isNotDisabled(DisableElement element) {
		return ProfileManager.getActive() == null || !ProfileManager.getActive().disabled.contains(element);
	}
	public static boolean isDisabled(DisableElement element) {
		return ProfileManager.getActive() != null && ProfileManager.getActive().disabled.contains(element);
	}

	public static boolean ignoreFirstToast = false;
	private static void updateProfiles() {
		WatchKey key = CustomHud.profileWatcher.poll();
		if (key == null)
			return;
		for (WatchEvent<?> event : key.pollEvents()) {
			Path path = CustomHud.PROFILE_FOLDER.resolve((Path) event.context());
			String fileName = path.getFileName().toString();
			CustomHud.logInDebugMode("Filename: `" + fileName + "`");
			if (!fileName.endsWith(".txt"))
				continue;
			fileName = fileName.substring(0, fileName.length()-4);
			var profiles = ProfileManager.getProfiles().stream().collect(Collectors.toMap(p -> p.name, p -> p));
			Profile profile = profiles.get(fileName);

			if (event.kind().name().equals("ENTRY_DELETE")) {
				if (profile != null)
					ProfileManager.remove(profile, false);
				continue;
			}
			if (event.kind().name().equals("ENTRY_CREATE")) {
				if (profile != null) {
					continue;
				}
				else
					ProfileManager.add( Profile.parseProfile(path, fileName) );
			}
			if (event.kind().name().equals("ENTRY_MODIFY")) {
				if (profile == null) {
					logInDebugMode("CustomHud ENTRY MODIFY: You Don't Exist?");
					continue;
				}
				else {
					profile = Profile.parseProfile(path, fileName);
					ProfileManager.replace(profile);
					if (CLIENT.currentScreen instanceof ErrorsScreen screen)
						screen.changeProfile(profile);
					if (CLIENT.currentScreen instanceof TogglesScreen screen)
						screen.changeProfile(profile);
					if (CLIENT.currentScreen instanceof NewConfigScreen screen)
						screen.init();
				}
			}

            LOGGER.info("Updated Profile {}", fileName);
			if (!ignoreFirstToast)
				showToast(fileName);
			ignoreFirstToast = false;
		}
		key.reset();
	}

	public static void resourceTriggeredReload() {
		boolean anyHasErrors = false;
		try(Stream<Path> pathsStream = Files.list(PROFILE_FOLDER).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
			for (Path path : pathsStream.toList()) {
				if (!Files.isDirectory(path)) {
					String name = path.getFileName().toString();
					if (name.endsWith(".txt")) {
						name = name.substring(0, name.length() - 4);
						ProfileManager.replace(Profile.parseProfile(path, name));
						if (Errors.hasErrors(name)) {
							anyHasErrors = true;
							CustomHud.showToast(name);
						}
					}
				}
			}
			if (!anyHasErrors) {
				CustomHud.showAllUpdatedToast();
			}
		} catch (IOException e) {
			CustomHud.LOGGER.catching(e);
		}
		CustomHud.updateCrosshairObjectShare();
	}

	public static void updateCrosshairObjectShare() {
		FabricLoader.getInstance().getObjectShare().put("customhud:crosshair",
				ProfileManager.getActive() == null ? "normal" : ProfileManager.getActive().crosshair.getName());
	}

	public static void showToast(String profileName) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
				Text.translatable("gui.custom_hud.profile_updated", profileName).formatted(Formatting.WHITE),
				Errors.hasErrors(profileName) ?
						Text.literal("§cFound " + Errors.getErrors(profileName).size() + " errors")
							.append(CLIENT.currentScreen instanceof TitleScreen ?
								Text.literal("§7, view in config screen via modmenu ")
								: Text.literal("§7, press ")
									.append(((MutableText)kb_showErrors.getBoundKeyLocalizedText()).formatted(Formatting.AQUA))
									.append("§7 to view"))
						: Text.literal("§aNo errors found")
		));
	}
	public static void showAllUpdatedToast() {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION,
				Text.literal("§fAll Profiles Updated"),
				Text.literal("§aNo errors found")
		));
	}

	public static void logInDebugMode(String msg) {
		if (DEBUG_MODE)
			LOGGER.info(msg);
	}


}
