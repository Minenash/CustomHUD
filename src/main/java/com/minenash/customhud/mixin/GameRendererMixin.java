package com.minenash.customhud.mixin;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static com.minenash.customhud.CustomHud.CLIENT;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    //@Unique private double originalScale = 1;
//
    //@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;F)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    //public void setHudGuiScale(float tickDelta, long startTime, boolean tick2, CallbackInfo ci, boolean tick, boolean bl, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, DrawContext drawContext)  {
    //    Profile p = ProfileManager.getActive();
    //    if (p != null && p.baseTheme.hudScale != null) {
    //        originalScale = CLIENT.getWindow().getScaleFactor();
    //        double target = p.baseTheme.getTargetGuiScale();
    //        CLIENT.getWindow().setScaleFactor(target);
    //        float scale = (float) (originalScale / target);
    //        drawContext.getMatrices().push();
    //        drawContext.getMatrices().scale(scale, scale, 1);
    //    }
    //}
//
    //@Inject(method = "render", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;F)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    //public void resetHudGuiScale(float tickDelta, long startTime, boolean tick2, CallbackInfo ci, boolean tick, boolean bl, int i, int j, Window window, Matrix4f matrix4f, MatrixStack matrixStack, DrawContext drawContext)  {
    //    Profile p = ProfileManager.getActive();
    //    if (p != null && p.baseTheme.hudScale != null) {
    //        CLIENT.getWindow().setScaleFactor(originalScale);
    //        drawContext.getMatrices().pop();
    //    }
    //}

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;render(Lnet/minecraft/client/gui/DrawContext;F)V"))
    public void changeHudGuiScale(InGameHud instance, DrawContext context, float tickDelta) {
        Profile p = ProfileManager.getActive();
        if (p == null || p.baseTheme.hudScale == null) {
            instance.render(context, tickDelta);
            return;
        }

        double originalScale = CLIENT.getWindow().getScaleFactor();
        double target = p.baseTheme.getTargetGuiScale();
        float scale = (float) (target/originalScale);
        CLIENT.getWindow().setScaleFactor(target);

        context.getMatrices().push();
        context.getMatrices().scale(scale, scale, 1);
        instance.render(context, tickDelta);
        context.getMatrices().pop();

        CLIENT.getWindow().setScaleFactor(originalScale);


    }

}
