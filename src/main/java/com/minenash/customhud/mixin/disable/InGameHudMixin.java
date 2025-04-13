package com.minenash.customhud.mixin.disable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minenash.customhud.CustomHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minenash.customhud.data.DisableElement.*;

@Mixin(value = InGameHud.class, priority = 10000)
public abstract class InGameHudMixin {

    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    public void customhud$disableHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(HOTBAR))
            ci.cancel();
    }
    @Inject(method = "method_55808", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableBossBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(BOSSBARS))
            ci.cancel();
    }

    @Inject(method = "renderStatusBars", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableStatusBars(DrawContext context, CallbackInfo ci) {
        if (CustomHud.isDisabled(STATUS_BARS))
            ci.cancel();
    }

    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderArmor(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIII)V"))
    public void customhud$disableArmor(DrawContext context, PlayerEntity player, int i, int j, int k, int x, Operation<Void> original) {
        if (CustomHud.isNotDisabled(ARMOR))
            original.call(context, player, i, j, k, x);
    }

    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    public void customhud$disableHealthBar(InGameHud instance, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, Operation<Void> original) {
        if (CustomHud.isNotDisabled(HEALTH))
            original.call(instance, context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderFood(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;II)V"))
    public void customhud$disableHunger(InGameHud instance, DrawContext context, PlayerEntity player, int top, int right, Operation<Void> original) {
        if (CustomHud.isNotDisabled(HUNGER))
            original.call(instance, context, player, top, right);
    }

    @WrapOperation(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderAirBubbles(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;III)V"))
    public void customhud$disableAir(InGameHud instance, DrawContext context, PlayerEntity player, int heartCount, int top, int left, Operation<Void> original) {
        if (CustomHud.isNotDisabled(AIR))
            original.call(instance, context, player, heartCount, top, left);
    }

    @Inject(method = "renderMountHealth", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableHorseHealth(DrawContext context, CallbackInfo ci) {
        if (CustomHud.isDisabled(HORSE) || CustomHud.isDisabled(HORSE_HEALTH) || CustomHud.isDisabled(STATUS_BARS))
            ci.cancel();
    }

    @Inject(method = "renderMountJumpBar", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableHorseJump(JumpingMount mount, DrawContext context, int x, CallbackInfo ci) {
        if (CustomHud.isDisabled(HORSE) || CustomHud.isDisabled(HORSE_JUMP))
            ci.cancel();
    }

    @Inject(method = "renderExperienceBar", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableXPBar(DrawContext context, int x, CallbackInfo ci) {
        if (CustomHud.isDisabled(XP))
            ci.cancel();
    }

    @Inject(method = "renderExperienceLevel", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableXPLvl(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(XP))
            ci.cancel();
    }

    @Inject(method = "renderHeldItemTooltip", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableHotbar(DrawContext context, CallbackInfo ci) {
        if (CustomHud.isDisabled(ITEM_TOOLTIP))
            ci.cancel();
    }

    @Inject(method = "renderStatusEffectOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableStatusEffects(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(STATUS_EFFECTS))
            ci.cancel();
    }

    @Inject(method = "method_55806", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableSubtitles(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(SUBTITLES))
            ci.cancel();
    }

    @Inject(method = "renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableScoreboard(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(SCOREBOARD))
            ci.cancel();
    }

    @Inject(method = "renderChat", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableChat(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(CHAT))
            ci.cancel();
    }

    @Inject(method = "renderTitleAndSubtitle", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableTitles(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(TITLES))
            ci.cancel();
    }

    @Inject(method = "renderOverlayMessage", at = @At(value = "HEAD"), cancellable = true)
    public void customhud$disableActionbarMsg(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (CustomHud.isDisabled(ACTIONBAR))
            ci.cancel();
    }

}
