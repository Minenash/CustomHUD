package com.minenash.customhud.mixin;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
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
        System.out.println(key.getCode() + " " + key.getTranslationKey());
        for (Profile p : ProfileManager.getProfiles()) {
            if (p.keyBinding.matchesKey(key.getCode(), key.getCode()))
                ++p.keyBinding.timesPressed;
            for (Toggle t : p.toggles.values()) {
                if (t.keyBinding.matchesKey(key.getCode(), key.getCode()))
                    ++t.keyBinding.timesPressed;
            }
        }

    }

}
