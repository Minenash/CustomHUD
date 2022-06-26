package com.minenash.customhud.mixin;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.CustomHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Recorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/Math;round(D)J"))
    private long getGPUUtilisation(double gpuUsage) {
        ComplexData.gpuLoad = gpuUsage;
        return Math.round(gpuUsage);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Recorder;isActive()Z"))
    private boolean enableGPU(Recorder recorder) {
        return recorder.isActive() || (CustomHud.getActiveProfile() != null && CustomHud.getActiveProfile().enabled.gpu);
    }

}
