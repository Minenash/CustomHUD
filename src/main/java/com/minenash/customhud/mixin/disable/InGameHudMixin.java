package com.minenash.customhud.mixin.disable;

import com.minenash.customhud.CustomHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.minenash.customhud.data.DisableElement.*;

@Mixin(value = InGameHud.class, priority = 10000)
public abstract class InGameHudMixin {

    //TODO: Less Invasive?

    @Shadow protected abstract void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking);
    @Shadow protected abstract void renderMountHealth(DrawContext context);
    @Shadow protected abstract void renderStatusEffectOverlay(DrawContext context);
    @Shadow protected abstract void renderScoreboardSidebar(DrawContext context, ScoreboardObjective objective);
    @Shadow protected abstract void renderHotbar(float tickDelta, DrawContext context);

    //TODO: Definitely Move
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbar(FLnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableHotbar(InGameHud instance, float tickDelta, DrawContext context) {
        if (CustomHud.isNotDisabled(HOTBAR))
            renderHotbar(tickDelta, context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BossBarHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableBossBar(BossBarHud instance, DrawContext context) {
        if (CustomHud.isNotDisabled(BOSSBARS))
            instance.render(context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasStatusBars()Z"))
    public boolean customhud$disableStatusBars(ClientPlayerInteractionManager instance) {
        return instance.hasStatusBars() && CustomHud.isNotDisabled(STATUS_BARS);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableArmor1(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(ARMOR))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 1, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableArmor2(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(ARMOR))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableArmor3(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(ARMOR))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
    public void customhud$disableHealthBar(InGameHud instance, DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking) {
        if (CustomHud.isNotDisabled(HEALTH))
            renderHealthBar(context, player, x, y, lines, regeneratingHeartIndex, maxHealth, lastHealth, health, absorption, blinking);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableHunger1(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(HUNGER))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 4, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableHunger2(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(HUNGER))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 5, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableHunger3(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(HUNGER))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 6, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableAir1(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(AIR))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 7, target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"))
    public void customhud$disableAir2(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (CustomHud.isNotDisabled(AIR))
            instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableHorseHealth(InGameHud instance, DrawContext context) {
        if (CustomHud.isNotDisabled(HORSE) && CustomHud.isNotDisabled(HORSE_HEALTH) && CustomHud.isNotDisabled(STATUS_BARS))
            renderMountHealth(context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/entity/JumpingMount;Lnet/minecraft/client/gui/DrawContext;I)V"))
    public void customhud$disableHorseJump(InGameHud instance, JumpingMount mount, DrawContext context, int x) {
        if (CustomHud.isNotDisabled(HORSE) && CustomHud.isNotDisabled(HORSE_JUMP))
            instance.renderMountJumpBar(mount, context, x);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;hasExperienceBar()Z"))
    public boolean customhud$disableXPBar(ClientPlayerInteractionManager instance) {
        return instance.hasExperienceBar() && CustomHud.isNotDisabled(XP);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHeldItemTooltip(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableHotbar(InGameHud instance, DrawContext context) {
        if (CustomHud.isNotDisabled(HOTBAR))
            instance.renderHeldItemTooltip(context);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableStatusEffects(InGameHud instance, DrawContext context) {
        if (CustomHud.isNotDisabled(STATUS_EFFECTS))
            renderStatusEffectOverlay(context);
    }

    //TODO: Definitely Move
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SubtitlesHud;render(Lnet/minecraft/client/gui/DrawContext;)V"))
    public void customhud$disableSubtitles(SubtitlesHud instance, DrawContext context) {
        if (CustomHud.isNotDisabled(SUBTITLES))
            instance.render(context);
    }

    //TODO: Double Check
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderScoreboardSidebar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/scoreboard/ScoreboardObjective;)V"))
    public void customhud$disableScoreboard(InGameHud instance, DrawContext context, ScoreboardObjective objective) {
        if (CustomHud.isNotDisabled(SCOREBOARD))
            renderScoreboardSidebar(context, objective);
    }

    //TODO: Probably Move
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/DrawContext;III)V"))
    public void customhud$disableChat(ChatHud instance, DrawContext context, int currentTick, int mouseX, int mouseY) {
        if (CustomHud.isNotDisabled(CHAT))
            instance.render(context, currentTick, mouseX, mouseY);
    }

}
