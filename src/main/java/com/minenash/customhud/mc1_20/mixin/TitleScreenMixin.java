package com.minenash.customhud.mc1_20.mixin;

import com.minenash.customhud.mc1_20.CustomHud;
import com.minenash.customhud.core.errors.Errors;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    int count = 0;

    @Inject(method = "init", at = @At("RETURN"))
    public void showToasts(CallbackInfo ci) {
        if (count == 1)
            for (int i = 1; i <= 3; i++)
                if (Errors.hasErrors(i))
                    CustomHud.showToast(i, true);
        count++;
    }

}
