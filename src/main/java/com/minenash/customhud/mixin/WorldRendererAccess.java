package com.minenash.customhud.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BuiltChunkStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public interface WorldRendererAccess {
    @Accessor BuiltChunkStorage getChunks();
    @Invoker(value = "getCompletedChunkCount") int getCompletedChunks();
    @Accessor ChunkBuilder getChunkBuilder();

    @Accessor int getRegularEntityCount();
}
