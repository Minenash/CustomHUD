package com.minenash.customhud.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Queue;

@Environment(EnvType.CLIENT)
@Mixin(ChunkBuilder.class)
public interface ChunkBuilderAccess {
    @Accessor int getQueuedTaskCount();
    @Accessor int getBufferCount();
    @Accessor Queue<Runnable> getUploadQueue();
}
