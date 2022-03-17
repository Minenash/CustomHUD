package com.minenash.customhud;

import com.minenash.customhud.HudElements.FormattedElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.supplier.*;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.minenash.customhud.HudElements.supplier.SpecialSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringIntSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.IntegerSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.DecimalSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.BooleanSupplierElement.*;

public class VariableParser {

    public static HudElement getSupplierElement(String inside, ComplexData.Enabled enabled) {
        String[] parts = inside.split(" ");

        Flags flags = getFlags(parts);
        HudElement raw = getRawSupplierElement(parts[0], enabled, flags.precision, flags.scale);

        if (flags.anyUsed())
            return new FormattedElement(raw, flags);
        
        return raw;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static HudElement getRawSupplierElement(String name, ComplexData.Enabled enabled, int precision, double scale) {

        Supplier supplier = getStringSupplier(name, enabled);
        if (supplier != null)
            return new StringSupplierElement(supplier);

        supplier = getBooleanSupplier(name);
        if (supplier != null)
            return new BooleanSupplierElement(supplier);

        supplier = getIntegerSupplier(name, enabled);
        if (supplier != null)
            return new IntegerSupplierElement(supplier);

        supplier = getStringIntSupplier(name, enabled);
        if (supplier != null)
            return new StringIntSupplierElement(supplier);

        DecimalSupplierElement.Entry entry = getDecimalSupplier(name, enabled);
        if (entry != null)
            return precision == -1 ? new DecimalSupplierElement(entry, scale) : new DecimalSupplierElement(entry, precision, scale);

        SpecialSupplierElement.Entry entry2 = getSpecialSupplierElements(name, enabled);
        if (entry2 != null)
            return new SpecialSupplierElement(entry2);

        return null;
    }

    private static final Pattern precision = Pattern.compile("-p(\\d+)");
    private static final Pattern scale = Pattern.compile("-s((\\d+)\\/(\\d+)|\\d+(\\.\\d+)?)");
    public static Flags getFlags(String[] parts) {
        Flags flags = new Flags();

        if (parts.length <= 1)
            return flags;

        for (int i = 1; i < parts.length; i++) {
            switch (parts[i]) {
                case "-uc", "-uppercase" -> flags.textCase = Flags.TextCase.UPPER;
                case "-lc", "-lowercase" -> flags.textCase = Flags.TextCase.LOWER;
                case "-tc", "-titlecase" -> flags.textCase = Flags.TextCase.TITLE;
                case "-sc", "-smallcaps" -> flags.smallCaps = true;
                case "-nd", "-nodashes" -> flags.noDelimiters = true;
                case "-f", "formatted" -> flags.formatted = true;
                default -> {
                    Matcher matcher = precision.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.precision = Integer.parseInt(matcher.group(1));
                        continue;
                    }
                    matcher = scale.matcher(parts[i]);
                    if (matcher.matches()) {
                        if (parts[i].contains("/"))
                            flags.scale = Integer.parseInt(matcher.group(2)) / (double) Integer.parseInt(matcher.group(3));
                        else
                            flags.scale = Double.parseDouble(matcher.group(1));
                    }
                }
            }
        }
        return flags;
    }

    private static Supplier<String> getStringSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "version" -> VERSION;
            case "client_version" -> CLIENT_VERSION;
            case "modded_name" -> MODDED_NAME;
            case "graphics_mode" -> GRAPHICS_MODE;
            case "dimension" -> DIMENSION;
            case "dimension_id" -> DIMENSION_ID;
            case "facing" -> FACING;
            case "facing_towards_xz" -> FACING_TOWARDS_XZ;
            case "facing_towards_pn_word" -> FACING_TOWARDS_PN_WORD;
            case "facing_towards_pn_sign" -> FACING_TOWARDS_PN_SIGN;
            case "biome" -> BIOME;
            case "biome_id" -> BIOME_ID;
            case "target_entity" -> TARGET_ENTITY;
            case "target_entity_id" -> TARGET_ENTITY_ID;
            case "address" -> ADDRESS;
            case "java_version" -> JAVA_VERSION;
            case "server_brand" -> SERVER_BRAND;
            case "am_pm" -> { enabled.time = true; yield TIME_AM_PM; }
            default -> null;
        };
    }

    private static Supplier<Boolean> getBooleanSupplier(String element) {
        return switch (element) {
            case "vsync" -> VSYNC;
            case "chunks_culling" -> CHUNK_CULLING;
            case "overworld" -> IN_OVERWORLD;
            case "nether" -> IN_NETHER;
            case "end" -> IN_END;
            case "slime_chunk" -> IS_SLIME_CHUNK;
            case "item_has_durability", "item_has_dur" -> ITEM_HAS_DURABILITY;
            case "offhand_item_has_durability", "oitem_has_dur" -> OFFHAND_ITEM_HAS_DURABILITY;
            default -> null;
        };
    }

    private static Supplier<Integer> getIntegerSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "fps" -> FPS;
            case "max_fps" -> MAX_FPS;
            case "biome_blend" -> BIOME_BLEND;
            case "ms_ticks" -> MS_TICKS;
            case "simulation_distance", "sd" -> SIMULATION_DISTANCE;
            case "packets_sent", "tx" -> PACKETS_SENT;
            case "packets_received", "rx" -> PACKETS_RECEIVED;
            case "chunks_rendered" -> CHUNKS_RENDERED;
            case "chunks_loaded" -> CHUNKS_LOADED;
            case "render_distance" -> RENDER_DISTANCE;
            case "queued_tasks" -> QUEUED_TASKS;
            case "upload_queue" -> UPLOAD_QUEUE;
            case "buffer_count" -> BUFFER_COUNT;
            case "entities_rendered" -> ENTITIES_RENDERED;
            case "entities_loaded" -> ENTITIES_LOADED;
            case "force_loaded_chunks", "fc" -> { enabled.world = true; yield FORCED_LOADED_CHUNKS; }
            case "block_x", "bx" -> BLOCK_X;
            case "block_y", "by" -> BLOCK_Y;
            case "block_z", "bz" -> BLOCK_Z;
            case "target_block_x", "target_x", "tbx" -> { enabled.world = true; enabled.targetBlock = true; yield TARGET_BLOCK_X; }
            case "target_block_y", "target_y", "tby" -> { enabled.world = true; enabled.targetBlock = true; yield TARGET_BLOCK_Y; }
            case "target_block_z", "target_z", "tbz" -> { enabled.world = true; enabled.targetBlock = true; yield TARGET_BLOCK_Z; }
            case "target_fluid_x", "tfx" -> { enabled.world = true; enabled.targetFluid = true; yield TARGET_FLUID_X; }
            case "target_fluid_y", "tfy" -> { enabled.world = true; enabled.targetFluid = true; yield TARGET_FLUID_Y; }
            case "target_fluid_z", "tfz" -> { enabled.world = true; enabled.targetFluid = true; yield TARGET_FLUID_Z; }
            case "target_entity_x", "tex" -> TARGET_ENTITY_X;
            case "target_entity_y", "tey" -> TARGET_ENTITY_Y;
            case "target_entity_z", "tez" -> TARGET_ENTITY_Z;
            case "in_chunk_x", "icx" -> IN_CHUNK_X;
            case "in_chunk_y", "icy" -> IN_CHUNK_Y;
            case "in_chunk_z", "icz" -> IN_CHUNK_Z;
            case "chunk_x", "cx" -> CHUNK_X;
            case "chunk_y", "cy" -> CHUNK_Y;
            case "chunk_z", "cz" -> CHUNK_Z;
            case "region_x", "rex" -> REGION_X;
            case "region_z", "rez" -> REGION_Z;
            case "region_relative_x", "rrx" -> REGION_RELATIVE_X;
            case "region_relative_z", "rrz" -> REGION_RELATIVE_Z;
            case "client_light", "light" -> { enabled.clientChunk = true; yield CLIENT_LIGHT; }
            case "client_light_sky", "light_sky" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_SKY; }
            case "client_light_block", "light_block" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_BLOCK; }
            case "server_light_sky" -> { enabled.world = true; enabled.serverChunk = true; yield SERVER_LIGHT_SKY; }
            case "server_light_block" -> { enabled.world = true; enabled.serverChunk = true; yield SERVER_LIGHT_BLOCK; }
            case "client_height_map_surface", "chs" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_SURFACE; }
            case "client_height_map_motion_blocking", "chm" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_surface", "shs" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_SURFACE; }
            case "server_height_map_ocean_floor", "sho" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_OCEAN_FLOOR; }
            case "server_height_map_motion_blocking", "shm" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_motion_blocking_no_leaves", "shml" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES; }
            case "spawn_chunks", "sc" -> { enabled.serverWorld = true; yield SPAWN_CHUNKS; }
            case "monsters" -> { enabled.serverWorld = true; yield MONSTERS; }
            case "creatures" -> { enabled.serverWorld = true; yield CREATURES; }
            case "ambient_mobs" -> { enabled.serverWorld = true; yield AMBIENT_MOBS; }
            case "water_creatures" -> { enabled.serverWorld = true; yield WATER_CREATURES; }
            case "water_ambient_mobs" -> { enabled.serverWorld = true; yield WATER_AMBIENT_MOBS; }
            case "misc_mobs" -> { enabled.serverWorld = true; yield MISC_MOBS; }
            case "java_bit" -> JAVA_BIT;
            case "display_width" -> DISPLAY_WIDTH;
            case "display_height" -> DISPLAY_HEIGHT;
            case "mods" -> MODS;
            case "ping" -> PING;
            case "item_durability", "item_dur" -> ITEM_DURABILITY;
            case "item_max_durability", "item_max_dur" -> ITEM_MAX_DURABILITY;
            case "offhand_item_durability", "oitem_dur" -> OFFHAND_ITEM_DURABILITY;
            case "offhand_item_max_durability", "oitem_max_dur" -> OFFHAND_ITEM_MAX_DURABILITY;
            case "hour12" -> { enabled.time = true; yield TIME_HOUR_12; }
            default -> null;
        };
    }

    private static Supplier<String> getStringIntSupplier(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "particles", "p": return PARTICLES;
            case "streaming_sounds", "sounds": enabled.sound = true; return STREAMING_SOUNDS;
            case "max_streaming_sonds", "max_sounds": enabled.sound = true; return MAX_STREAMING_SOUNDS;
            case "static_sounds": enabled.sound = true; return STATIC_SOUNDS;
            case "max_static_sounds": enabled.sound = true; return MAX_STATIC_SOUNDS;
            case "client_chunk_cache_capacity": enabled.clientChunkCache = true; return CLIENT_CHUNK_CACHE_CAPACITY;
            case "client_chunk_cache": enabled.clientChunkCache = true; return CLIENT_CHUNK_CACHE;
            case "server_chunk_cache": enabled.serverWorld = true; return SERVER_CHUNK_CACHE;
            default: return null;
        }
    }

    private static DecimalSupplierElement.Entry getDecimalSupplier(String element, ComplexData.Enabled enabled) {
        if (element.startsWith("velocity_"))
            enabled.velocity = true;
        return switch (element) {
            case "x" -> X;
            case "y" -> Y;
            case "z" -> Z;
            case "nether_x", "nx" -> NETHER_X;
            case "nether_z", "nz" -> NETHER_Z;
            case "velocity_xz" -> VELOCITY_XZ;
            case "velocity_y" -> VELOCITY_Y;
            case "velocity_xyz" -> VELOCITY_XYZ;
            case "velocity_xz_kmh" -> VELOCITY_XZ_KMH;
            case "velocity_y_kmh" -> VELOCITY_Y_KMH;
            case "velocity_xyz_kmh" -> VELOCITY_XYZ_KMH;
            case "yaw" -> YAW;
            case "pitch" -> PITCH;
            case "day" -> DAY;
            case "mode" -> MOOD;
            case "tps" -> TPS;
            case "memory_used_percentage" -> MEMORY_USED_PERCENTAGE;
            case "memory_used" -> MEMORY_USED;
            case "total_memory" -> TOTAL_MEMORY;
            case "allocated_percentage" -> ALLOCATED_PERCENTAGE;
            case "allocated" -> ALLOCATED;
            case "cpu_usage", "cpu" -> {enabled.cpu = true; yield CPU_USAGE;}
            case "item_durability_percent", "item_dur_per" -> ITEM_DURABILITY_PERCENT;
            case "offhand_item_durability_percent", "oitem_dur_per" -> OFFHAND_ITEM_DURABILITY_PERCENT;
            case "local_difficulty" -> { enabled.localDifficulty = true; yield LOCAL_DIFFICULTY; }
            case "clamped_local_difficulty" -> { enabled.localDifficulty = true; yield CLAMPED_LOCAL_DIFFICULTY; }
            default -> null;
        };
    }

    private static SpecialSupplierElement.Entry getSpecialSupplierElements(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "hour24": { enabled.time = true; return TIME_HOUR_24; }
            case "minute": { enabled.time = true; return TIME_MINUTE; }
            case "target_block": {enabled.world = true; enabled.targetBlock = true; return TARGET_BLOCK;}
            case "target_block_id": {enabled.world = true; enabled.targetBlock = true; return TARGET_BLOCK_ID;}
            case "target_fluid": {enabled.world = true; enabled.targetFluid = true; return TARGET_FLUID;}
            case "target_fluid_id": {enabled.world = true; enabled.targetFluid = true; return TARGET_FLUID_ID;}
            case "item": return ITEM;
            case "item_id": return ITEM_ID;
            case "offhand_item", "oitem": return OFFHAND_ITEM;
            case "offhand_item_id", "oitem_id": return OFFHAND_ITEM_ID;
            case "clouds": return CLOUDS;
            default: return null;
        }
    }
    
}
