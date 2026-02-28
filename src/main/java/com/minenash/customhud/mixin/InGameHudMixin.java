package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Crosshairs;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InGameHud.class, priority = 900)
public abstract class InGameHudMixin {

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/debug/DebugHudProfile;isEntryVisible(Lnet/minecraft/util/Identifier;)Z"))
    private boolean getDebugCrosshairEnable(boolean original) {
        return ProfileManager.getActive() != null  && ProfileManager.getActive().crosshair == Crosshairs.NONE;
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
    private boolean skipNormalCrosshairRendering0(DrawContext instance, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        return ProfileManager.getActive() == null  || ProfileManager.getActive().crosshair == Crosshairs.NORMAL;
    }
}



