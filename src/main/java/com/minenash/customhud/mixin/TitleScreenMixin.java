package com.minenash.customhud.mixin;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.errors.Errors;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Unique int count = 0;

    @Inject(method = "init", at = @At("RETURN"))
    public void showToasts(CallbackInfo ci) {
        if (count == 1)
            for (Profile profile : ProfileManager.getProfiles())
                if (Errors.hasErrors(profile.name))
                    CustomHud.showToast(profile.name);
        count++;
    }

}
