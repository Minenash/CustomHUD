package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.CustomHudRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyArg(method = "drawLayer(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer.font == null ? old : old.withFont(CustomHudRenderer.font);
    }

    @Redirect(method = "draw(Ljava/lang/String;FFILnet/minecraft/util/math/Matrix4f;ZZ)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"))
    public void noRender(VertexConsumerProvider.Immediate instance) {
        if (!CustomHudRenderer.batch)
            instance.draw();
    }

    @Redirect(method = "draw(Lnet/minecraft/text/OrderedText;FFILnet/minecraft/util/math/Matrix4f;Z)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;draw()V"))
    public void noRender2(VertexConsumerProvider.Immediate instance) {
        if (!CustomHudRenderer.batch)
            instance.draw();
    }

}
