package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.CustomHudRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyArg(method = "drawLayer(Ljava/lang/String;FFIZLnet/minecraft/util/math/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;ZII)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer.font == null ? old : old.withFont(CustomHudRenderer.font);
    }

}
