package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.DebugCharts;
import com.minenash.customhud.data.Profile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.PieChart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PieChart.class)
public class PieChartMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    public int moveProfilerToLeft(DrawContext instance, Operation<Integer> original) {
        Profile p = ProfileManager.getActive();
        return p != null && p.leftChart == DebugCharts.PROFILER ? 360 : original.call(instance);
    }

}
