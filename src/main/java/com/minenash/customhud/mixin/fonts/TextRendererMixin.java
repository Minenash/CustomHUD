package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.ducks.CustomHudTextRendererExtention;
import com.minenash.customhud.render.CustomHudRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextRenderer.class)
public class TextRendererMixin implements CustomHudTextRendererExtention {

    @ModifyArg(method = "drawLayer(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer.font == null ? old : old.withFont(CustomHudRenderer.font);
    }

    private VertexConsumerProvider.Immediate owo$labelVertexConsumers = null;

    @Override
    public void customHud$beginCache() {
        this.owo$labelVertexConsumers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
    }

    @ModifyArg(method = "draw(Ljava/lang/String;FFILorg/joml/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I"))
    private VertexConsumerProvider customHud$injectConsumers(VertexConsumerProvider immediate) {
        if (this.owo$labelVertexConsumers == null) return immediate;
        return this.owo$labelVertexConsumers;
    }

    @ModifyArg(method = "draw(Lnet/minecraft/text/OrderedText;FFILorg/joml/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private VertexConsumerProvider customHud$injectConsumers2(VertexConsumerProvider immediate) {
        if (this.owo$labelVertexConsumers == null) return immediate;
        return this.owo$labelVertexConsumers;
    }

    @Inject(method = "draw(Ljava/lang/String;FFILorg/joml/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void customHud$skipDraw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow, boolean mirror, CallbackInfoReturnable<Integer> cir, VertexConsumerProvider.Immediate immediate, int i) {
        if (this.owo$labelVertexConsumers == null) return;
        cir.setReturnValue(i);
    }

    @Inject(method = "draw(Lnet/minecraft/text/OrderedText;FFILorg/joml/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void customHud$skipDraw2(OrderedText text, float x, float y, int color, Matrix4f matrix, boolean shadow, CallbackInfoReturnable<Integer> cir, VertexConsumerProvider.Immediate immediate, int i) {
        if (this.owo$labelVertexConsumers == null) return;
        cir.setReturnValue(i);
    }

    @Override
    public void customHud$submitCache() {
        this.owo$labelVertexConsumers.draw();
        this.owo$labelVertexConsumers = null;
    }

}
