package com.minenash.customhud.mixin.fonts;

import com.minenash.customhud.render.CustomHudRenderer2;
import net.minecraft.client.font.TextHandler;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(TextHandler.class)
public class TextHandlerMixin {

    @ModifyArg(method = "getWidth(Ljava/lang/String;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"), index = 1)
    public Style changeStyle(Style old) {
        return CustomHudRenderer2.font == null ? old : old.withFont(CustomHudRenderer2.font);
    }

}
