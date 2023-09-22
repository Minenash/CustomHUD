package com.minenash.customhud.mixin;

import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.profiler.PerformanceLog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {

    @Accessor PerformanceLog getFrameNanosLog();
    @Accessor PerformanceLog getTickNanosLog();

}
