package com.minenash.customhud.mixin.accessors;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {

    @Accessor MultiValueDebugSampleLogImpl getFrameNanosLog();
    @Accessor MultiValueDebugSampleLogImpl getTickNanosLog();

}
