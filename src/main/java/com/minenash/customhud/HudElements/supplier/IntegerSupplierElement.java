package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.mixin.ChunkBuilderAccess;
import com.minenash.customhud.mixin.MinecraftClientAccess;
import com.minenash.customhud.mixin.WorldRendererAccess;
import com.mojang.blaze3d.platform.GLX;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.LightingProvider;

import java.util.function.Supplier;

public class IntegerSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static WorldRendererAccess worldRender() {
        return (WorldRendererAccess)client.worldRenderer;
    }
    private static ChunkBuilderAccess chunkBuilder() {
        return (ChunkBuilderAccess)worldRender().getChunkBuilder();
    }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }
    private static LightingProvider serverLighting() { return ComplexData.world.getChunkManager().getLightingProvider(); }

    private static Integer chunk(WorldChunk chunk, Heightmap.Type type) {
        if (chunk == null) return null;
        BlockPos pos = client.getCameraEntity().getBlockPos();
        return chunk.sampleHeightmap(type, pos.getX(), pos.getZ());
    }

    private static Integer spawn(SpawnGroup group) {
        SpawnHelper.Info info = ComplexData.serverWorld.getChunkManager().getSpawnInfo();
        return info == null ? null : info.getGroupToCount().getInt(group);
    }


    public static final Supplier<Integer> FPS = MinecraftClientAccess::getCurrentFps;
    public static final Supplier<Integer> MAX_FPS = () -> client.options.getMaxFps().getValue() == GameOptions.MAX_FRAMERATE ? null : client.options.getMaxFps().getValue();
    public static final Supplier<Integer> BIOME_BLEND = () -> client.options.getBiomeBlendRadius().getValue();
    public static final Supplier<Integer> SIMULATION_DISTANCE = () -> client.options.getSimulationDistance().getValue();

    public static final Supplier<Integer> PACKETS_SENT = () -> (int)client.getNetworkHandler().getConnection().getAveragePacketsSent();
    public static final Supplier<Integer> PACKETS_RECEIVED = () -> (int)client.getNetworkHandler().getConnection().getAveragePacketsReceived();
    public static final Supplier<Integer> CHUNKS_RENDERED = () -> worldRender().getCompletedChunks();
    public static final Supplier<Integer> CHUNKS_LOADED = () -> worldRender().getChunks().chunks.length;
    @SuppressWarnings("Convert2MethodRef" )
    public static final Supplier<Integer> RENDER_DISTANCE = () -> client.options.getClampedViewDistance();
    public static final Supplier<Integer> QUEUED_TASKS = () -> chunkBuilder().getQueuedTaskCount();
    public static final Supplier<Integer> UPLOAD_QUEUE = () -> chunkBuilder().getUploadQueue().size();
    public static final Supplier<Integer> BUFFER_COUNT = () -> chunkBuilder().getBufferCount();
    public static final Supplier<Integer> ENTITIES_RENDERED = () -> worldRender().getRegularEntityCount();
    public static final Supplier<Integer> ENTITIES_LOADED = () -> client.world.getRegularEntityCount();

    public static final Supplier<Integer> FORCED_LOADED_CHUNKS = () -> ComplexData.world instanceof ServerWorld ? ((ServerWorld)ComplexData.world).getForcedChunks().size() : null;

    public static final Supplier<Integer> MS_TICKS = () -> client.getServer() == null ? null : (int)client.getServer().getTickTime();

    public static final Supplier<Integer> BLOCK_X = () -> blockPos().getX();
    public static final Supplier<Integer> BLOCK_Y = () -> blockPos().getY();
    public static final Supplier<Integer> BLOCK_Z = () -> blockPos().getZ();
    public static final Supplier<Integer> TARGET_BLOCK_X = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getX();
    public static final Supplier<Integer> TARGET_BLOCK_Y = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getY();
    public static final Supplier<Integer> TARGET_BLOCK_Z = () -> ComplexData.targetBlockPos == null ? null : ComplexData.targetBlockPos.getZ();
    public static final Supplier<Integer> TARGET_FLUID_X = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getX();
    public static final Supplier<Integer> TARGET_FLUID_Y = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getY();
    public static final Supplier<Integer> TARGET_FLUID_Z = () -> ComplexData.targetFluidPos == null ? null : ComplexData.targetFluidPos.getZ();
    public static final Supplier<Integer> TARGET_ENTITY_X = () -> client.targetedEntity == null ? null : client.targetedEntity.getBlockX();
    public static final Supplier<Integer> TARGET_ENTITY_Y = () -> client.targetedEntity == null ? null : client.targetedEntity.getBlockY();
    public static final Supplier<Integer> TARGET_ENTITY_Z = () -> client.targetedEntity == null ? null : client.targetedEntity.getBlockZ();

    //TODO: Make these Decimal ^ and v
    public static final Supplier<Integer> IN_CHUNK_X = () -> blockPos().getX() & 15;
    public static final Supplier<Integer> IN_CHUNK_Y = () -> blockPos().getY() & 15;
    public static final Supplier<Integer> IN_CHUNK_Z = () -> blockPos().getZ() & 15;
    public static final Supplier<Integer> CHUNK_X = () -> blockPos().getX() >> 4;
    public static final Supplier<Integer> CHUNK_Y = () -> blockPos().getY() >> 4;
    public static final Supplier<Integer> CHUNK_Z = () -> blockPos().getZ() >> 4;
    public static final Supplier<Integer> REGION_X = () -> blockPos().getX() >> 9;
    public static final Supplier<Integer> REGION_Z = () -> blockPos().getZ() >> 9;
    public static final Supplier<Integer> REGION_RELATIVE_X = () -> blockPos().getX() >> 4 & 0x1F;
    public static final Supplier<Integer> REGION_RELATIVE_Z = () -> blockPos().getZ() >> 4 & 0x1F;


    public static final Supplier<Integer> CLIENT_LIGHT = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getChunkManager().getLightingProvider().getLight(blockPos(), 0);
    public static final Supplier<Integer> CLIENT_LIGHT_SKY = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getLightLevel(LightType.SKY, blockPos());
    public static final Supplier<Integer> CLIENT_LIGHT_BLOCK = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getLightLevel(LightType.BLOCK, blockPos());
    public static final Supplier<Integer> SERVER_LIGHT_SKY = () -> ComplexData.serverChunk == null ? null : serverLighting().get(LightType.SKY).getLightLevel(blockPos());
    public static final Supplier<Integer> SERVER_LIGHT_BLOCK = () -> ComplexData.serverChunk == null ? null : serverLighting().get(LightType.BLOCK).getLightLevel(blockPos());

    public static final Supplier<Integer> CLIENT_HEIGHT_MAP_SURFACE = () -> chunk(ComplexData.clientChunk, Heightmap.Type.WORLD_SURFACE);
    public static final Supplier<Integer> CLIENT_HEIGHT_MAP_MOTION_BLOCKING = () -> chunk(ComplexData.clientChunk, Heightmap.Type.MOTION_BLOCKING);
    public static final Supplier<Integer> SERVER_HEIGHT_MAP_SURFACE = () -> chunk(ComplexData.serverChunk, Heightmap.Type.WORLD_SURFACE);
    public static final Supplier<Integer> SERVER_HEIGHT_MAP_OCEAN_FLOOR = () -> chunk(ComplexData.serverChunk, Heightmap.Type.OCEAN_FLOOR);
    public static final Supplier<Integer> SERVER_HEIGHT_MAP_MOTION_BLOCKING = () -> chunk(ComplexData.serverChunk, Heightmap.Type.MOTION_BLOCKING);
    public static final Supplier<Integer> SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES = () -> chunk(ComplexData.serverChunk, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);

    public static final Supplier<Integer> MOON_PHASE = () -> ComplexData.clientChunk.isEmpty() ? null : client.world.getMoonPhase();

    public static final Supplier<Integer> SPAWN_CHUNKS = () -> {
        SpawnHelper.Info info = ComplexData.serverWorld.getChunkManager().getSpawnInfo();
        return info == null ? null : info.getSpawningChunkCount();
    };
    public static final Supplier<Integer> MONSTERS = () -> spawn(SpawnGroup.MONSTER);
    public static final Supplier<Integer> CREATURES = () -> spawn(SpawnGroup.CREATURE);
    public static final Supplier<Integer> AMBIENT_MOBS = () -> spawn(SpawnGroup.AMBIENT);
    public static final Supplier<Integer> WATER_CREATURES = () -> spawn(SpawnGroup.WATER_CREATURE);
    public static final Supplier<Integer> WATER_AMBIENT_MOBS = () -> spawn(SpawnGroup.WATER_AMBIENT);
    public static final Supplier<Integer> UNDERGROUND_WATER_CREATURE = () -> spawn(SpawnGroup.UNDERGROUND_WATER_CREATURE);
    public static final Supplier<Integer> AXOLOTLS = () -> spawn(SpawnGroup.AXOLOTLS);
    public static final Supplier<Integer> MISC_MOBS = () -> spawn(SpawnGroup.MISC);

    public static final Supplier<Integer> JAVA_BIT = () -> client.is64Bit() ? 64 : 32;
    public static final Supplier<Integer> CPU_CORES = () -> ComplexData.cpu.getPhysicalProcessorCount();
    public static final Supplier<Integer> CPU_THREADS = () -> ComplexData.cpu.getLogicalProcessorCount();

    public static final Supplier<Integer> DISPLAY_WIDTH = () -> client.getWindow().getFramebufferWidth();
    public static final Supplier<Integer> DISPLAY_HEIGHT = () -> client.getWindow().getFramebufferHeight();
    public static final Supplier<Integer> DISPLAY_REFRESH_RATE = () -> GLX._getRefreshRate(client.getWindow());
    public static final Supplier<Integer> MODS = () -> FabricLoader.getInstance().getAllMods().size();
    public static final Supplier<Integer> PING = () -> client.player.networkHandler.getPlayerListEntry(client.player.getUuid()).getLatency();

    public static final Supplier<Integer> ITEM_DURABILITY = () -> client.player.getMainHandStack().getMaxDamage() - client.player.getMainHandStack().getDamage();
    public static final Supplier<Integer> ITEM_MAX_DURABILITY = () -> client.player.getMainHandStack().getMaxDamage();
    public static final Supplier<Integer> OFFHAND_ITEM_DURABILITY = () -> client.player.getOffHandStack().getMaxDamage() - client.player.getOffHandStack().getDamage();
    public static final Supplier<Integer> OFFHAND_ITEM_MAX_DURABILITY = () -> client.player.getOffHandStack().getMaxDamage();

    public static final Supplier<Integer> TIME_HOUR_12 = () -> {
        int hour = ComplexData.timeOfDay / 1000 % 12;
        return hour == 0 ? 12 : hour;
    };

    private final Supplier<Integer> supplier;

    public IntegerSupplierElement(Supplier<Integer> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getString() {
        try {
            Integer value = supplier.get();
            return value == null ? "-" : value.toString();
        }
        catch (Exception e) {
            return "-";
        }
    }

    @Override
    public Number getNumber() {
        try {
            Integer value = supplier.get();
            return value == null ? Double.NaN : value;
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }

    @Override
    public boolean getBoolean() {
        try {
            Integer value = supplier.get();
            return value != null && value > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

}
