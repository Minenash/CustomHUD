package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.render.CustomHudRenderer3;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextRenderer.class)
public class TextRendererMixin {

    @ModifyArg(method = "prepare(Ljava/lang/String;FFIZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"))
    public Style changeStyle(Style old) {
        return CustomHudRenderer3.font == null ? old : old.withFont(CustomHudRenderer3.font);
    }

}
