package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.complex.EstimatedTick;
import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.minenash.customhud.data.StatFormatters;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.chunk.NoiseChunkGenerator;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.noise.NoiseRouter;

import static com.minenash.customhud.HudElements.supplier.NumberSupplierElement.*;

public class EntryNumberSuppliers {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Runtime runtime = Runtime.getRuntime();
    public static final VanillaBiomeParameters par = new VanillaBiomeParameters();
    public static DebugHud.AllocationRateCalculator allocationRateCalculator = new DebugHud.AllocationRateCalculator();

    private static Entity cameraEntity() { return client.getCameraEntity(); }
    private static boolean inNether() { return client.world.getRegistryKey().getValue().equals(World.NETHER.getValue()); }
    private static double toMiB(long bytes) { return bytes / 1024D / 1024D; }

    public static boolean isNoise() { return ComplexData.serverWorld.getChunkManager().getChunkGenerator() instanceof NoiseChunkGenerator; }
    public static NoiseRouter sampler() { return ComplexData.serverWorld.getChunkManager().getNoiseConfig().getNoiseRouter(); }
    public static double sample(DensityFunction function) {
        BlockPos pos = client.player.getBlockPos();
        return function.sample(new DensityFunction.UnblendedNoisePos(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static final Entry ACTIONBAR_REMAINING = of( () -> client.inGameHud.overlayRemaining, 0, StatFormatters.MIL_HMS);
    public static final Entry TITLE_REMAINING = of( () -> client.inGameHud.titleRemainTicks, 0, StatFormatters.MIL_HMS);

    public static final Entry X = of( () -> cameraEntity().getX(), 3);
    public static final Entry Y = of( () -> cameraEntity().getY(), 3);
    public static final Entry Z = of( () -> cameraEntity().getZ(), 3);
    public static final Entry NETHER_X = of( () -> inNether() ? cameraEntity().getX() * 8 : cameraEntity().getX() / 8, 0);
    public static final Entry NETHER_Z = of( () -> inNether() ? cameraEntity().getZ() * 8 : cameraEntity().getZ() / 8, 0);

    public static final Entry ENTITY_REACH_DISTANCE = of ( () -> {
        EntityAttributeInstance instance = client.player.getAttributeInstance(EntityAttributes.ENTITY_INTERACTION_RANGE);
        return instance == null ? 0 : instance.getValue();
    }, 1);
    public static final Entry BLOCK_REACH_DISTANCE = of ( () -> {
        EntityAttributeInstance instance = client.player.getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
        return instance == null ? 0 : instance.getValue();
    }, 1);
    public static final Entry FISHING_HOOK_DISTANCE = of ( () -> client.player.fishHook.distanceTo(client.player), 1);

    public static final Entry VELOCITY_XZ = of( () -> ComplexData.velocityXZ, 1);
    public static final Entry VELOCITY_Y = of( () -> ComplexData.velocityY, 1);
    public static final Entry VELOCITY_XYZ = of( () -> ComplexData.velocityXYZ, 1);
    public static final Entry VELOCITY_XZ_KMH = of( () -> ComplexData.velocityXZ * 3.6, 1);
    public static final Entry VELOCITY_Y_KMH = of( () -> ComplexData.velocityY * 3.6, 1);
    public static final Entry VELOCITY_XYZ_KMH = of( () -> ComplexData.velocityXYZ * 3.6, 1);

    public static final Entry YAW = of( () -> MathHelper.wrapDegrees(cameraEntity().getYaw()), 1);
    public static final Entry PITCH = of( () -> MathHelper.wrapDegrees(cameraEntity().getPitch()), 1);

    public static final Entry LOCAL_DIFFICULTY = of( () -> ComplexData.localDifficulty.getLocalDifficulty(), 2);
    public static final Entry CLAMPED_LOCAL_DIFFICULTY = of( () -> ComplexData.localDifficulty.getClampedLocalDifficulty(), 2);
    public static final Entry MOOD = of( () -> client.player.getMoodPercentage() * 100.0F, 0);

    public static final Entry FRAME_MS_MIN = of( () -> ComplexData.frameTimeMetrics[1], 0);
    public static final Entry FRAME_MS_MAX = of( () -> ComplexData.frameTimeMetrics[2], 0);
    public static final Entry FRAME_MS_AVG = of( () -> ComplexData.frameTimeMetrics[0], 1);
    public static final Entry FRAME_MS_SAMPLES = of( () -> ComplexData.frameTimeMetrics[3], 0);

    public static final Entry FPS_MIN = of( () -> 1000 / ComplexData.frameTimeMetrics[2], 0);
    public static final Entry FPS_MAX = of( () -> 1000 / ComplexData.frameTimeMetrics[1], 0);
    public static final Entry FPS_AVG = of( () -> 1000 / ComplexData.frameTimeMetrics[0], 1);

    public static final Entry MS_PER_TICK = of( () -> ComplexData.world.getTickManager().getMillisPerTick(), 0);

    public static final Entry TICK_MS = of( () -> client.getServer() != null ? client.getServer().getAverageTickTime() : EstimatedTick.get(), 0);
    public static final Entry TICK_MS_MIN = of( () -> ComplexData.tickTimeMetrics[1], 0);
    public static final Entry TICK_MS_MAX = of( () -> ComplexData.tickTimeMetrics[2], 0);
    public static final Entry TICK_MS_AVG = of( () -> ComplexData.tickTimeMetrics[0], 1);
    public static final Entry TICK_MS_SAMPLES = of( () -> ComplexData.tickTimeMetrics[3], 0);

    public static final Entry TPS_MIN = of( () -> ComplexData.tpsMetrics[1], 0);
    public static final Entry TPS_MAX = of( () -> ComplexData.tpsMetrics[2], 0);
    public static final Entry TPS_AVG = of( () -> ComplexData.tpsMetrics[0], 1);
    public static final Entry TPS_SAMPLES = of( () -> ComplexData.tpsMetrics[3], 0);

    public static final Entry PING_MIN = of( () -> ComplexData.pingMetrics[1], 0);
    public static final Entry PING_MAX = of( () -> ComplexData.pingMetrics[2], 0);
    public static final Entry PING_AVG = of( () -> ComplexData.pingMetrics[0], 1);
    public static final Entry PING_SAMPLES = of( () -> ComplexData.pingMetrics[3], 0);

    public static final Entry PACKET_SIZE_MIN = of( () -> ComplexData.packetSizeMetrics[1], 0);
    public static final Entry PACKET_SIZE_MAX = of( () -> ComplexData.packetSizeMetrics[2], 0);
    public static final Entry PACKET_SIZE_AVG = of( () -> ComplexData.packetSizeMetrics[0], 1);
    public static final Entry PACKET_SIZE_SAMPLES = of( () -> ComplexData.packetSizeMetrics[3], 0);

    public static final Entry SLOTS_PERCENTAGE = of( () -> 100F * ComplexData.slots_used / client.player.getInventory().main.size(), 0);

    public static final Entry RECORD_LENGTH = of( () -> MusicAndRecordTracker.isRecordPlaying ? MusicAndRecordTracker.getClosestRecord().length / 20F : Double.NaN, 0, StatFormatters.SEC_HMS);
    public static final Entry RECORD_ELAPSED = of( () -> MusicAndRecordTracker.isRecordPlaying ? MusicAndRecordTracker.getClosestRecord().elapsed / 20F : Double.NaN, 0, StatFormatters.SEC_HMS);
    public static final Entry RECORD_REMAINING = of( () -> MusicAndRecordTracker.isRecordPlaying ? (MusicAndRecordTracker.getClosestRecord().length - MusicAndRecordTracker.getClosestRecord().elapsed) / 20F : Double.NaN, 0, StatFormatters.SEC_HMS);
    public static final Entry RECORD_ELAPSED_PER = of( () -> 100F * MusicAndRecordTracker.getClosestRecord().elapsed / MusicAndRecordTracker.getClosestRecord().length, 0);

    public static final Entry XP_POINTS_PER = of( () -> client.player.experienceProgress * 100, 0);
    public static final Entry AIR_LEVEL_PERCENTAGE = of( () -> 100F * client.player.getAir() / client.player.getMaxAir(), 0);
    public static final Entry HEALTH_PERCENTAGE = of( () -> 100F * (client.player.getHealth() + client.player.getAbsorptionAmount()) / client.player.getMaxHealth(), 0);

    public static final Entry NOISE_ROUTER_TEMPERATURE = of( () -> isNoise() ? sample(sampler().temperature()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_VEGETATION = of( () -> isNoise() ? sample(sampler().vegetation()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_CONTINENTS = of( () -> isNoise() ? sample(sampler().continents()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_EROSION = of( () -> isNoise() ? sample(sampler().erosion()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_DEPTH = of( () -> isNoise() ? sample(sampler().depth()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_RIDGES = of( () -> isNoise() ? sample(sampler().ridges()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_PEAKS = of( () -> isNoise() ? DensityFunctions.getPeaksValleysNoise((float)sample(sampler().ridges())) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_INIT_DENSITY = of( () -> isNoise() ? sample(sampler().initialDensityWithoutJaggedness()) : Double.NaN, 3);
    public static final Entry NOISE_ROUTER_FINAL_DENSITY = of( () -> isNoise() ? sample(sampler().finalDensity()) : Double.NaN, 3);

    @Deprecated public static final Entry ITEM_DURABILITY_PERCENT = of( () -> client.player.getMainHandStack().getDamage() / (float) client.player.getMainHandStack().getMaxDamage() * 100, 0);
    @Deprecated public static final Entry OFFHAND_ITEM_DURABILITY_PERCENT = of( () -> client.player.getOffHandStack().getDamage() / (float) client.player.getOffHandStack().getMaxDamage() * 100, 0);

    public static final Entry DAY = of( () -> client.world.getTimeOfDay() / 24000L, 0);

    public static final Entry TPS = of( () -> {
        IntegratedServer server = client.getServer();
        float ms_ticks = server == null ? EstimatedTick.get() : server.getAverageTickTime();
        return ms_ticks < 50 ? 20 : 1000/ms_ticks;
    }, 0);
    public static final Entry MAX_TPS = of( () -> client.world.getTickManager().getTickRate(), 0);

    public static final Entry CPU_USAGE = of( () -> ComplexData.cpuLoad, 0);
    public static final Entry GPU_USAGE = of(() -> ComplexData.gpuUsage, 0);
    public static final Entry MEMORY_USED_PERCENTAGE = of( () -> (runtime.totalMemory() - runtime.freeMemory())*100D / runtime.maxMemory(), 0);
    public static final Entry MEMORY_USED = of( () -> toMiB(runtime.totalMemory() - runtime.freeMemory()), 0);
    public static final Entry TOTAL_MEMORY = of( () -> toMiB(runtime.maxMemory()), 0);
    public static final Entry ALLOCATED_PERCENTAGE = of( () -> runtime.totalMemory() * 100 / runtime.maxMemory(), 0);
    public static final Entry ALLOCATED = of( () -> toMiB(runtime.totalMemory()), 0);
    public static final Entry ALLOCATION_RATE = of( () -> toMiB( allocationRateCalculator.get( runtime.totalMemory() - runtime.freeMemory() ) ), 0);

    private static final double PHI_CONST = (1 + Math.sqrt(5))/2;

    public static final Entry E = of( () -> Math.E, 10);
    public static final Entry PI = of( () -> Math.PI, 10);
    public static final Entry TAU = of( () -> Math.PI*2, 10);
    public static final Entry PHI = of( () -> PHI_CONST, 10);


}
