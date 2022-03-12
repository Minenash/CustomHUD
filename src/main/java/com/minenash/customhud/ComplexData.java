package com.minenash.customhud;

import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
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
    public static Block targetBlock = null;
    public static String[] sounds = null;
    public static String[] clientChunkCache = null;
    public static int timeOfDay = -1;
    public static double x1 = 0, y1 = 0, z1 = 0, velocityXZ = 0, velocityY = 0, velocityXYZ = 0;

    //Not really complex, but not sure where else to put it
    public static String address = "";

    private static final MinecraftClient client = MinecraftClient.getInstance();

    //Chunk Data.
    private static ChunkPos pos = null;
    private static CompletableFuture<WorldChunk> chunkFuture;
    private static int velocityWaitCounter = 0;

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
            HitResult hit =  client.cameraEntity.raycast(profile.targetDistance, 0.0F, false);
            targetBlockPos = hit.getType() == HitResult.Type.BLOCK ? ((BlockHitResult)hit).getBlockPos() : null;
            targetBlock = world.getBlockState(targetBlockPos).getBlock();
        }

        if (profile.enabled.world)
            world = DataFixUtils.orElse(Optional.ofNullable(client.getServer()).flatMap((integratedServer) -> Optional.ofNullable(integratedServer.getWorld(client.world.getRegistryKey()))), client.world);

        if (profile.enabled.localDifficulty)
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());

        if (profile.enabled.sound)
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");

        if (profile.enabled.clientChunkCache)
            clientChunkCache = client.worldRenderer.getChunksDebugString().substring(20).split(", ");

        if (profile.enabled.time) {
            timeOfDay = (int) ((client.world.getTimeOfDay() + 6000) % 24000);
        }

        if (profile.enabled.velocity) {
            if (velocityWaitCounter > 0) {
                velocityWaitCounter--;
                return;
            }
            velocityWaitCounter = 4;
            ClientPlayerEntity p = client.player;
            final double changeXZ = Math.sqrt(Math.pow(Math.abs(p.getX() - x1), 2) + Math.pow(Math.abs(p.getZ() - z1), 2));
            final double changeY = Math.abs(p.getY() - y1);
            final double changeXYZ = Math.sqrt(changeXZ*changeXZ + changeY*changeY);
            x1 = p.getX();
            y1 = p.getY();
            z1 = p.getZ();
            velocityXZ = ((int)(changeXZ*40))/10.0;
            velocityY = ((int)(changeY*40))/10.0;
            velocityXYZ = ((int)(changeXYZ*40))/10.0;
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
        x1 = y1 = z1 = velocityXZ = velocityY = velocityXYZ = 0;
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
        public boolean velocity = false;

    }



}
