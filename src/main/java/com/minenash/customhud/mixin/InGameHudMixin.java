package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Crosshairs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(value = InGameHud.class, priority = 900)
public abstract class InGameHudMixin {

    @Shadow protected abstract void renderCrosshair(DrawContext context, RenderTickCounter tickCounter);

    @Shadow @Final private MinecraftClient client;
    @Unique boolean renderAttackIndicator = false;

    @Inject(method = "renderCrosshair", at = @At(value = "TAIL"))
    private void renderAttackIndicatorForDebugScreen2(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!renderAttackIndicator && getCrosshair() == Crosshairs.DEBUG && MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
            renderAttackIndicator = true;
            renderCrosshair(context, tickCounter);
            renderAttackIndicator = false;
        }
    }

    @ModifyExpressionValue(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    private boolean getDebugCrosshairEnable(boolean original) {
        return client.getDebugHud().shouldShowDebugHud() || ( !renderAttackIndicator && getCrosshair() == Crosshairs.DEBUG);
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 0))
    private boolean skipNormalCrosshairRendering0(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        return !renderAttackIndicator && getCrosshair() != Crosshairs.NONE;
    }
    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 1))
    private boolean skipNormalCrosshairRendering1(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        return renderAttackIndicator || getCrosshair() != Crosshairs.NONE;
    }
    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V", ordinal = 2))
    private boolean skipNormalCrosshairRendering2(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
        return renderAttackIndicator || getCrosshair() != Crosshairs.NONE;
    }

    @WrapWithCondition(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIIIIIII)V"))
    private boolean skipNormalCrosshairRendering3(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int textureWidth, int textureHeight, int u, int v, int x, int y, int width, int height) {
        return renderAttackIndicator || getCrosshair() != Crosshairs.NONE;
    }

    @Unique
    private static Crosshairs getCrosshair() {
        return ProfileManager.getActive() == null ? Crosshairs.NORMAL : ProfileManager.getActive().crosshair;
    }

}


