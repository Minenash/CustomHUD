package com.minenash.customhud;

import com.minenash.customhud.data.Profile;
import com.minenash.customhud.mixin.DebugHudAccessor;
import com.minenash.customhud.mod_compat.CustomHudRegistry;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.PerformanceLog;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.RaycastContext;
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
    public static Entity targetEntity = null;
    public static Vec3d targetEntityHitPos = null;
    public static Entity lastHitEntity = null;
    public static double lastHitEntityDist = Double.NaN;
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

    public static double[] frameTimeMetrics = new double[4];
    public static double[] tickTimeMetrics = new double[4];
    public static double[] pingMetrics = new double[4];
    public static double[] packetSizeMetrics = new double[4];
    public static double[] tpsMetrics = new double[4];

    public static int slots_used = 0;
    public static int slots_empty = 0;

    private static long lastStatUpdate = 0;

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

        if (profile.enabled.targetEntity) {
            double dist = profile.targetDistance;

            Vec3d min = client.cameraEntity.getCameraPosVec(0);
            Vec3d rot = client.cameraEntity.getRotationVec(1.0F);
            Vec3d max = min.add(rot.x * dist, rot.y * dist, rot.z * dist);
            Box box = client.cameraEntity.getBoundingBox().stretch(rot.multiply(dist)).expand(1.0, 1.0, 1.0);

            HitResult block = client.cameraEntity.raycast(dist, 0, false);
            double dist2 = block == null ? dist*dist : block.getPos().squaredDistanceTo(min);

            EntityHitResult result = ProjectileUtil.raycast(client.cameraEntity, min, max, box, (en) -> !en.isSpectator(), dist2);
            targetEntity = result == null ? null : result.getEntity();
            targetEntityHitPos = result == null ? null : result.getPos();
        }

        if (profile.enabled.localDifficulty)
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());

        if (profile.enabled.sound)
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");

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
            velocityXZ = changeXZ * 4;
            velocityY = changeY * 4;
            velocityXYZ = changeXYZ * 4;
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

        if (profile.enabled.frameMetrics) processLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getFrameNanosLog(), 0.000001, 240, frameTimeMetrics);
        if (profile.enabled.tickMetrics) processLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getTickNanosLog(), 0.000001, 120, tickTimeMetrics);
        if (profile.enabled.pingMetrics) processLog(client.inGameHud.getDebugHud().getPingLog(), 1, 120, pingMetrics);
        if (profile.enabled.packetMetrics) processLog(client.inGameHud.getDebugHud().getPacketSizeLog(), 20/1024D, 120, packetSizeMetrics);
        if (profile.enabled.tpsMetrics) processTPSLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getTickNanosLog(), tpsMetrics);

        if (profile.enabled.slots) {
            slots_used = slots_empty = 0;
            DefaultedList<ItemStack> inv = client.player.getInventory().main;
            for (ItemStack itemStack : inv) {
                if (itemStack == ItemStack.EMPTY)
                    slots_empty++;
                else
                    slots_used++;
            }
        }

        if (profile.enabled.music) {
            MusicAndRecordTracker.tick();
        }

        CustomHudRegistry.runComplexData();

    }

    public static void processLog(PerformanceLog log, double multiplier, int samples, double[] metrics) {
        if (log.getMaxIndex() == 0) {
            metrics[0] = metrics[1] = metrics[2] = metrics[3] = Double.NaN;
            return;
        }

        metrics[0] = 0; //AVG
        metrics[1] = Integer.MAX_VALUE; //MIN
        metrics[2] = Integer.MIN_VALUE; //MAX
        metrics[3] = Math.min(samples, log.getMaxIndex()); //SAMPLES

        double avg = 0L;
        for (int r = 0; r <  metrics[3]; ++r) {
            double s = log.get(r) * multiplier;
            metrics[1] = Math.min(metrics[1], s);
            metrics[2] = Math.max(metrics[2], s);
            avg += s;
        }
        metrics[0] = avg / metrics[3];
    }

    public static void processTPSLog(PerformanceLog log, double[] metrics) {
        if (log.getMaxIndex() == 0) {
            metrics[0] = metrics[1] = metrics[2] = metrics[3] = Double.NaN;
            return;
        }

        metrics[0] = 0; //AVG
        metrics[1] = Integer.MAX_VALUE; //MIN
        metrics[2] = Integer.MIN_VALUE; //MAX
        metrics[3] = Math.min(120, log.getMaxIndex()); //SAMPLES

        double avg = 0L;
        for (int r = 0; r <  metrics[3]; ++r) {
            double s = Math.min(20, 1000F / (log.get(r) * 0.000001));
            metrics[1] = Math.min(metrics[1], s);
            metrics[2] = Math.max(metrics[2], s);
            avg += s;
        }
        metrics[0] = avg / metrics[3];
    }

    public static void reset() {
        clientChunk = null;
        serverChunk = null;
        serverWorld = null;
        localDifficulty = null;
        world = null;
        sounds = null;
        clientChunkCache = null;
        clicks = null;
        frameTimeMetrics = new double[4];
        tickTimeMetrics = new double[4];
        pingMetrics = new double[4];
        packetSizeMetrics = new double[4];
        x1 = y1 = z1 = velocityXZ = velocityY = velocityXYZ = 0;
        slots_used = slots_empty = 0;
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
        public boolean targetEntity = false;
        public boolean time = false;
        public boolean velocity = false;
        public boolean cpu = false;
        public boolean updateStats = false;
        public boolean clicksPerSeconds = false;
        public boolean music = false;

        public boolean gpuMetrics = false;
        public boolean frameMetrics = false;
        public boolean tickMetrics = false;
        public boolean tpsMetrics = false;
        public boolean pingMetrics = false;
        public boolean packetMetrics = false;

        public boolean slots = false;
    }



}
