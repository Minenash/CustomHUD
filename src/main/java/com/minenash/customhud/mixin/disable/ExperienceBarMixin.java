package com.minenash.customhud.mixin.disable;

import com.minenash.customhud.CustomHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minenash.customhud.data.DisableElement.XP;

@Mixin(ExperienceBar.class)
public class ExperienceBarMixin {
    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    public void customhud$disableXPBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(XP))
            ci.cancel();
    }
}
