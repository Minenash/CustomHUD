package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.CustomHudRenderer;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextHandler.class)
public class TextHandlerMixin {

    @ModifyArg(method = "getWidth(Ljava/lang/String;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer.font == null ? old : old.withFont(CustomHudRenderer.font);
    }

}
