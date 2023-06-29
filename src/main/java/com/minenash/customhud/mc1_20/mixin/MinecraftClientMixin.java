package com.minenash.customhud.mc1_20.mixin;

import com.minenash.customhud.core.ProfileHandler;
import com.minenash.customhud.mc1_20.ComplexData;
import com.minenash.customhud.core.data.Enabled;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

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
        ProfileHandler.loadProfiles();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Ljava/lang/String;format(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
    public void getGpuUsage(boolean tick, CallbackInfo ci) {
        ComplexData.gpuUsage = gpuUtilizationPercentage > 100 ? 100 : gpuUtilizationPercentage;
    }


    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;debugEnabled:Z"))
    public boolean getGpuUsageAndOtherPerformanceMetrics(GameOptions instance) {
        return instance.debugEnabled || (ProfileHandler.getActiveProfile() != null && ProfileHandler.getActiveProfile().enabled.has(Enabled.PERFORMANCE_METRICS) );
    }

}
