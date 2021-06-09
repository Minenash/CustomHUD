package com.minenash.customhud.HudElements;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.mixin.ChunkBuilderAccess;
import com.minenash.customhud.mixin.WorldRendererAccess;
import com.minenash.customhud.mixin.MinecraftClientAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.Option;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.text.WordUtils;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public class SupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Runtime runtime = Runtime.getRuntime();
    private static WorldRendererAccess worldRender() {
        return (WorldRendererAccess)client.worldRenderer;
    }
    private static ChunkBuilderAccess chunkBuilder() {
        return (ChunkBuilderAccess)worldRender().getChunkBuilder();
    }
    private static Entity cameraEntity() { return client.getCameraEntity(); }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }
    private static boolean isInDim(Identifier id) { return client.world.getRegistryKey().getValue().equals(id); }
    private static LightingProvider serverLighting() { return ComplexData.world.getChunkManager().getLightingProvider(); }
    private static SpawnHelper.Info spawnInfo() { return ComplexData.serverWorld.getChunkManager().getSpawnInfo();}
    private static long toMiB(long bytes) {
        return bytes / 1024L / 1024L;
    }

    public static final Supplier<String> FPS = () -> Integer.toString(MinecraftClientAccess.getCurrentFps());
    public static final Supplier<String> MAX_FPS = () -> client.options.maxFps == Option.FRAMERATE_LIMIT.getMax() ? "inf" : Integer.toString(client.options.maxFps);
    public static final Supplier<String> VSYNC = () -> Boolean.toString(client.options.enableVsync);
    public static final Supplier<String> VERSION = () -> SharedConstants.getGameVersion().getName();
    public static final Supplier<String> CLIENT_VERSION = client::getGameVersion;
    public static final Supplier<String> MODDED_NAME = ClientBrandRetriever::getClientModName;
    public static final Supplier<String> GRAPHICS_MODE = () -> client.options.graphicsMode.toString().substring(1);
    public static final Supplier<String> CLOUDS = () -> client.options.cloudRenderMode == CloudRenderMode.OFF ? "" : (client.options.cloudRenderMode == CloudRenderMode.FAST ? " fast-clouds" : " fancy-clouds");
    public static final Supplier<String> BIOME_BLEND = () -> Integer.toString(client.options.biomeBlendRadius);
    public static final Supplier<String> MS_TICKS = () -> {
        IntegratedServer server = client.getServer();
        return server == null ? "" : Integer.toString((int)client.getServer().getTickTime());
    };
    public static final Supplier<String> TPS = () -> {
        IntegratedServer server = client.getServer();
        if (server == null) return "";
        int ms_tics = (int)client.getServer().getTickTime();
        return ms_tics < 50 ? "20" : Integer.toString(1000/ms_tics);
    };
    public static final Supplier<String> SERVER_BRAND = () -> client.player.getServerBrand();
    public static final Supplier<String> PACKETS_SENT = () -> Integer.toString((int)client.getNetworkHandler().getConnection().getAveragePacketsSent());
    public static final Supplier<String> PACKETS_RECEIVED = () -> Integer.toString((int)client.getNetworkHandler().getConnection().getAveragePacketsReceived());
    public static final Supplier<String> CHUNKS_RENDERED = () -> Integer.toString(worldRender().getCompletedChunks());
    public static final Supplier<String> CHUNKS_LOADED = () -> Integer.toString(worldRender().getChunks().chunks.length);
    public static final Supplier<String> CHUNK_CULLING = () -> Boolean.toString(client.chunkCullingEnabled);
    public static final Supplier<String> RENDER_DISTANCE = () -> Integer.toString(client.options.viewDistance);
    public static final Supplier<String> QUEUED_TASKS = () -> Integer.toString(chunkBuilder().getQueuedTaskCount());
    public static final Supplier<String> UPLOAD_QUEUE = () -> Integer.toString(chunkBuilder().getUploadQueue().size());
    public static final Supplier<String> BUFFER_COUNT = () -> Integer.toString(chunkBuilder().getBufferCount());
    public static final Supplier<String> ENTITIES_RENDERED = () -> Integer.toString(worldRender().getRegularEntityCount());
    public static final Supplier<String> ENTITIES_LOADED = () -> Integer.toString(client.world.getRegularEntityCount());
    public static final Supplier<String> PARTICLES = client.particleManager::getDebugString;
    public static final Supplier<String> DIMENSION = () -> WordUtils.capitalize(client.world.getRegistryKey().getValue().getPath().replace("_"," "));
    public static final Supplier<String> DIMENSION_ID = () -> client.world.getRegistryKey().getValue().toString();
    public static final Supplier<String> IN_OVERWORLD = () -> Boolean.toString(isInDim(DimensionType.OVERWORLD_ID));
    public static final Supplier<String> IN_NETHER = () -> Boolean.toString(isInDim(DimensionType.THE_NETHER_ID));
    public static final Supplier<String> IN_END = () -> Boolean.toString(isInDim(DimensionType.THE_END_ID));
    public static final Supplier<String> FORCED_LOADED_CHUNKS = () -> ComplexData.world instanceof ServerWorld ? Integer.toString(((ServerWorld)ComplexData.world).getForcedChunks().size()) : "0";
    public static final Supplier<String> X = () -> String.format("%.3f", cameraEntity().getX());
    public static final Supplier<String> Y = () -> String.format("%.3f", cameraEntity().getY());
    public static final Supplier<String> Z = () -> String.format("%.3f", cameraEntity().getZ());
    public static final Supplier<String> BLOCK_X = () -> Integer.toString(blockPos().getX());
    public static final Supplier<String> BLOCK_Y = () -> Integer.toString(blockPos().getY());
    public static final Supplier<String> BLOCK_Z = () -> Integer.toString(blockPos().getZ());
    public static final Supplier<String> NETHER_X = () -> Integer.toString(isInDim(DimensionType.THE_NETHER_ID) ? blockPos().getX() * 8 : blockPos().getX() / 8);
    public static final Supplier<String> NETHER_Z = () -> Integer.toString(isInDim(DimensionType.THE_NETHER_ID) ? blockPos().getZ() * 8 : blockPos().getZ() / 8);
    public static final Supplier<String> IN_CHUNK_X = () -> Integer.toString(blockPos().getX() & 15);
    public static final Supplier<String> IN_CHUNK_Y = () -> Integer.toString(blockPos().getY() & 15);
    public static final Supplier<String> IN_CHUNK_Z = () -> Integer.toString(blockPos().getZ() & 15);
    public static final Supplier<String> CHUNK_X = () -> Integer.toString(blockPos().getX() >> 4);
    public static final Supplier<String> CHUNK_Y = () -> Integer.toString(blockPos().getY() >> 4);
    public static final Supplier<String> CHUNK_Z = () -> Integer.toString(blockPos().getZ() >> 4);
    public static final Supplier<String> TARGET_X = () -> ComplexData.targetBlockPos == null ? "" : Integer.toString(ComplexData.targetBlockPos.getX());
    public static final Supplier<String> TARGET_Y = () -> ComplexData.targetBlockPos == null ? "" : Integer.toString(ComplexData.targetBlockPos.getY());
    public static final Supplier<String> TARGET_Z = () -> ComplexData.targetBlockPos == null ? "" : Integer.toString(ComplexData.targetBlockPos.getZ());

    public static final Supplier<String> VELOCITY_XZ = () -> Double.toString(ComplexData.velocityXZ);
    public static final Supplier<String> VELOCITY_Y = () -> Double.toString(ComplexData.velocityY);
    public static final Supplier<String> VELOCITY_XYZ = () -> Double.toString(ComplexData.velocityXYZ);
    public static final Supplier<String> VELOCITY_XZ_KMH = () -> String.format("%.1f", ComplexData.velocityXZ * 3.6);
    public static final Supplier<String> VELOCITY_Y_KMH = () -> String.format("%.1f", ComplexData.velocityY * 3.6);
    public static final Supplier<String> VELOCITY_XYZ_KMH = () -> String.format("%.1f", ComplexData.velocityXYZ * 3.6);

    public static final Supplier<String> FACING = () -> cameraEntity().getHorizontalFacing().getName();
    public static final Supplier<String> FACING_TOWARDS_XZ = () ->
            cameraEntity().getHorizontalFacing() == Direction.EAST || cameraEntity().getHorizontalFacing() == Direction.WEST ? "X" : "Z";
    public static final Supplier<String> FACING_TOWARDS_PN_WORD = () ->
            cameraEntity().getHorizontalFacing() == Direction.EAST || cameraEntity().getHorizontalFacing() == Direction.SOUTH ? "positive" : "negative";
    public static final Supplier<String> FACING_TOWARDS_PN_SIGN = () ->
            cameraEntity().getHorizontalFacing() == Direction.EAST || cameraEntity().getHorizontalFacing() == Direction.SOUTH ? "+" : "-";
    public static final Supplier<String> YAW = () -> String.format("%.1f", MathHelper.wrapDegrees(cameraEntity().yaw));
    public static final Supplier<String> PITCH = () -> String.format("%.1f", MathHelper.wrapDegrees(cameraEntity().pitch));
    public static final Supplier<String> CLIENT_LIGHT = () -> ComplexData.clientChunk.isEmpty() ? "?" :
            Integer.toString(client.world.getChunkManager().getLightingProvider().getLight(blockPos(), 0));
    public static final Supplier<String> CLIENT_LIGHT_SKY = () ->
            ComplexData.clientChunk.isEmpty() ? "?" : Integer.toString(client.world.getLightLevel(LightType.SKY, blockPos()));
    public static final Supplier<String> CLIENT_LIGHT_BLOCK = () ->
            ComplexData.clientChunk.isEmpty() ? "?" : Integer.toString(client.world.getLightLevel(LightType.BLOCK, blockPos()));
    public static final Supplier<String> SERVER_LIGHT_SKY = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(serverLighting().get(LightType.SKY).getLightLevel(blockPos()));
    public static final Supplier<String> SERVER_LIGHT_BLOCK = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(serverLighting().get(LightType.BLOCK).getLightLevel(blockPos()));
    public static final Supplier<String> CLIENT_HEIGHT_MAP_SURFACE = () ->
            ComplexData.clientChunk == null ? "?" : Integer.toString(ComplexData.clientChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> CLIENT_HEIGHT_MAP_MOTION_BLOCKING = () ->
            ComplexData.clientChunk == null ? "?" : Integer.toString(ComplexData.clientChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> SERVER_HEIGHT_MAP_SURFACE = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(ComplexData.serverChunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> SERVER_HEIGHT_MAP_OCEAN_FLOOR = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(ComplexData.serverChunk.sampleHeightmap(Heightmap.Type.OCEAN_FLOOR, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> SERVER_HEIGHT_MAP_MOTION_BLOCKING = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(ComplexData.serverChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES = () ->
            ComplexData.serverChunk == null ? "?" : Integer.toString(ComplexData.serverChunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos().getX(), blockPos().getZ()));
    public static final Supplier<String> BIOME = () -> WordUtils.capitalize(client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(blockPos())).getPath().replace("_", " "));
    public static final Supplier<String> BIOME_ID = () -> client.world.getRegistryManager().get(Registry.BIOME_KEY).getId(client.world.getBiome(blockPos())).toString();
    public static final Supplier<String> LOCAL_DIFFICULTY = () -> String.format("%.2f",  ComplexData.localDifficulty.getLocalDifficulty());
    public static final Supplier<String> CLAMPED_LOCAL_DIFFICULTY = () -> String.format("%.2f",  ComplexData.localDifficulty.getClampedLocalDifficulty());
    public static final Supplier<String> DAY = () -> Long.toString(client.world.getTimeOfDay() / 24000L);
    public static final Supplier<String> SPAWN_CHUNKS = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getSpawningChunkCount());
    public static final Supplier<String> MONSTERS = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.MONSTER));
    public static final Supplier<String> CREATURES = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.CREATURE));
    public static final Supplier<String> AMBIENT_MOBS = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.AMBIENT));
    public static final Supplier<String> WATER_CREATURES = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.WATER_CREATURE));
    public static final Supplier<String> WATER_AMBIENT_MOBS = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.WATER_AMBIENT));
    public static final Supplier<String> MISC_MOBS = () -> spawnInfo() == null ? "?" : Integer.toString(spawnInfo().getGroupToCount().getInt(SpawnGroup.MISC));
    public static final Supplier<String> STREAMING_SOUNDS = () -> ComplexData.sounds[0];
    public static final Supplier<String> MAX_STREAMING_SOUNDS = () -> ComplexData.sounds[1];
    public static final Supplier<String> STATIC_SOUNDS = () -> ComplexData.sounds[2];
    public static final Supplier<String> MAX_STATIC_SOUNDS = () -> ComplexData.sounds[3];
    public static final Supplier<String> MOOD = () -> Integer.toString(Math.round(client.player.getMoodPercentage() * 100.0F));
    public static final Supplier<String> JAVA_VERSION = () -> System.getProperty("java.version");
    public static final Supplier<String> JAVA_BIT = () -> client.is64Bit() ? "64bit" : "32bit";
    public static final Supplier<String> MEMORY_USED_PERCENTAGE = () -> Long.toString((runtime.totalMemory() - runtime.freeMemory())*100L / runtime.maxMemory());
    public static final Supplier<String> MEMORY_USED = () -> Long.toString(toMiB(runtime.totalMemory() - runtime.freeMemory()));
    public static final Supplier<String> TOTAL_MEMORY = () -> Long.toString(toMiB(runtime.maxMemory()));
    public static final Supplier<String> ALLOCATED_PERCENTAGE = () -> Long.toString(runtime.totalMemory() * 100 / runtime.maxMemory());
    public static final Supplier<String> ALLOCATED = () -> Long.toString(toMiB(runtime.totalMemory()));
    public static final Supplier<String> CLIENT_CHUNK_CACHE_CAPACITY = () -> ComplexData.clientChunkCache[0];
    public static final Supplier<String> CLIENT_CHUNK_CACHE = () -> ComplexData.clientChunkCache[1];
    public static final Supplier<String> SERVER_CHUNK_CACHE = () -> ComplexData.serverWorld == null ? "" : ComplexData.serverWorld.getDebugString().substring(18);
    public static final Supplier<String> DISPLAY_WIDTH = () -> Integer.toString(client.getWindow().getFramebufferWidth());
    public static final Supplier<String> DISPLAY_HEIGHT = () -> Integer.toString(client.getWindow().getFramebufferHeight());
    public static final Supplier<String> MODS = () -> Integer.toString(FabricLoader.getInstance().getAllMods().size());
    public static final Supplier<String> TIME_HOUR_12 = () -> {
        int hour = ComplexData.timeOfDay / 1000 % 12;
        return hour == 0 ? "12" : Integer.toString(hour);
    };
    public static final Supplier<String> TIME_HOUR_24 = () -> String.format("%02d",ComplexData.timeOfDay / 1000);
    public static final Supplier<String> TIME_MINUTE = () -> String.format("%02d",(int)((ComplexData.timeOfDay % 1000) / (1000/60F)));
    public static final Supplier<String> TIME_AM_PM = () -> ComplexData.timeOfDay < 12000 ? "am" : "pm";
    public static final Supplier<String> PING = () -> Integer.toString(client.player.networkHandler.getPlayerListEntry(client.player.getUuid()).getLatency());
    public static final Supplier<String> ADDRESS = () -> client.getCurrentServerEntry().address;
    //public static final Supplier<String> template = () -> "";client.getCurrentServerEntry().ping

    private final Supplier<String> supplier;

    public SupplierElement(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getString() {
        try {
            return supplier.get();
        } catch (Exception e) {
            return "";
        }
    }
}
