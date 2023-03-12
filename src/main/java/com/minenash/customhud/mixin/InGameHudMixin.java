package com.minenash.customhud.mixin;

import com.minenash.customhud.CustomHud;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow protected abstract void renderCrosshair(MatrixStack matrices);

    boolean renderAttackIndicator = false;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V", shift = At.Shift.AFTER))
    private void renderAttackIndicatorForDebugScreen2(MatrixStack stack, float _tickDelta, CallbackInfo _info) {
        if (CustomHud.INDEPENDENT_GIZMO_INSTALLED && MinecraftClient.getInstance().options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
            renderAttackIndicator = true;
            renderCrosshair(stack);
            renderAttackIndicator = false;
        }
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z", opcode = Opcodes.GETFIELD))
    private boolean getDebugCrosshairEnable(GameOptions options) {
        return !renderAttackIndicator && (Boolean) FabricLoader.getInstance().getObjectShare().get("independent_gizmo:enable");
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0,target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    private void skipNormalCrosshairRendering(MatrixStack stack, int x, int y, int u, int v, int width, int height) {
        if (!renderAttackIndicator)
            DrawableHelper.drawTexture(stack, x, y, u, v, width, height);
    }

}
