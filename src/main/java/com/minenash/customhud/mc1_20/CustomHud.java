package com.minenash.customhud.mc1_20;

import com.minenash.customhud.core.ProfileHandler;
import com.minenash.customhud.core.UpdateChecker;
import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.data.Profile;
import com.minenash.customhud.mc1_20.errors.ErrorScreen;
import com.minenash.customhud.core.errors.Errors;
import com.minenash.customhud.mc1_20.mod_compat.BuiltInModCompat;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.*;

import static com.minenash.customhud.core.ProfileHandler.*;

public class CustomHud implements ModInitializer {

	//Debug: LD_PRELOAD=/home/jakob/Programs/renderdoc_1.25/lib/librenderdoc.so
	private static final MinecraftClient client = MinecraftClient.getInstance();

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
		ProfileHandler.setup();
		Variables.registerVars();
		BuiltInModCompat.register();

		UpdateChecker.check( MinecraftClient.getInstance().getGameVersion() );

		HudRenderCallback.EVENT.register(CustomHudRenderer::render);

		ClientTickEvents.END_CLIENT_TICK.register(CustomHud::onTick);
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if (UpdateChecker.updateMessage != null)
				client.getMessageHandler().onGameMessage(UpdateChecker.updateMessage, false);
		});

	}

	private static Enabled previousEnabled = new Enabled();
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
		Profile profile = getActiveProfile();
		if (profile != null && client.cameraEntity != null) {
			if (profile.enabled.equals(previousEnabled)) {
				ComplexData.reset();
				previousEnabled = profile.enabled;
			}
			ComplexData.update(profile);
		}


		if (kb_enable.wasPressed()) {
			enabled = !enabled;
		}
		else if (kb_cycleProfiles.wasPressed()) {
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

		onProfileChangeOrUpdate();
		CustomHud.justSaved = true;
		saveDelay = 100;
	}



	private static void updateProfiles() {
		WatchKey key = profileWatcher.poll();
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
			Path changed = CONFIG_FOLDER.resolve((Path) event.context());
			Path original = profile == 0 ? CONFIG : getProfilePath(profile);
			try {
				if (Files.exists(changed) && Files.isSameFile(changed, original)) {
					if (profile == 0) {
						LOGGER.info("Reloading Config");
						loadConfig();
					}
					else {
						profiles[profile - 1] = Profile.parseProfile(original, profile);
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

		onProfileChangeOrUpdate();
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
