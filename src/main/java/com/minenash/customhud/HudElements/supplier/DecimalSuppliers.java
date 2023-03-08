package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ComplexData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.supplier.NumberSupplierElement.*;

public class DecimalSuppliers {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Runtime runtime = Runtime.getRuntime();

    private static Entity cameraEntity() { return client.getCameraEntity(); }
    private static boolean inNether() { return client.world.getRegistryKey().getValue().equals(World.NETHER.getValue()); }
    private static double toMiB(long bytes) { return bytes / 1024D / 1024L; }

    public static final Entry X = of( () -> cameraEntity().getX(), 3);
    public static final Entry Y = of( () -> cameraEntity().getY(), 3);
    public static final Entry Z = of( () -> cameraEntity().getZ(), 3);
    public static final Entry NETHER_X = of( () -> inNether() ? cameraEntity().getX() * 8 : cameraEntity().getX() / 8, 0);
    public static final Entry NETHER_Z = of( () -> inNether() ? cameraEntity().getZ() * 8 : cameraEntity().getZ() / 8, 0);

    //TODO: Fix precision
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

    public static final Entry TICK_MS = of( () -> client.getServer() == null ? null : client.getServer().getTickTime(), 0);
    public static final Entry FRAME_MS_MIN = of( () -> ComplexData.performanceMetrics.getMinTime() / 1000000D, 0);
    public static final Entry FRAME_MS_MAX = of( () -> ComplexData.performanceMetrics.getMaxTime() / 1000000D, 0);
    public static final Entry FRAME_MS_AVG = of( () -> ComplexData.performanceMetrics.getAverageTime() / 1000000D, 1);
    public static final Entry FRAME_MS_SAMPLES = of( () -> ComplexData.performanceMetrics.getSampleCount(), 0);

    @Deprecated public static final Entry ITEM_DURABILITY_PERCENT = of( () -> client.player.getMainHandStack().getDamage() / (float) client.player.getMainHandStack().getMaxDamage() * 100, 0);
    @Deprecated public static final Entry OFFHAND_ITEM_DURABILITY_PERCENT = of( () -> client.player.getOffHandStack().getDamage() / (float) client.player.getOffHandStack().getMaxDamage() * 100, 0);

    public static final Entry DAY = of( () -> client.world.getTimeOfDay() / 24000L, 0);

    public static final Entry TPS = of( () -> {
        IntegratedServer server = client.getServer();
        if (server == null) return null;
        float ms_tics = client.getServer().getTickTime();
        return ms_tics < 50 ? 20 : 1000/ms_tics;
    }, 0);

    public static final Entry CPU_USAGE = of( () -> ComplexData.cpuLoad, 0);
    public static final Entry GPU_USAGE = of(client::getGpuUtilizationPercentage, 0);
    public static final Entry MEMORY_USED_PERCENTAGE = of( () -> (runtime.totalMemory() - runtime.freeMemory())*100D / runtime.maxMemory(), 0);
    public static final Entry MEMORY_USED = of( () -> toMiB(runtime.totalMemory() - runtime.freeMemory()), 0);
    public static final Entry TOTAL_MEMORY = of( () -> toMiB(runtime.maxMemory()), 0);
    public static final Entry ALLOCATED_PERCENTAGE = of( () -> runtime.totalMemory() * 100 / runtime.maxMemory(), 0);
    public static final Entry ALLOCATED = of( () -> toMiB(runtime.totalMemory()), 0);
//    public static final Entry OFF_HEAP = of( () -> toMiB(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed() + NativeBuffer.getTotalAllocated()), 0);

}
