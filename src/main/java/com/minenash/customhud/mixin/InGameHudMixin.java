package com.minenash.customhud.mixin;

import com.minenash.customhud.render.CustomHudRenderer2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    public void renderCustomHud(MatrixStack matrix, float _timeDelta, CallbackInfo _info) {
        if (!MinecraftClient.getInstance().options.debugEnabled)
            CustomHudRenderer2.render(matrix);
    }

}
