package com.minenash.customhud.mixin;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.CustomHud;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.profiler.Recorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow @Final public GameOptions options;
    @Shadow private double gpuUtilizationPercentage;

    @Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;wasPressed()Z"))
    public boolean readClick(KeyBinding instance) {
        boolean p = instance.wasPressed();

        if (p && instance == options.attackKey)
            ComplexData.clicksSoFar[0]++;
        if (p && instance == options.useKey)
            ComplexData.clicksSoFar[1]++;

        return p;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(RunArgs args, CallbackInfo ci) {
        CustomHud.loadProfiles();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    public void getGpuUsage(boolean tick, CallbackInfo ci) {
        ComplexData.gpuUsage = gpuUtilizationPercentage;
    }


    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    public boolean getGpuUsageAndOtherPerformanceMetrics(GameOptions instance) {
        return CustomHud.getActiveProfile().enabled.performanceMetrics || instance.debugEnabled;
    }

}
