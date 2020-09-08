package com.minenash.customhud;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ComplexData {

    public static WorldChunk clientChunk = null;
    public static WorldChunk serverChunk = null;
    public static ServerWorld serverWorld = null;
    public static LocalDifficulty localDifficulty = null;
    public static World world = null;
    public static BlockPos targetBlockPos = null;
    public static String[] sounds = null;
    public static String[] clientChunkCache = null;
    public static int timeOfDay = -1;

    private static final MinecraftClient client = MinecraftClient.getInstance();

    //Chunk Data.
    private static ChunkPos pos = null;
    private static CompletableFuture<WorldChunk> chunkFuture;

    @SuppressWarnings("ConstantConditions")
    public static void update(Profile profile) {

        if (profile.enabled.serverWorld) {
            IntegratedServer integratedServer = client.getServer();
            serverWorld = integratedServer != null ? integratedServer.getWorld(client.world.getRegistryKey()) : null;
        }

        if (profile.enabled.clientChunk) {
            ChunkPos newPos = new ChunkPos(client.getCameraEntity().getBlockPos());
            if (!Objects.equals(ComplexData.pos,newPos)) {
                pos = newPos;
                chunkFuture = null;
                clientChunk = null;
            }
            if (clientChunk == null)
                clientChunk = client.world.getChunk(pos.x, pos.z);
        }

        if (profile.enabled.serverChunk) {
            if (chunkFuture == null) {
                if (serverWorld != null)
                    chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(pos.x, pos.z, ChunkStatus.FULL, false).thenApply((either) -> either.map((chunk) -> (WorldChunk)chunk, (unloaded) -> null));

                if (chunkFuture == null)
                    chunkFuture = CompletableFuture.completedFuture(clientChunk);
            }
            serverChunk = chunkFuture.getNow(null);
        }

        if (profile.enabled.targetBlock) {
            HitResult hit =  client.cameraEntity.rayTrace(profile.targetDistance, 0.0F, false);
            targetBlockPos = hit.getType() == HitResult.Type.BLOCK ? ((BlockHitResult)hit).getBlockPos() : null;
        }

        if (profile.enabled.world)
            world = DataFixUtils.orElse(Optional.ofNullable(client.getServer()).flatMap((integratedServer) -> Optional.ofNullable(integratedServer.getWorld(client.world.getRegistryKey()))), client.world);

        if (profile.enabled.localDifficulty)
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());

        if (profile.enabled.sound)
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");

        if (profile.enabled.clientChunkCache)
            clientChunkCache = client.world.getDebugString().substring(20).split(", ");

        if (profile.enabled.time) {
            timeOfDay = (int) (client.world.getTimeOfDay() % 24000) - 6000;
            if (timeOfDay < 0)
                timeOfDay = 24000 - (-1 * timeOfDay);
        }

    }

    public static void reset() {
        clientChunk = null;
        serverChunk = null;
        serverWorld = null;
        localDifficulty = null;
        world = null;
        targetBlockPos = null;
        sounds = null;
        clientChunkCache = null;
    }

    public static class Enabled {
        public static final Enabled DISABLED = new Enabled();
        public boolean clientChunk = false;
        public boolean serverChunk = false;
        public boolean serverWorld = false;
        public boolean localDifficulty = false;
        public boolean world = false;
        public boolean sound = false;
        public boolean targetBlock = false;
        public boolean clientChunkCache = false;
        public boolean time = false;

    }



}
