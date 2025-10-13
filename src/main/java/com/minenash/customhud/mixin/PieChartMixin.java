package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.DebugCharts;
import com.minenash.customhud.data.Profile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.chart.PieChart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minenash.customhud.CustomHud.CLIENT;

@Mixin(PieChart.class)
public class PieChartMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    public int moveProfilerToLeft(DrawContext instance, Operation<Integer> original) {
        Profile p = ProfileManager.getActive();
        return p != null && p.leftChart == DebugCharts.PROFILER ? 360 : original.call(instance);
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    private void shouldRenderTheActualProfiler(DrawContext context, CallbackInfo ci) {
        Profile p = ProfileManager.getActive();
        if (CLIENT.inGameHud.getDebugHud().shouldShowDebugHud() ||
                (!CLIENT.options.hudHidden && !CLIENT.inGameHud.getDebugHud().shouldShowDebugHud() && CLIENT.world != null
                        && p != null && (p.leftChart == DebugCharts.PROFILER || p.rightChart == DebugCharts.PROFILER)) )
            return;
        ci.cancel();
    }

}
