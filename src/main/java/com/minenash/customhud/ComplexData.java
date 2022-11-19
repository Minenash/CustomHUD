package com.minenash.customhud;

import com.minenash.customhud.mod_compat.CustomHudRegistry;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
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
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.ArrayDeque;
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
    public static BlockState targetBlock = null;
    public static BlockPos targetFluidPos = null;
    public static FluidState targetFluid = null;
    public static String[] sounds = null;
    public static String[] clientChunkCache = null;
    public static int timeOfDay = -1;
    public static double x1 = 0, y1 = 0, z1 = 0, velocityXZ = 0, velocityY = 0, velocityXYZ = 0;

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final BlockState AIR_BLOCK_STATE = Blocks.AIR.getDefaultState();

    //Chunk Data.
    private static ChunkPos pos = null;
    private static CompletableFuture<WorldChunk> chunkFuture;
    private static int velocityWaitCounter = 0;
    private static int cpsWaitCounter = 0;

    public static final CentralProcessor cpu = new SystemInfo().getHardware().getProcessor();
    private static long[] prevTicks = new long[CentralProcessor.TickType.values().length];
    public static double cpuLoad = 0;
    public static double gpuLoad = 0;

    public static int[] clicksSoFar = new int[]{0,0};
    public static int[] clicksPerSeconds = new int[]{0,0};
    public static ArrayDeque<Integer>[] clicks = null;

    public static PerformanceMetrics performanceMetrics = null;

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

        if (profile.enabled.world)
            world = DataFixUtils.orElse(Optional.ofNullable(client.getServer()).flatMap((integratedServer) -> Optional.ofNullable(integratedServer.getWorld(client.world.getRegistryKey()))), client.world);

        if (profile.enabled.targetBlock) {
            HitResult hit =  client.cameraEntity.raycast(profile.targetDistance, 0.0F, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                targetBlockPos = ((BlockHitResult)hit).getBlockPos();
                targetBlock = world.getBlockState(targetBlockPos);
            }
            else {
                targetBlockPos = null;
                targetBlock = AIR_BLOCK_STATE;
            }
        }

        if (profile.enabled.targetFluid) {
            HitResult hit =  client.cameraEntity.raycast(profile.targetDistance, 0.0F, true);

            if (hit.getType() == HitResult.Type.BLOCK) {
                targetFluidPos = ((BlockHitResult)hit).getBlockPos();
                targetFluid = world.getFluidState(targetFluidPos);
            }
            else {
                targetFluidPos = null;
                targetFluid = Fluids.EMPTY.getDefaultState();
            }
        }

        if (profile.enabled.localDifficulty)
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());

        if (profile.enabled.sound)
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");

//      if (profile.enabled.clientChunkCache)
//          clientChunkCache = client.worldRenderer.getChunksDebugString().substring(20).split(", ");

        if (profile.enabled.time) {
            timeOfDay = (int) ((client.world.getTimeOfDay() + 6000) % 24000);
        }

        velocity:
        if (profile.enabled.velocity) {
            if (velocityWaitCounter > 0) {
                velocityWaitCounter--;
                break velocity;
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

        if (profile.enabled.cpu) {
            double load = cpu.getSystemCpuLoadBetweenTicks( prevTicks ) * 100;
            if (load > 0)
                cpuLoad = load;
            prevTicks = cpu.getSystemCpuLoadTicks();
        }

        if (profile.enabled.updateStats) {
            if (System.currentTimeMillis() - lastStatUpdate >= 500) {
                client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
                lastStatUpdate = System.currentTimeMillis();
            }
        }


        if (profile.enabled.clicksPerSeconds) {
            if (clicks == null) {
                clicks = new ArrayDeque[]{new ArrayDeque<Integer>(20), new ArrayDeque<Integer>(20)};
                for (int i = 0; i < 20; i++) {
                    clicks[0].add(0);
                    clicks[1].add(0);
                }
            }

            if (cpsWaitCounter >= 20) {
                System.out.println(System.currentTimeMillis());
                cpsWaitCounter = 0;
            }

            clicks[0].remove();
            clicks[1].remove();
            clicks[0].add(clicksSoFar[0]);
            clicks[1].add(clicksSoFar[1]);
            clicksSoFar[0] = 0;
            clicksSoFar[1] = 0;
            clicksPerSeconds[0] = clicks[0].stream().reduce(0, Integer::sum);
            clicksPerSeconds[1] = clicks[1].stream().reduce(0, Integer::sum);
            cpsWaitCounter++;
        }

        if (profile.enabled.performanceMetrics) {
            performanceMetrics = client.getGame().getPerformanceMetrics();
        }

        CustomHudRegistry.runComplexData();

    }

    static long lastStatUpdate = 0;

    public static void reset() {
        clientChunk = null;
        serverChunk = null;
        serverWorld = null;
        localDifficulty = null;
        world = null;
        sounds = null;
        clientChunkCache = null;
        clicks = null;
        performanceMetrics = null;
        x1 = y1 = z1 = velocityXZ = velocityY = velocityXYZ = 0;
        clicksSoFar[0] = clicksSoFar[1] = 0;
        clicksPerSeconds[0] = clicksPerSeconds[1] = 0;
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
        public boolean targetFluid = false;
        public boolean time = false;
        public boolean velocity = false;
        public boolean cpu = false;
        public boolean gpu = false;
        public boolean updateStats = false;
        public boolean clicksPerSeconds = false;
        public boolean performanceMetrics = false;
//      public boolean clientChunkCache = false;

    }



}
