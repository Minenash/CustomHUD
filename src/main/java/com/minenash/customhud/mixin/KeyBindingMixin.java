package com.minenash.customhud.mixin;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import net.minecraft.client.input.KeyInput;
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
        var input = new KeyInput(key.getCode(), key.getCode(), 0);
        for (Profile p : ProfileManager.getProfiles()) {
            if (p.keyBinding.matchesKey(input))
                ++p.keyBinding.timesPressed;
        }
        Profile activeProfile = ProfileManager.getActive();
        if (activeProfile != null) {
            for (Toggle t : activeProfile.toggles.values()) {
                if (t.key.matchesKey(input))
                    ++t.key.timesPressed;
            }
        }

    }

}
