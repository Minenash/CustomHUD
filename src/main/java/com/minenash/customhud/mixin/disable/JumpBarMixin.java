package com.minenash.customhud.mixin.disable;

import com.minenash.customhud.CustomHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.JumpBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minenash.customhud.data.DisableElement.*;

@Mixin(JumpBar.class)
public class JumpBarMixin {
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    public void customhud$disableHorseJump(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(HORSE) || CustomHud.isDisabled(HORSE_JUMP))
            ci.cancel();
    }
}
