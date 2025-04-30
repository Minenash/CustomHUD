package com.minenash.customhud.mixin;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.ProfileOption;
import com.minenash.customhud.data.Toggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyBinding.class)
public class KeyBindingMixin {

    @Inject(method = "onKeyPressed", at = @At("TAIL"))
    private static void checkKeybinds(InputUtil.Key key, CallbackInfo ci) {
        for (Profile p : ProfileManager.getProfiles()) {
            if (p.keyBinding.matchesKey(key.getCode(), key.getCode()))
                ++p.keyBinding.timesPressed;
            for (Toggle t : p.toggles.values()) {
                if (t.key.matchesKey(key.getCode(), key.getCode()))
                    ++t.key.timesPressed;
            }
            for (ProfileOption o : p.options.values())
                if (o.value instanceof KeyBinding kb)
                      if (kb.boundKey.equals(key))
                        ++kb.timesPressed;
        }
    }

    @Inject(method = "setKeyPressed", at = @At("TAIL"))
    private static void checkHoldKeybinds(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
        for (Profile p : ProfileManager.getProfiles())
            for (ProfileOption o : p.options.values())
                if (o.value instanceof KeyBinding kb && kb.boundKey.equals(key))
                    kb.setPressed(pressed);
    }

}
