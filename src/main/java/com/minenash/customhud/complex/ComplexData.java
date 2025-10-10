package com.minenash.customhud.complex;

import com.minenash.customhud.data.Profile;
import com.minenash.customhud.mixin.accessors.DebugHudAccessor;
import com.minenash.customhud.registry.CustomHudRegistry;
import com.mojang.datafixers.DataFixUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerTiming;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.minenash.customhud.CustomHud.CLIENT;

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
    public static long lastHitEntityTime = -1;
    public static String[] sounds = null;
    public static String[] clientChunkCache = null;
    public static int timeOfDay = -1;
    public static double x1 = 0, y1 = 0, z1 = 0, velocityXZ = 0, velocityY = 0, velocityXYZ = 0;

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final BlockState AIR_BLOCK_STATE = Blocks.AIR.getDefaultState();

    //Chunk Data.
    private static ChunkPos pos = null;
    private static CompletableFuture<WorldChunk> chunkFuture;

    public static Object cpu;
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

    public static final Map<UUID, BossBar> bossbars = new HashMap<>();

    public static TradeOfferList villagerOffers = new TradeOfferList();
    public static int villagerXP = 0;
    public static UUID villagerUUID = null;
    public static int fakeVillagerInteract = 0;
    public static long villagerLastRequested = Long.MAX_VALUE;

    public static boolean refreshTimings = false;
    public record ProfilerTimingWithPath(String path, String name, double parent, double total, int color, List<ProfilerTimingWithPath> entries) {}
    public static List<ProfilerTimingWithPath> rootEntries = Collections.EMPTY_LIST;
    public static Map<String,ProfilerTimingWithPath> allEntries = Collections.EMPTY_MAP;

    @SuppressWarnings("ConstantConditions")
    public static void update(Profile profile) {

        Profilers.get().push("custom_hud_complex_data");
        if (profile.enabled.serverWorld) {
            Profilers.get().push("serverWorld");
            IntegratedServer integratedServer = client.getServer();
            serverWorld = integratedServer != null ? integratedServer.getWorld(client.world.getRegistryKey()) : null;
            Profilers.get().pop();
        }

        if (profile.enabled.clientChunk) {
            Profilers.get().push("clientChunk");
            ChunkPos newPos = new ChunkPos(client.getCameraEntity().getBlockPos());
            if (!Objects.equals(ComplexData.pos,newPos)) {
                pos = newPos;
                chunkFuture = null;
                clientChunk = null;
            }
            if (clientChunk == null)
                clientChunk = client.world.getChunk(pos.x, pos.z);
            Profilers.get().pop();
        }

        if (profile.enabled.serverChunk) {
            Profilers.get().push("serverChunk");
            if (chunkFuture == null) {
                if (serverWorld != null)
                    chunkFuture = serverWorld.getChunkManager().getChunkFutureSyncOnMainThread(pos.x, pos.z, ChunkStatus.FULL, false).thenApply((either) -> (WorldChunk) either.orElse(null));

                if (chunkFuture == null)
                    chunkFuture = CompletableFuture.completedFuture(clientChunk);
            }
            serverChunk = chunkFuture.getNow(null);
            Profilers.get().pop();
        }

        if (profile.enabled.world) {
            Profilers.get().push("world");
            world = DataFixUtils.orElse(Optional.ofNullable(client.getServer()).flatMap((integratedServer) -> Optional.ofNullable(integratedServer.getWorld(client.world.getRegistryKey()))), client.world);
            Profilers.get().pop();
        }

        if (profile.enabled.targetBlock) {
            Profilers.get().push("targetBlock");
            HitResult hit =  client.cameraEntity.raycast(profile.targetDistance, 0.0F, false);

            if (hit.getType() == HitResult.Type.BLOCK) {
                targetBlockPos = ((BlockHitResult)hit).getBlockPos();
                targetBlock = world.getBlockState(targetBlockPos);
            }
            else {
                targetBlockPos = null;
                targetBlock = AIR_BLOCK_STATE;
            }
            Profilers.get().pop();
        }

        if (profile.enabled.targetFluid) {
            Profilers.get().push("targetFluid");
            HitResult hit =  client.cameraEntity.raycast(profile.targetDistance, 0.0F, true);

            if (hit.getType() == HitResult.Type.BLOCK) {
                targetFluidPos = ((BlockHitResult)hit).getBlockPos();
                targetFluid = world.getFluidState(targetFluidPos);
            }
            else {
                targetFluidPos = null;
                targetFluid = Fluids.EMPTY.getDefaultState();
            }

            Profilers.get().pop();
        }

        if (profile.enabled.targetEntity) {
            Profilers.get().push("targetEntity");
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
            Profilers.get().pop();
        }

        if (profile.enabled.localDifficulty) {
            Profilers.get().push("localDifficulty");
            localDifficulty = new LocalDifficulty(world.getDifficulty(), world.getTimeOfDay(), serverChunk == null ? 0 : serverChunk.getInhabitedTime(), world.getMoonSize());
            Profilers.get().pop();
        }

        if (profile.enabled.sound) {
            Profilers.get().push("sound");
            sounds = client.getSoundManager().getDebugString().substring(8).replace(" + ", "/").split("/");
            Profilers.get().pop();
        }

        if (profile.enabled.time) {
            Profilers.get().push("time");
            timeOfDay = (int) ((client.world.getTimeOfDay() + 6000) % 24000);
            Profilers.get().pop();
        }

        if (!profile.enabled.velocityTrackers.isEmpty()) {
            Profilers.get().push("velocities");
            for (var v : profile.enabled.velocityTrackers)
                v.tick();
            VelocityTracker.recordCords();
            Profilers.get().pop();
        }

        if (profile.enabled.cpu) {
            if (cpu == null)
                cpu = new SystemInfo().getHardware().getProcessor();
        }
        if (profile.enabled.cpuUsage) {
            Profilers.get().push("cpu");
            var c = (CentralProcessor) cpu;
            double load = c.getSystemCpuLoadBetweenTicks( prevTicks ) * 100;
            if (load > 0)
                cpuLoad = load;
            prevTicks = c.getSystemCpuLoadTicks();
            Profilers.get().pop();
        }

        if (profile.enabled.updateStats) {
            Profilers.get().push("updateStats");
            if (System.currentTimeMillis() - lastStatUpdate >= 500) {
                client.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS));
                lastStatUpdate = System.currentTimeMillis();
            }
            Profilers.get().pop();
        }


        if (profile.enabled.clicksPerSeconds) {
            Profilers.get().push("clicksPerSeconds");
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
            Profilers.get().pop();
        }

        if (profile.enabled.frameMetrics) {
            Profilers.get().push("frameMetrics");
            processLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getFrameNanosLog(), 0.000001, 240, frameTimeMetrics);
            Profilers.get().pop();
        }
        if (profile.enabled.tickMetrics) {
            Profilers.get().push("tickMetrics");
            processLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getTickNanosLog(), 0.000001, 120, tickTimeMetrics);
            Profilers.get().pop();
        }
        if (profile.enabled.pingMetrics) {
            Profilers.get().push("pingMetrics");
            processLog(client.inGameHud.getDebugHud().getPingLog(), 1, 120, pingMetrics);
            Profilers.get().pop();
        }
        if (profile.enabled.packetMetrics) {
            Profilers.get().push("packetMetrics");
            processLog(client.inGameHud.getDebugHud().getPacketSizeLog(), 20/1024D, 120, packetSizeMetrics);
            Profilers.get().pop();
        }
        if (profile.enabled.tpsMetrics) {
            Profilers.get().push("tpsMetrics");
            processTPSLog(((DebugHudAccessor)client.inGameHud.getDebugHud()).getTickNanosLog(), tpsMetrics);
            Profilers.get().pop();
        }

        if (profile.enabled.slots) {
            Profilers.get().push("slots");
            slots_used = slots_empty = 0;
            DefaultedList<ItemStack> inv = client.player.getInventory().getMainStacks();
            for (ItemStack itemStack : inv) {
                if (itemStack == ItemStack.EMPTY)
                    slots_empty++;
                else
                    slots_used++;
            }
            Profilers.get().pop();
        }

        if (profile.enabled.music) {
            Profilers.get().push("music");
            MusicAndRecordTracker.tick();
            Profilers.get().pop();
        }

        if (profile.enabled.targetVillager) {
            Profilers.get().push("targetVillager");
            if ( !(targetEntity instanceof VillagerEntity) && villagerUUID != null) {
                villagerOffers.clear();
                villagerUUID = null;
                villagerLastRequested = Long.MAX_VALUE;
            }
            else if (targetEntity instanceof VillagerEntity && (villagerUUID == null ||
                    !targetEntity.getUuid().equals(villagerUUID) || System.currentTimeMillis() - villagerLastRequested > 30_000)) {
                villagerUUID = targetEntity.getUuid();
                fakeVillagerInteract = 2;
                CLIENT.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.interact(targetEntity, false, Hand.OFF_HAND));
                villagerLastRequested = System.currentTimeMillis();
            }
            Profilers.get().pop();
        }

        if (profile.enabled.profilerTimings) {
            Profilers.get().push("profilerTimings");
            ProfileResult profileResult = CLIENT.getDebugHud().getPieChart().profileResult;
            if (profileResult == null) {
                rootEntries = Collections.EMPTY_LIST;
                allEntries = Collections.EMPTY_MAP;
            }
            else {
                rootEntries = new ArrayList<>();
                allEntries = new HashMap<>();
                List<ProfilerTiming> timings = profileResult.getTimings("root");
                timings.remove(0);
                for (var entry : timings)
                    rootEntries.add( getEntries(profileResult, entry, "root\u001e" + entry.name) );
            }
            Profilers.get().pop();
        }

        SubtitleTracker.INSTANCE.setEnable(profile.enabled.subtitles);
        Profilers.get().push("registry");
        CustomHudRegistry.runComplexData(profile.enabled);
        Profilers.get().pop();
        Profilers.get().pop();
    }

    public static ProfilerTimingWithPath getEntries(ProfileResult profileResult, ProfilerTiming timing, String path) {
        List<ProfilerTimingWithPath> entries = new ArrayList<>();
        List<ProfilerTiming> timings = profileResult.getTimings(path);
        timings.remove(0);
        for (var entry : timings)
            entries.add(getEntries(profileResult, entry, path + "\u001e" + entry.name));

        ProfilerTimingWithPath entry = new ProfilerTimingWithPath(path, timing.name, timing.parentSectionUsagePercentage, timing.totalUsagePercentage, timing.getColor(), entries);
        allEntries.put(path, entry);
        return entry;
    }

    public static void processLog(MultiValueDebugSampleLogImpl log, double multiplier, int samples, double[] metrics) {
        if (log.getLength() == 0) {
            metrics[0] = metrics[1] = metrics[2] = metrics[3] = Double.NaN;
            return;
        }

        metrics[0] = 0; //AVG
        metrics[1] = Integer.MAX_VALUE; //MIN
        metrics[2] = Integer.MIN_VALUE; //MAX
        metrics[3] = Math.min(samples, log.getLength()-1); //SAMPLES

        double avg = 0L;
        for (int r = 0; r <  metrics[3]; ++r) {
            double s = log.get(r) * multiplier;
            metrics[1] = Math.min(metrics[1], s);
            metrics[2] = Math.max(metrics[2], s);
            avg += s;
        }
        metrics[0] = avg / metrics[3];
    }

    public static void processTPSLog(MultiValueDebugSampleLogImpl log, double[] metrics) {
        if (log.getLength() == 0) {
            metrics[0] = metrics[1] = metrics[2] = metrics[3] = Double.NaN;
            return;
        }

        metrics[0] = 0; //AVG
        metrics[1] = Integer.MAX_VALUE; //MIN
        metrics[2] = Integer.MIN_VALUE; //MAX
        metrics[3] = Math.min(120, log.getLength()-1); //SAMPLES

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
        public final Map<String,Boolean> custom = new HashMap<>();

        public final List<VelocityTracker> velocityTrackers = new ArrayList<>();

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
        public boolean cpuUsage = false;
        public boolean updateStats = false;
        public boolean clicksPerSeconds = false;
        public boolean music = false;
        public boolean subtitles = false;

        public boolean gpuMetrics = false;
        public boolean frameMetrics = false;
        public boolean tickMetrics = false;
        public boolean tpsMetrics = false;
        public boolean pingMetrics = false;
        public boolean packetMetrics = false;
        public boolean profilerTimings = false;

        public boolean slots = false;
        public boolean targetVillager = false;

        public void merge(Enabled enabled) {
            for (Field field : this.getClass().getFields()) {
                if (field.getType() != Boolean.TYPE) continue;
                try { field.setBoolean(this, field.getBoolean(this) || field.getBoolean(enabled)); }
                catch (Exception ignored) {}
            }
            this.custom.putAll(enabled.custom);


            this.velocityTrackers.addAll(enabled.velocityTrackers);
        }

        public boolean get(String name) {
            return custom.getOrDefault(name, false);
        }
        public void set(String name) {
            custom.put(name, true);
        }
        public void set(String name, boolean value) {
            custom.put(name, value);
        }
    }



}
