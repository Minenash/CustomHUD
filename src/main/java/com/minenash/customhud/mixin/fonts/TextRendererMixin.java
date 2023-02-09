package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.ducks.CustomHudTextRendererExtention;
import com.minenash.customhud.render.CustomHudRenderer2;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TextRenderer.class)
public class TextRendererMixin implements CustomHudTextRendererExtention {

    @ModifyArg(method = "drawLayer(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer2.font == null ? old : old.withFont(CustomHudRenderer2.font);
    }

    private VertexConsumerProvider.Immediate owo$labelVertexConsumers = null;

    @Override
    public void customHud$beginCache() {
        this.owo$labelVertexConsumers = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
    }

    @ModifyArg(method = "draw(Ljava/lang/String;FFILnet/minecraft/util/math/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I"))
    private VertexConsumerProvider injectConsumers(VertexConsumerProvider immediate) {
        if (this.owo$labelVertexConsumers == null) return immediate;
        return this.owo$labelVertexConsumers;
    }

    @ModifyArg(method = "draw(Lnet/minecraft/text/OrderedText;FFILnet/minecraft/util/math/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I"))
    private VertexConsumerProvider injectConsumers2(VertexConsumerProvider immediate) {
        if (this.owo$labelVertexConsumers == null) return immediate;
        return this.owo$labelVertexConsumers;
    }

    @Inject(method = "draw(Ljava/lang/String;FFILnet/minecraft/util/math/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZIIZ)I", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void skipDraw(String text, float x, float y, int color, Matrix4f matrix, boolean shadow, boolean mirror, CallbackInfoReturnable<Integer> cir, VertexConsumerProvider.Immediate immediate, int i) {
        if (this.owo$labelVertexConsumers == null) return;
        cir.setReturnValue(i);
    }

    @Inject(method = "draw(Lnet/minecraft/text/OrderedText;FFILnet/minecraft/util/math/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/OrderedText;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)I", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void skipDraw2(OrderedText text, float x, float y, int color, Matrix4f matrix, boolean shadow, CallbackInfoReturnable<Integer> cir, VertexConsumerProvider.Immediate immediate, int i) {
        if (this.owo$labelVertexConsumers == null) return;
        cir.setReturnValue(i);
    }

    @Override
    public void customHud$submitCache() {
        this.owo$labelVertexConsumers.draw();
        this.owo$labelVertexConsumers = null;
    }

}
