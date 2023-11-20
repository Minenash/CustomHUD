package com.minenash.customhud;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.data.DisableElement;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import com.minenash.customhud.gui.ErrorsScreen;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.gui.TogglesScreen;
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
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomHud implements ModInitializer {

	//Debug: LD_PRELOAD=/home/jakob/Programs/renderdoc_1.25/lib/librenderdoc.so
	public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
	public static final Logger LOGGER = LogManager.getLogger("CustomHud");

	public static final Path CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud");
	public static final Path PROFILE_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("custom-hud/profiles");
	public static WatchService profileWatcher;

	private static final KeyBinding kb_enable = registerKeyBinding("enable", GLFW.GLFW_KEY_UNKNOWN);
	private static final KeyBinding kb_showErrors = registerKeyBinding("show_errors", GLFW.GLFW_KEY_B);

	private static KeyBinding registerKeyBinding(String binding, int defaultKey) {
		return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.custom_hud." + binding, InputUtil.Type.KEYSYM, defaultKey, "category.custom_hud"));
	}

	@Override
	public void onInitialize() {
		BuiltInModCompat.register();

		UpdateChecker.check();

		HudRenderCallback.EVENT.register(CustomHudRenderer::render);

		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (UpdateChecker.updateMessage != null)
				client.getMessageHandler().onGameMessage(UpdateChecker.updateMessage, false);
		});

	}

	public static void delayedInitialize() {
		readProfiles();
		onProfileChangeOrUpdate();

		ConfigManager.load();
		ConfigManager.save();

		try {
			profileWatcher = FileSystems.getDefault().newWatchService();
			PROFILE_FOLDER.register(profileWatcher, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
		} catch (IOException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}


	private static ComplexData.Enabled previousEnabled = ComplexData.Enabled.DISABLED;
	public static boolean justSaved = false;
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
		if (profile != null && client.cameraEntity != null) {
			if (!Objects.equals(previousEnabled,profile.enabled)) {
				ComplexData.reset();
				previousEnabled = profile.enabled;
			}
			ComplexData.update(profile);
		}


		//TODO: Redo KeyBinds!
		if (kb_enable.wasPressed()) {
			ProfileManager.enabled = !ProfileManager.enabled;
		}
		else {
			for (Profile p : ProfileManager.getProfiles()) {
				while (p.keyBinding.wasPressed()) {
					ProfileManager.setActive(p);
					ProfileManager.enabled = true;
				}
				for (Toggle t : p.toggles.values()) {
					while (t.keyBinding.wasPressed())
						t.toggle();
				}
			}
		}

		onProfileChangeOrUpdate();
		CustomHud.justSaved = true;
		saveDelay = 100;
	}

	public static boolean isNotDisabled(DisableElement element) {
		return ProfileManager.getActive() == null || !ProfileManager.getActive().disabled.contains(element);
	}

	private static void updateProfiles() {
		WatchKey key = CustomHud.profileWatcher.poll();
		if (key == null)
			return;
		for (WatchEvent<?> event : key.pollEvents()) {
			if (CustomHud.justSaved) {
				CustomHud.justSaved = false;
				continue;
			}
			Path path = CustomHud.PROFILE_FOLDER.resolve((Path) event.context());
			String fileName = path.getFileName().toString();
			System.out.println("Filename: `" + fileName + "`");
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
				if (profile != null)
					continue;
				else
					ProfileManager.add( Profile.parseProfile(path, fileName) );
			}
			if (event.kind().name().equals("ENTRY_MODIFY")) {
				if (profile == null) {
					System.out.println("CustomHud ENTRY MODIFY: You Don't Exist?");
					continue;
				}
				else {
					profile = Profile.parseProfile(path, fileName);
					ProfileManager.replace(profile);
					if (CLIENT.currentScreen instanceof ErrorsScreen screen)
						screen.changeProfile(profile);
					if (CLIENT.currentScreen instanceof TogglesScreen screen)
						screen.changeProfile(profile);
				}
			}

			LOGGER.info("Updated Profile " + fileName);
			showToast(fileName);
		}

		onProfileChangeOrUpdate();
		key.reset();
	}

	public static void onProfileChangeOrUpdate() {
		FabricLoader.getInstance().getObjectShare().put("customhud:crosshair",
				ProfileManager.getActive() == null ? "normal" : ProfileManager.getActive().crosshair.getName());
	}

	public static void showToast(String profileName) {
		CLIENT.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT,
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


}
