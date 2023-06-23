package com.minenash.customhud.mc1_20;

import com.minenash.customhud.core.data.Profile;
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

import static com.minenash.customhud.core.data.Enabled.*;

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

    public static double gpuUsage = 0;

    public static int[] clicksSoFar = new int[]{0,0};
    public static int[] clicksPerSeconds = new int[]{0,0};
    public static ArrayDeque<Integer>[] clicks = null;

    public static double[] performanceMetrics = new double[3];

    @SuppressWarnings("ConstantConditions")
    public static void update(Profile profile) {
        if (profile.enabled.has(SERVER_WORLD)) {
            IntegratedServer integratedServer = client.getServer();
            serverWorld = integratedServer != null ? integratedServer.getWorld(client.world.getRegistryKey()) : null;
        }

        if (profile.enabled.has(CLIENT_CHUNK)) {
            ChunkPos newPos = new ChunkPos(client.getCameraEntity().getBlockPos());
            if (!Objects.equals(ComplexData.pos,newPos)) {
                pos = newPos;
                chunkFuture = null;
                clientChunk = null;
            }
            if (clientChunk == null)
                clientChunk = client.world.getChunk(pos.x, pos.z);
        }

        if (profile.enabled.has(SERVER_CHUNK)) {
            if (chunkFuture == null) {
                if (serverWorld != null)
                    chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(pos.x, pos.z, ChunkStatus.FULL, false).thenApply((either) -> either.map((chunk) -> (WorldChunk)chunk, (unloaded) -> null));

                if (chunkFuture == null)
                    chunkFuture = CompletableFuture.completedFuture(clientChunk);
            }
            serverChunk = chunkFuture.getNow(null);
        }

        if (profile.enabled.has(WORLD))
            world = DataFixUtils.orElse(Optional.ofNullable(client.getServer()).flatMap((integratedServer) -> Optional.ofNullable(integratedServer.getWorld(client.world.getRegistryKey()))), client.world);

        if (profile.enabled.has(TARGET_BLOCK)) {
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

        if (profile.enabled.has(TARGET_FLUID)) {
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

        if (profile.enabled.has(LOCAL_DIFFICULTY))
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());

        if (profile.enabled.has(SOUND))
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");

        if (profile.enabled.has(TIME)) {
            timeOfDay = (int) ((client.world.getTimeOfDay() + 6000) % 24000);
        }

        velocity:
        if (profile.enabled.has(VELOCITY)) {
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
            velocityXZ = changeXZ * 4;
            velocityY = changeY * 4;
            velocityXYZ = changeXYZ * 4;
        }

        if (profile.enabled.has(CPU)) {
            double load = cpu.getSystemCpuLoadBetweenTicks( prevTicks ) * 100;
            if (load > 0)
                cpuLoad = load;
            prevTicks = cpu.getSystemCpuLoadTicks();
        }

        if (profile.enabled.has(UPDATE_STATS)) {
            if (System.currentTimeMillis() - lastStatUpdate >= 500) {
                client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
                lastStatUpdate = System.currentTimeMillis();
            }
        }


        if (profile.enabled.has(CLICKS_PER_SECONDS)) {
            if (clicks == null) {
                clicks = new ArrayDeque[]{new ArrayDeque<Integer>(20), new ArrayDeque<Integer>(20)};
                for (int i = 0; i < 20; i++) {
                    clicks[0].add(0);
                    clicks[1].add(0);
                }
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

        if (profile.enabled.has(PERFORMANCE_METRICS)) {
            long[] ls = client.getMetricsData().getSamples();
            double avg = 0L;

            performanceMetrics[0] = 0;
            performanceMetrics[1] = Integer.MAX_VALUE; //MIN
            performanceMetrics[2] = Integer.MIN_VALUE; //MAX

            for (long l : ls) {
                double s = (l / 1000000D);
                performanceMetrics[1] = Math.min(performanceMetrics[1], s);
                performanceMetrics[2] = Math.max(performanceMetrics[2], s);
                avg += s;
            }
            performanceMetrics[0] = avg / ls.length;
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
        performanceMetrics = new double[3];
        x1 = y1 = z1 = velocityXZ = velocityY = velocityXYZ = 0;
        clicksSoFar[0] = clicksSoFar[1] = 0;
        clicksPerSeconds[0] = clicksPerSeconds[1] = 0;
    }

}
