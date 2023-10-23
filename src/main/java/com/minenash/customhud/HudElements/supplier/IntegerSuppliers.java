package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.mixin.accessors.WorldRendererAccess;
import com.mojang.blaze3d.platform.GLX;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.densityfunction.DensityFunction;

import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.supplier.EntryNumberSuppliers.*;

public class IntegerSuppliers {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static WorldRenderer worldRender() {
        return client.worldRenderer;
    }
    private static ChunkBuilder chunkBuilder() {
        return worldRender().getChunkBuilder();
    }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }
    private static LightingProvider serverLighting() { return ComplexData.world.getChunkManager().getLightingProvider(); }

    private static Integer chunk(WorldChunk chunk, Heightmap.Type type) {
        if (chunk == null) return null;
        BlockPos pos = client.getCameraEntity().getBlockPos();
        return chunk.sampleHeightmap(type, pos.getX(), pos.getZ());
    }

    private static Integer spawnGroup(SpawnGroup group) {
        SpawnHelper.Info info = ComplexData.serverWorld.getChunkManager().getSpawnInfo();
        return info == null ? null : info.getGroupToCount().getInt(group);
    }

    private static double biome(DensityFunction function, MultiNoiseUtil.ParameterRange[] range) {
        double d = (double)MultiNoiseUtil.toLong((float) EntryNumberSuppliers.sample(function));
        for(int i = 0; i < range.length; ++i)
            if (d < (double)range[i].max())
                return i;
        return Double.NaN;
    }


    public static final Supplier<Number> FPS = client::getCurrentFps;
    public static final Supplier<Number> MAX_FPS = () -> client.options.getMaxFps().getValue() == GameOptions.MAX_FRAMERATE ? null : client.options.getMaxFps().getValue();
    public static final Supplier<Number> BIOME_BLEND = () -> client.options.getBiomeBlendRadius().getValue();
    public static final Supplier<Number> SIMULATION_DISTANCE = () -> client.options.getSimulationDistance().getValue();

    public static final Supplier<Number> PACKETS_SENT = () -> (int)client.getNetworkHandler().getConnection().getAveragePacketsSent();
    public static final Supplier<Number> PACKETS_RECEIVED = () -> (int)client.getNetworkHandler().getConnection().getAveragePacketsReceived();
    public static final Supplier<Number> CHUNKS_RENDERED = () -> worldRender().getCompletedChunkCount();
    public static final Supplier<Number> CHUNKS_LOADED = () -> worldRender().getChunkCount();
    @SuppressWarnings("Convert2MethodRef" )
    public static final Supplier<Number> RENDER_DISTANCE = () -> client.options.getClampedViewDistance();
    public static final Supplier<Number> QUEUED_TASKS = () -> chunkBuilder().getToBatchCount();
    public static final Supplier<Number> UPLOAD_QUEUE = () -> chunkBuilder().getChunksToUpload();
    public static final Supplier<Number> BUFFER_COUNT = () -> chunkBuilder().getFreeBufferCount();
    public static final Supplier<Number> ENTITIES_RENDERED = () -> ((WorldRendererAccess)worldRender()).getRegularEntityCount();
    public static final Supplier<Number> ENTITIES_LOADED = () -> client.world.getRegularEntityCount();

    public static final Supplier<Number> FORCED_LOADED_CHUNKS = () -> ComplexData.world instanceof ServerWorld ? ((ServerWorld)ComplexData.world).getForcedChunks().size() : null;

    public static final Supplier<Number> BLOCK_X = () -> blockPos().getX();
    public static final Supplier<Number> BLOCK_Y = () -> blockPos().getY();
    public static final Supplier<Number> BLOCK_Z = () -> blockPos().getZ();
    public static final Supplier<Number> TARGET_BLOCK_X = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getX();
    public static final Supplier<Number> TARGET_BLOCK_Y = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getY();
    public static final Supplier<Number> TARGET_BLOCK_Z = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getZ();
    public static final Supplier<Number> TARGET_BLOCK_DISTANCE = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getManhattanDistance(client.player.getBlockPos());
    public static final Supplier<Number> TARGET_FLUID_X = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getX();
    public static final Supplier<Number> TARGET_FLUID_Y = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getY();
    public static final Supplier<Number> TARGET_FLUID_Z = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getZ();
    public static final Supplier<Number> TARGET_FLUID_DISTANCE = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getManhattanDistance(client.player.getBlockPos());

    public static final Supplier<Number> IN_CHUNK_X = () -> blockPos().getX() & 15;
    public static final Supplier<Number> IN_CHUNK_Y = () -> blockPos().getY() & 15;
    public static final Supplier<Number> IN_CHUNK_Z = () -> blockPos().getZ() & 15;
    public static final Supplier<Number> CHUNK_X = () -> blockPos().getX() >> 4;
    public static final Supplier<Number> CHUNK_Y = () -> blockPos().getY() >> 4;
    public static final Supplier<Number> CHUNK_Z = () -> blockPos().getZ() >> 4;
    public static final Supplier<Number> REGION_X = () -> blockPos().getX() >> 9;
    public static final Supplier<Number> REGION_Z = () -> blockPos().getZ() >> 9;
    public static final Supplier<Number> REGION_RELATIVE_X = () -> blockPos().getX() >> 4 & 0x1F;
    public static final Supplier<Number> REGION_RELATIVE_Z = () -> blockPos().getZ() >> 4 & 0x1F;


    public static final Supplier<Number> CLIENT_LIGHT = () -> {
        if (ComplexData.clientChunk.isEmpty()) return null;
        client.world.calculateAmbientDarkness();
        return Math.max(0, client.world.getChunkManager().getLightingProvider().getLight(blockPos(), client.world.getAmbientDarkness()));
    };
    public static final Supplier<Number> CLIENT_LIGHT_SKY = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getLightLevel(LightType.SKY, blockPos());
    public static final Supplier<Number> CLIENT_LIGHT_SUN = () -> {
        if (ComplexData.clientChunk.isEmpty()) return null;
        client.world.calculateAmbientDarkness();
        return Math.max(0, client.world.getLightLevel(LightType.SKY, blockPos()) - client.world.getAmbientDarkness());
    };
    public static final Supplier<Number> CLIENT_LIGHT_BLOCK = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getLightLevel(LightType.BLOCK, blockPos());
    @Deprecated public static final Supplier<Number> SERVER_LIGHT_SKY = () -> ComplexData.serverChunk == null ? null : serverLighting().get(LightType.SKY).getLightLevel(blockPos());
    @Deprecated public static final Supplier<Number> SERVER_LIGHT_BLOCK = () -> ComplexData.serverChunk == null ? null : serverLighting().get(LightType.BLOCK).getLightLevel(blockPos());

    public static final Supplier<Number> CLIENT_HEIGHT_MAP_SURFACE = () -> chunk(ComplexData.clientChunk, Heightmap.Type.WORLD_SURFACE);
    public static final Supplier<Number> CLIENT_HEIGHT_MAP_MOTION_BLOCKING = () -> chunk(ComplexData.clientChunk, Heightmap.Type.MOTION_BLOCKING);
    public static final Supplier<Number> SERVER_HEIGHT_MAP_SURFACE = () -> chunk(ComplexData.serverChunk, Heightmap.Type.WORLD_SURFACE);
    public static final Supplier<Number> SERVER_HEIGHT_MAP_OCEAN_FLOOR = () -> chunk(ComplexData.serverChunk, Heightmap.Type.OCEAN_FLOOR);
    public static final Supplier<Number> SERVER_HEIGHT_MAP_MOTION_BLOCKING = () -> chunk(ComplexData.serverChunk, Heightmap.Type.MOTION_BLOCKING);
    public static final Supplier<Number> SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES = () -> chunk(ComplexData.serverChunk, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);

    public static final Supplier<Number> MOON_PHASE = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getMoonPhase()+1;

    public static final Supplier<Number> SPAWN_CHUNKS = () -> {
        SpawnHelper.Info info = ComplexData.serverWorld.getChunkManager().getSpawnInfo();
        return info == null ? null : info.getSpawningChunkCount();
    };
    public static final Supplier<Number> MONSTERS = () -> spawnGroup(SpawnGroup.MONSTER);
    public static final Supplier<Number> CREATURES = () -> spawnGroup(SpawnGroup.CREATURE);
    public static final Supplier<Number> AMBIENT_MOBS = () -> spawnGroup(SpawnGroup.AMBIENT);
    public static final Supplier<Number> WATER_CREATURES = () -> spawnGroup(SpawnGroup.WATER_CREATURE);
    public static final Supplier<Number> WATER_AMBIENT_MOBS = () -> spawnGroup(SpawnGroup.WATER_AMBIENT);
    public static final Supplier<Number> UNDERGROUND_WATER_CREATURE = () -> spawnGroup(SpawnGroup.UNDERGROUND_WATER_CREATURE);
    public static final Supplier<Number> AXOLOTLS = () -> spawnGroup(SpawnGroup.AXOLOTLS);
    public static final Supplier<Number> MISC_MOBS = () -> spawnGroup(SpawnGroup.MISC);

    public static final Supplier<Number> JAVA_BIT = () -> client.is64Bit() ? 64 : 32;
    public static final Supplier<Number> CPU_CORES = () -> ComplexData.cpu.getPhysicalProcessorCount();
    public static final Supplier<Number> CPU_THREADS = () -> ComplexData.cpu.getLogicalProcessorCount();

    public static final Supplier<Number> DISPLAY_WIDTH = () -> client.getWindow().getFramebufferWidth();
    public static final Supplier<Number> DISPLAY_HEIGHT = () -> client.getWindow().getFramebufferHeight();
    public static final Supplier<Number> DISPLAY_REFRESH_RATE = () -> GLX._getRefreshRate(client.getWindow());
    public static final Supplier<Number> MODS = () -> FabricLoader.getInstance().getAllMods().size();
    public static final Supplier<Number> PING = () -> Math.round(ComplexData.pingMetrics[0]);
    public static final Supplier<Number> LATENCY = () -> client.player.networkHandler.getPlayerListEntry(client.player.getUuid()).getLatency();
    public static final Supplier<Number> SOLAR_TIME = () -> client.world.getTimeOfDay() % 24000;
    public static final Supplier<Number> LUNAR_TIME = () -> client.world.getTimeOfDay();

    public static final Supplier<Number> SLOTS_USED = () -> ComplexData.slots_used;
    public static final Supplier<Number> SLOTS_EMPTY = () -> ComplexData.slots_empty;

    public static final Supplier<Number> FOOD_LEVEL = () -> client.player.getHungerManager().getFoodLevel();
    public static final Supplier<Number> SATURATION_LEVEL = () -> client.player.getHungerManager().getSaturationLevel();
    public static final Supplier<Number> ARMOR_LEVEL = () -> client.player.getArmor();
    public static final Supplier<Number> AIR_LEVEL = () -> Math.round(20F * client.player.getAir() / client.player.getMaxAir());
    public static final Supplier<Number> XP_LEVEL = () -> client.player.experienceLevel;
    public static final Supplier<Number> XP_POINTS = () -> client.player.experienceProgress * client.player.getNextLevelExperience();
    public static final Supplier<Number> XP_POINTS_NEEDED = () -> client.player.getNextLevelExperience();
    public static final Supplier<Number> HEALTH = () -> client.player.getHealth() + client.player.getAbsorptionAmount();
    public static final Supplier<Number> HEALTH_MAX = () -> client.player.getMaxHealth();

    public static final Supplier<Number> FOOD_LEVEL_PERCENTAGE = () -> client.player.getHungerManager().getFoodLevel() * 5;
    public static final Supplier<Number> SATURATION_LEVEL_PERCENTAGE = () -> client.player.getHungerManager().getSaturationLevel() * 5;
    public static final Supplier<Number> ARMOR_LEVEL_PERCENTAGE = () -> client.player.getArmor() * 5;

    public static final Supplier<Number> BIOME_BUILDER_EROSION = () -> isNoise() ? biome(sampler().erosion(), par.getErosionParameters()) : Double.NaN;
    public static final Supplier<Number> BIOME_BUILDER_TEMPERATURE = () -> isNoise() ? biome(sampler().temperature(), par.getTemperatureParameters()) : Double.NaN;
    public static final Supplier<Number> BIOME_BUILDER_VEGETATION = () -> isNoise() ? biome(sampler().vegetation(), par.getHumidityParameters()) : Double.NaN;

    @Deprecated public static final Supplier<Number> ITEM_DURABILITY = () -> client.player.getMainHandStack().getMaxDamage() - client.player.getMainHandStack().getDamage();
    @Deprecated public static final Supplier<Number> ITEM_MAX_DURABILITY = () -> client.player.getMainHandStack().getMaxDamage();
    @Deprecated public static final Supplier<Number> OFFHAND_ITEM_DURABILITY = () -> client.player.getOffHandStack().getMaxDamage() - client.player.getOffHandStack().getDamage();
    @Deprecated public static final Supplier<Number> OFFHAND_ITEM_MAX_DURABILITY = () -> client.player.getOffHandStack().getMaxDamage();

    public static final Supplier<Number> LCPS = () -> ComplexData.clicksPerSeconds[0];
    public static final Supplier<Number> RCPS = () -> ComplexData.clicksPerSeconds[1];

    public static final Supplier<Number> TIME_HOUR_12 = () -> {
        int hour = ComplexData.timeOfDay / 1000 % 12;
        return hour == 0 ? 12 : hour;
    };

}
