package com.minenash.customhud.mc1_20;

import com.minenash.customhud.core.ProfileHandler;
import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.elements.FormattedElement;
import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.HudElement;
import com.minenash.customhud.core.elements.ToggleElement;
import com.minenash.customhud.core.errors.ErrorType;
import com.minenash.customhud.core.errors.Errors;
import com.minenash.customhud.core.registry.VariableParseContext;
import com.minenash.customhud.mc1_20.elements.ItemCountElement;
import com.minenash.customhud.mc1_20.elements.SettingsElement;
import com.minenash.customhud.mc1_20.elements.SlotItemElement;
import com.minenash.customhud.mc1_20.elements.icon.DebugGizmoElement;
import com.minenash.customhud.mc1_20.elements.icon.ItemIconElement;
import com.minenash.customhud.mc1_20.elements.icon.TextureIconElement;
import com.minenash.customhud.mc1_20.elements.stats.CustomStatElement;
import com.minenash.customhud.mc1_20.elements.stats.TypedStatElement;
import com.minenash.customhud.mc1_20.elements.supplier.DecimalSuppliers;
import com.minenash.customhud.mc1_20.elements.supplier.SpecialSuppliers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.minenash.customhud.core.data.Enabled.*;
import static com.minenash.customhud.core.registry.VariableRegistry.SupplierEntryType.*;
import static com.minenash.customhud.core.registry.VariableRegistry.*;
import static com.minenash.customhud.mc1_20.elements.supplier.BooleanSuppliers.*;
import static com.minenash.customhud.mc1_20.elements.supplier.DecimalSuppliers.*;
import static com.minenash.customhud.mc1_20.elements.supplier.IntegerSuppliers.*;
import static com.minenash.customhud.mc1_20.elements.supplier.SpecialSuppliers.*;
import static com.minenash.customhud.mc1_20.elements.supplier.StringIntSuppliers.*;
import static com.minenash.customhud.mc1_20.elements.supplier.StringSuppliers.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Variables {

    private static final Pattern TEXTURE_ICON_PATTERN = Pattern.compile("((?:[a-z0-9/._-]+:)?[a-z0-9/._-]+)(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?");

    public static void registerVars() {

        register(NONE, BOOLEAN, VSYNC, "vsync");
        register(NONE, BOOLEAN, SINGLEPLAYER, "sp", "singleplayer");
        register(NONE, BOOLEAN, MULTIPLAYER, "mp", "multiplayer");
        register(NONE, BOOLEAN, CHUNK_CULLING, "chunks_culling");
        register(NONE, BOOLEAN, IN_OVERWORLD, "overworld");
        register(NONE, BOOLEAN, IN_NETHER, "nether");
        register(NONE, BOOLEAN, IN_END, "end");
        register(WORLD, BOOLEAN, IS_RAINING, "raining");
        register(WORLD, BOOLEAN, IS_THUNDERING, "thundering");
        register(WORLD, BOOLEAN, IS_SNOWING, "snowing");
        register(WORLD, BOOLEAN, IS_SLIME_CHUNK, "slime_chunk");
        register(NONE, BOOLEAN, SPRINTING, "sprinting");
        register(NONE, BOOLEAN, SNEAKING, "sneaking");
        register(NONE, BOOLEAN, SWIMMING, "swimming");
        register(NONE, BOOLEAN, ON_GROUND, "on_ground");
        register(NONE, BOOLEAN, ITEM_HAS_DURABILITY, "item_has_durability", "item_has_dur");
        register(NONE, BOOLEAN, OFFHAND_ITEM_HAS_DURABILITY, "offhand_item_has_durability", "oitem_has_dur");
        register(NONE, BOOLEAN, FISHING_IS_CAST, "fishing_is_cast");
        register(NONE, BOOLEAN, FISHING_IS_HOOKED, "fishing_is_hooked");
        register(NONE, BOOLEAN, FISHING_HAS_CAUGHT, "fishing_has_caught");
        register(NONE, BOOLEAN, FISHING_IN_OPEN_WATER, "fishing_in_open_water");

        register(NONE, STRING, VERSION, "version");
        register(NONE, STRING, CLIENT_VERSION, "client_version");
        register(NONE, STRING, MODDED_NAME, "modded_name");
        register(NONE, STRING, DISPLAY_NAME, "display_name", "name");
        register(NONE, STRING, USERNAME, "username");
        register(NONE, STRING, UUID, "uuid");
        register(NONE, STRING, DIMENSION, "dimension");
        register(NONE, STRING, DIMENSION_ID, "dimension_id");
        register(NONE, STRING, FACING, "facing");
        register(NONE, STRING, FACING_SHORT, "facing_short");
        register(NONE, STRING, FACING_TOWARDS_XZ, "facing_towards_xz");
        register(NONE, STRING, BIOME, "biome");
        register(NONE, STRING, BIOME_ID, "biome_id");
        register(CLIENT_CHUNK, STRING, MOON_PHASE_WORD, "moon_phase_word");
        register(NONE, STRING, TARGET_ENTITY, "target_entity", "te");
        register(NONE, STRING, TARGET_ENTITY_ID, "target_entity_id", "tei");
        register(NONE, STRING, TARGET_ENTITY_NAME, "target_entity_name", "ten");
        register(NONE, STRING, TARGET_ENTITY_UUID, "target_entity_uuid", "teu");
        register(NONE, STRING, HOOKED_ENTITY, "hooked_entity", "he");
        register(NONE, STRING, HOOKED_ENTITY_ID, "hooked_entity_id", "hei");
        register(NONE, STRING, HOOKED_ENTITY_NAME, "hooked_entity_name", "hen");
        register(NONE, STRING, HOOKED_ENTITY_UUID, "hooked_entity_uuid", "heu");
        register(NONE, STRING, WORLD_NAME, "world_name", "world");
        register(NONE, STRING, SERVER_NAME, "server_name");
        register(NONE, STRING, SERVER_ADDRESS, "server_address", "address", "ip");
        register(NONE, STRING, SERVER_BRAND, "server_brand");
        register(NONE, STRING, JAVA_VERSION, "java_version");
        register(NONE, STRING, CPU_NAME, "cpu_name");
        register(NONE, STRING, GPU_NAME, "gpu_name");
        register(TIME, STRING, TIME_AM_PM, "am_pm");

        register(NONE, STR_INT, PARTICLES, "particles", "p");
        register(SOUND, STR_INT, STREAMING_SOUNDS, "streaming_sounds", "sounds");
        register(SOUND, STR_INT, MAX_STREAMING_SOUNDS, "max_streaming_sounds", "max_sounds");
        register(SOUND, STR_INT, STATIC_SOUNDS, "static_sounds");
        register(SOUND, STR_INT, MAX_STATIC_SOUNDS, "max_static_sounds");

        register(NONE, INT, FPS, "fps");
        register(NONE, INT, MAX_FPS, "max_fps");
        register(NONE, INT, BIOME_BLEND, "biome_blend");
        register(NONE, INT, SIMULATION_DISTANCE, "simulation_distance", "sd");
        register(NONE, INT, PACKETS_SENT, "packets_sent", "tx");
        register(NONE, INT, PACKETS_RECEIVED, "packets_received", "rx");
        register(NONE, INT, CHUNKS_RENDERED, "chunks_rendered");
        register(NONE, INT, CHUNKS_LOADED, "chunks_loaded");
        register(NONE, INT, RENDER_DISTANCE, "render_distance");
        register(NONE, INT, QUEUED_TASKS, "queued_tasks");
        register(NONE, INT, UPLOAD_QUEUE, "upload_queue");
        register(NONE, INT, BUFFER_COUNT, "buffer_count");
        register(NONE, INT, ENTITIES_RENDERED, "entities_rendered");
        register(NONE, INT, ENTITIES_LOADED, "entities_loaded");
        register(WORLD, INT, FORCED_LOADED_CHUNKS, "force_loaded_chunks", "fc");
        register(NONE, INT, BLOCK_X, "block_x", "bx");
        register(NONE, INT, BLOCK_Y, "block_y", "by");
        register(NONE, INT, BLOCK_Z, "block_z", "bz");
        register(WORLD | Enabled.TARGET_BLOCK, INT, TARGET_BLOCK_X, "target_block_x", "target_x", "tbx");
        register(WORLD | Enabled.TARGET_BLOCK, INT, TARGET_BLOCK_Y, "target_block_y", "target_y", "tby");
        register(WORLD | Enabled.TARGET_BLOCK, INT, TARGET_BLOCK_Z, "target_block_z", "target_z", "tbz");
        register(WORLD | Enabled.TARGET_BLOCK, INT, TARGET_BLOCK_DISTANCE, "target_block_distance", "target_distance", "tbd");
        register(WORLD | Enabled.TARGET_FLUID, INT, TARGET_FLUID_X, "target_fluid_x", "tfx");
        register(WORLD | Enabled.TARGET_FLUID, INT, TARGET_FLUID_Y, "target_fluid_y", "tfy");
        register(WORLD | Enabled.TARGET_FLUID, INT, TARGET_FLUID_Z, "target_fluid_z", "tfz");
        register(WORLD | Enabled.TARGET_FLUID, INT, TARGET_FLUID_DISTANCE, "target_fluid_distance", "tfd");
        register(NONE, INT, IN_CHUNK_X, "in_chunk_x", "icx");
        register(NONE, INT, IN_CHUNK_Y, "in_chunk_y", "icy");
        register(NONE, INT, IN_CHUNK_Z, "in_chunk_z", "icz");
        register(NONE, INT, CHUNK_X, "chunk_x", "cx");
        register(NONE, INT, CHUNK_Y, "chunk_y", "cy");
        register(NONE, INT, CHUNK_Z, "chunk_z", "cz");
        register(NONE, INT, REGION_X, "region_x", "rex");
        register(NONE, INT, REGION_Z, "region_z", "rez");
        register(NONE, INT, REGION_RELATIVE_X, "region_relative_x", "rrx");
        register(NONE, INT, REGION_RELATIVE_Z, "region_relative_z", "rrz");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT, "client_light", "light");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_SKY, "client_light_sky", "light_sky");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_SUN, "client_light_sun", "light_sun");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_BLOCK, "client_light_block", "light_block");
        register(WORLD | SERVER_CHUNK, INT, SERVER_LIGHT_SKY, "server_light_sky");
        register(WORLD | SERVER_CHUNK, INT, SERVER_LIGHT_BLOCK, "server_light_block");
        register(CLIENT_CHUNK, INT, CLIENT_HEIGHT_MAP_SURFACE, "client_height_map_surface", "chs");
        register(CLIENT_CHUNK, INT, CLIENT_HEIGHT_MAP_MOTION_BLOCKING, "client_height_map_motion_blocking", "chm");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_SURFACE, "server_height_map_surface", "shs");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_OCEAN_FLOOR, "server_height_map_ocean_floor", "sho");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_MOTION_BLOCKING, "server_height_map_motion_blocking", "shm");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES, "server_height_map_motion_blocking_no_leaves", "shml");
        register(CLIENT_CHUNK, INT, MOON_PHASE, "moon_phase");
        register(SERVER_WORLD, INT, SPAWN_CHUNKS, "spawn_chunks", "sc");
        register(SERVER_WORLD, INT, MONSTERS, "monsters");
        register(SERVER_WORLD, INT, CREATURES, "creatures");
        register(SERVER_WORLD, INT, AMBIENT_MOBS, "ambient_mobs");
        register(SERVER_WORLD, INT, WATER_CREATURES, "water_creatures");
        register(SERVER_WORLD, INT, WATER_AMBIENT_MOBS, "water_ambient_mobs");
        register(SERVER_WORLD, INT, UNDERGROUND_WATER_CREATURE, "underground_water_creatures");
        register(SERVER_WORLD, INT, AXOLOTLS, "axolotls");
        register(SERVER_WORLD, INT, MISC_MOBS, "misc_mobs");
        register(NONE, INT, JAVA_BIT, "java_bit");
        register(NONE, INT, CPU_CORES, "cpu_cores");
        register(NONE, INT, CPU_THREADS, "cpu_threads");
        register(NONE, INT, DISPLAY_WIDTH, "display_width");
        register(NONE, INT, DISPLAY_HEIGHT, "display_height");
        register(NONE, INT, DISPLAY_REFRESH_RATE, "display_refresh_rate");
        register(NONE, INT, MODS, "mods");
        register(NONE, INT, PING, "ping");
        register(NONE, INT, SOLAR_TIME, "time", "solar_time");
        register(NONE, INT, LUNAR_TIME, "lunar_time");
        register(NONE, INT, ITEM_DURABILITY, "item_durability", "item_dur");
        register(NONE, INT, ITEM_MAX_DURABILITY, "item_max_durability", "item_max_dur");
        register(NONE, INT, OFFHAND_ITEM_DURABILITY, "offhand_item_durability", "oitem_dur");
        register(NONE, INT, OFFHAND_ITEM_MAX_DURABILITY, "offhand_item_max_durability", "oitem_max_dur");
        register(TIME, INT, TIME_HOUR_12, "hour12", "hour");
        register(CLICKS_PER_SECONDS, INT, LCPS, "left_clicks_per_second", "lcps");
        register(CLICKS_PER_SECONDS, INT, RCPS, "right_clicks_per_second", "rcps");

        register(NONE, DEC, X, "x");
        register(NONE, DEC, Y, "y");
        register(NONE, DEC, Z, "z");
        register(NONE, DEC, NETHER_X, "nether_x", "nx");
        register(NONE, DEC, NETHER_Z, "nether_z", "nz");
        register(NONE, DEC, TARGET_ENTITY_X, "target_entity_x", "tex");
        register(NONE, DEC, TARGET_ENTITY_Y, "target_entity_y", "tey");
        register(NONE, DEC, TARGET_ENTITY_Z, "target_entity_z", "tez");
        register(NONE, DEC, TARGET_ENTITY_DISTANCE, "target_entity_distance", "ted");
        register(NONE, DEC, HOOKED_ENTITY_X, "hooked_entity_x", "hex");
        register(NONE, DEC, HOOKED_ENTITY_Y, "hooked_entity_y", "hey");
        register(NONE, DEC, HOOKED_ENTITY_Z, "hooked_entity_z", "hez");
        register(NONE, DEC, HOOKED_ENTITY_DISTANCE, "hooked_entity_distance", "hed");
        register(NONE, DEC, REACH_DISTANCE, "reach_distance", "reach");
        register(NONE, DEC, FISHING_HOOK_DISTANCE, "fishing_hook_distance");
        register(VELOCITY, DEC, VELOCITY_XZ, "velocity_xz");
        register(VELOCITY, DEC, VELOCITY_Y, "velocity_y");
        register(VELOCITY, DEC, VELOCITY_XYZ, "velocity_xyz");
        register(VELOCITY, DEC, VELOCITY_XZ_KMH, "velocity_xz_kmh");
        register(VELOCITY, DEC, VELOCITY_Y_KMH, "velocity_y_kmh");
        register(VELOCITY, DEC, VELOCITY_XYZ_KMH, "velocity_xyz_kmh");
        register(NONE, DEC, YAW, "yaw");
        register(NONE, DEC, PITCH, "pitch");
        register(NONE, DEC, DAY, "day");
        register(NONE, DEC, MOOD, "mood");
        register(NONE, DEC, TPS, "tps");
        register(NONE, DEC, MEMORY_USED_PERCENTAGE, "memory_used_percentage");
        register(NONE, DEC, MEMORY_USED, "memory_used");
        register(NONE, DEC, TOTAL_MEMORY, "memory_total");
        register(NONE, DEC, ALLOCATED_PERCENTAGE, "memory_allocated_percentage");
        register(NONE, DEC, ALLOCATED, "memory_allocated");
        register(CPU, DEC, CPU_USAGE, "cpu_usage", "cpu");
        register(PERFORMANCE_METRICS, DEC, GPU_USAGE, "gpu_usage", "gpu");
        register(NONE, DEC, TICK_MS, "ms_ticks", "tick_ms");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_MIN, "frame_ms_min");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_MAX, "frame_ms_max");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_AVG, "frame_ms_avg");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_SAMPLES, "frame_ms_samples");
        register(NONE, DEC, ITEM_DURABILITY_PERCENT, "item_durability_percent", "item_dur_per");
        register(NONE, DEC, OFFHAND_ITEM_DURABILITY_PERCENT, "offhand_item_durability_percent", "oitem_dur_per");
        register(Enabled.LOCAL_DIFFICULTY, DEC, DecimalSuppliers.LOCAL_DIFFICULTY, "local_difficulty");
        register(Enabled.LOCAL_DIFFICULTY, DEC, CLAMPED_LOCAL_DIFFICULTY, "clamped_local_difficulty");

        register(TIME, SPECIAL, TIME_HOUR_24, "hour24");
        register(TIME, SPECIAL, TIME_MINUTES, "minute");
        register(TIME, SPECIAL, TIME_SECONDS, "second");
        register(WORLD | Enabled.TARGET_BLOCK, SPECIAL, SpecialSuppliers.TARGET_BLOCK, "target_block", "tb");
        register(WORLD | Enabled.TARGET_BLOCK, SPECIAL, TARGET_BLOCK_ID, "target_block_id", "tbi");
        register(WORLD | Enabled.TARGET_FLUID, SPECIAL, SpecialSuppliers.TARGET_FLUID, "target_fluid", "tf");
        register(WORLD | Enabled.TARGET_FLUID, SPECIAL, TARGET_FLUID_ID, "target_fluid_id", "tfi");
        register(NONE, SPECIAL, ITEM, "item");
        register(NONE, SPECIAL, ITEM_NAME, "item_name");
        register(NONE, SPECIAL, ITEM_ID, "item_id");
        register(NONE, SPECIAL, OFFHAND_ITEM, "offhand_item", "oitem");
        register(NONE, SPECIAL, OFFHAND_ITEM_NAME, "offhand_item_name");
        register(NONE, SPECIAL, OFFHAND_ITEM_ID, "offhand_item_id", "oitem_id");
        register(NONE, SPECIAL, CLOUDS, "clouds");
        register(NONE, SPECIAL, GRAPHICS_MODE, "graphics_mode");
        register(NONE, SPECIAL, FACING_TOWARDS_PN_WORD, "facing_towards_pn_word");
        register(NONE, SPECIAL, FACING_TOWARDS_PN_SIGN, "facing_towards_pn_sign");





        register("customhud:stat", context -> {
            if (!context.startsWith("stat:")) return null;

            String stat = context.base().substring(5);

            HudElement element = stat("mined:",   Stats.MINED,   Registries.BLOCK, stat, context);
            if (element == null) element = stat("crafted:", Stats.CRAFTED, Registries.ITEM,  stat, context);
            if (element == null) element = stat("used:",    Stats.USED,    Registries.ITEM,  stat, context);
            if (element == null) element = stat("broken:",  Stats.BROKEN,  Registries.ITEM,  stat, context);
            if (element == null) element = stat("dropped:", Stats.DROPPED, Registries.ITEM,  stat, context);
            if (element == null) element = stat("picked_up:", Stats.PICKED_UP, Registries.ITEM, stat, context);
            if (element == null) element = stat("killed:",    Stats.KILLED,    Registries.ENTITY_TYPE, stat, context);
            if (element == null) element = stat("killed_by:", Stats.KILLED_BY, Registries.ENTITY_TYPE, stat, context);

            if (element != null)
                return element;

            Identifier statId = Registries.CUSTOM_STAT.get(new Identifier(stat));
            if (Stats.CUSTOM.hasStat(statId)) {
                context.enabled().add(Enabled.UPDATE_STATS);
                return new CustomStatElement(Stats.CUSTOM.getOrCreateStat(statId), context.flags());
            }
            Errors.addError(context, ErrorType.UNKNOWN_STATISTIC, stat);
            return new FunctionalElement.Error();
        });

        register("customhud:itemcount", context -> {
            if (!context.startsWith("itemcount:")) return null;

            String itemStr = context.base().substring(context.base().indexOf(':')+1);
            Item item = Registries.ITEM.get( Identifier.tryParse(itemStr));

            if (item != Items.AIR)
                return new ItemCountElement(item);
            Errors.addError(context, ErrorType.UNKNOWN_ITEM_ID, itemStr);
            return new FunctionalElement.Error();

        });

        register("customhud:item", context -> {
            if (!context.startsWith("item:")) return null;

            int firstCollinIndex = context.base().indexOf(':', 6);

            String slot = firstCollinIndex == -1? context.base().substring(5) : context.base().substring(5, firstCollinIndex);
            String method = firstCollinIndex == -1? "" : context.base().substring(firstCollinIndex+1);
            Pair<HudElement,ErrorType> element = SlotItemElement.create(slot, method, context.flags());

            if (element.getRight() != null) {
                Errors.addError(context, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ITEM_PROPERTY ? method : slot);
                return new FunctionalElement.Error();
            }
            return element.getLeft();
        });

        register("customhud:setting", context -> {
            if (!context.startsWith("setting:") && !context.startsWith("s:")) return null;

            String setting = context.base().substring(context.base().indexOf(':') + 1).toLowerCase();
            Pair<HudElement, Pair<ErrorType,String>> element = SettingsElement.create(setting, context.flags());

            if (element.getRight() != null) {
                Errors.addError(context, element.getRight().getLeft(), element.getRight().getRight());
                return new FunctionalElement.Error();
            }
            return context.flags().anyTextUsed() ? new FormattedElement(element.getLeft(), context.flags()) : element.getLeft();

        });

        register("customhud:icon", context -> {
            if (!context.startsWith("icon:")) return null;

            String base = context.base().substring(context.base().indexOf(':')+1);

            Item item = Registries.ITEM.get(Identifier.tryParse(base));
            if (item != Items.AIR)
                return new ItemIconElement(new ItemStack(item), context.flags());

            Matcher matcher = TEXTURE_ICON_PATTERN.matcher(base);
            if (!matcher.matches()) {
                Errors.addError(context, ErrorType.UNKNOWN_ICON, base);
                return new FunctionalElement.Error();
            }

            Identifier id = new Identifier(matcher.group(1) + (matcher.group(1).endsWith(".png") ? "" : ".png"));
            int u = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            int v = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
            int w = matcher.group(4) == null ? -1 : Integer.parseInt(matcher.group(4));
            int h = matcher.group(5) == null ? -1 : Integer.parseInt(matcher.group(5));

            TextureIconElement element = new TextureIconElement(id, u, v, w, h, context.flags());
            if (!element.isIconAvailable())
                Errors.addError(context, ErrorType.UNKNOWN_ICON, id.toString());
            return element;

        });

        register("customhud:gizmo", context ->
            context.base().equals("gizmo") ? new DebugGizmoElement(context.flags()) : null
        );
    }

    private static HudElement stat(String prefix, StatType<?> type, Registry<?> registry, String stat, VariableParseContext context) {
        if (!stat.startsWith(prefix))
            return null;

        Optional<?> entry = registry.getOrEmpty( new Identifier(stat.substring(prefix.length())) );
        if (entry.isPresent()) {
            context.enabled().add(Enabled.UPDATE_STATS);
            return new TypedStatElement(type, entry.get(), context.flags());
        }

        return null;
    }

}
