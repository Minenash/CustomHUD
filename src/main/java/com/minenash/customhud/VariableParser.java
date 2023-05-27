package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.icon.DebugGizmoElement;
import com.minenash.customhud.HudElements.icon.ItemIconElement;
import com.minenash.customhud.HudElements.icon.SpaceElement;
import com.minenash.customhud.HudElements.icon.TextureIconElement;
import com.minenash.customhud.HudElements.stats.CustomStatElement;
import com.minenash.customhud.HudElements.stats.TypedStatElement;
import com.minenash.customhud.HudElements.supplier.*;
import com.minenash.customhud.conditionals.ConditionalParser;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.data.HudTheme;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.mod_compat.CustomHudRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.minenash.customhud.HudElements.supplier.BooleanSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.DecimalSuppliers.*;
import static com.minenash.customhud.HudElements.supplier.IntegerSuppliers.*;
import static com.minenash.customhud.HudElements.supplier.SpecialSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringIntSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringSupplierElement.*;

public class VariableParser {

    private static final Pattern LINE_PARING_PATTERN = Pattern.compile("([^{}&]*)(\\{\\{(?:.*?, ?([\"']).*?\\3 ?)?}}|&?\\{.*?})?");
    private static final Pattern CONDITIONAL_PARSING_PATTERN = Pattern.compile("(.*?), ?\"(.*?)\"");
    private static final Pattern CONDITIONAL_PARSING_ALT_PATTERN = Pattern.compile("(.*?), ?'(.*?)'");
    private static final Pattern TEXTURE_ICON_PATTERN = Pattern.compile("((?:[a-z0-9/._-]+:)?[a-z0-9/._-]+)(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?");
    private static final Pattern HEX_COLOR_VARIABLE_PATTERN = Pattern.compile("&\\{(?:0x|#)?([0-9a-fA-F]{3,8})}");

    public static List<HudElement> addElements(String str, int profile, int debugLine, ComplexData.Enabled enabled, boolean line) {
        List<String> parts = new ArrayList<>();

//        System.out.println("[Line " + debugLine+ "] '" + str + "'");
        Matcher matcher = LINE_PARING_PATTERN.matcher(str);
        while (matcher.find()) {
            String left = matcher.group(1);

            List<String> sections = new ArrayList<>();
            int j = 0;
            for (int i = 0; i < left.length()-1; i++) {
                if (left.charAt(i) == '\\' && left.charAt(i+1) == 'n') {
                    sections.add(left.substring(j,i));
                    sections.add("\n");
                    i+=2;
                    j=i;
                }
            }
            sections.add(left.substring(j));
//            System.out.println(sections + "\n");
            parts.addAll(sections);

            parts.add(matcher.group(2));
        }

        List<HudElement> elements = new ArrayList<>();

        for (String part : parts) {
            HudElement element = parseElement(part, profile, debugLine, enabled);
            if (element != null)
                elements.add(element);
        }

        if (line)
            elements.add(new FunctionalElement.NewLine());
        return elements;
    }

    private static List<ConditionalElement.ConditionalPair> parseConditional(Matcher args, String original, int profile, int debugLine, ComplexData.Enabled enabled) {
        List<ConditionalElement.ConditionalPair> pairs = new ArrayList<>();
        while (args.find()) {
//            System.out.println("Cond: '" + args.group(1) + "', Value: '" + args.group(2) + "'");
            pairs.add(new ConditionalElement.ConditionalPair(ConditionalParser.parseConditional(args.group(1), original, profile, debugLine, enabled), addElements(args.group(2), profile, debugLine, enabled, false)));
        }
        return pairs;
    }

    public static HudElement parseElement(String part, int profile, int debugLine, ComplexData.Enabled enabled) {
        if (part == null || part.isEmpty())
            return null;

        if (part.equals("\n"))
            return new FunctionalElement.NewLine();

        if (part.startsWith("&{")) {
            Matcher m = HEX_COLOR_VARIABLE_PATTERN.matcher(part);
            if (m.matches())
                return new FunctionalElement.ChangeColor(HudTheme.parseHexNumber(m.group(1), false));
            else {
                String colorStr = part.substring(2, part.length()-1).trim().toLowerCase();
                Integer color = HudTheme.parseColorName(colorStr);
                if (color != null)
                    return new FunctionalElement.ChangeColor(color);
                Errors.addError(profile, debugLine, part, ErrorType.UNKNOWN_COLOR, colorStr);
                return null;
            }
        }

        if (!part.startsWith("{"))
            return new StringElement(part);

        String original = part;
        part = part.substring(1, part.length()-1);
        if (part.startsWith("{") && part.length() > 1) {
            part = part.substring(1, part.length() - 1);

            List<ConditionalElement.ConditionalPair> pairs = parseConditional(CONDITIONAL_PARSING_PATTERN.matcher(part), original, profile, debugLine, enabled);
            if (pairs.isEmpty())
                pairs = parseConditional(CONDITIONAL_PARSING_ALT_PATTERN.matcher(part), original, profile, debugLine, enabled);
            if (pairs.isEmpty()) {
                Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                return null;
            }
            return new ConditionalElement(pairs);
        }

        String[] flagParts = part.split(" ");
        Flags flags = Flags.parse(profile, debugLine, flagParts);
        part = flagParts[0];


        if (part.startsWith("real_time:")) {
            try {
                return new RealTimeElement(new SimpleDateFormat(part.substring(10)));
            }
            catch (IllegalArgumentException e) {
                Errors.addError(profile, debugLine, original, ErrorType.INVALID_TIME_FORMAT, e.getMessage());
            }
        }


        else if (part.startsWith("stat:")) {
            String stat = part.substring(5);

            HudElement element = stat("mined:",   Stats.MINED,   Registries.BLOCK, stat, flags, enabled);
            if (element == null) element = stat("crafted:", Stats.CRAFTED, Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("used:",    Stats.USED,    Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("broken:",  Stats.BROKEN,  Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("dropped:", Stats.DROPPED, Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("picked_up:", Stats.PICKED_UP, Registries.ITEM, stat, flags, enabled);
            if (element == null) element = stat("killed:",    Stats.KILLED,    Registries.ENTITY_TYPE, stat, flags, enabled);
            if (element == null) element = stat("killed_by:", Stats.KILLED_BY, Registries.ENTITY_TYPE, stat, flags, enabled);

            if (element != null)
                return element;

            Identifier statId = Registries.CUSTOM_STAT.get(new Identifier(stat));
            if (Stats.CUSTOM.hasStat(statId)) {
                enabled.updateStats = true;
                return new CustomStatElement(Stats.CUSTOM.getOrCreateStat(statId), flags);
            }
            else
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_STATISTIC, stat);
        }

        else if (part.startsWith("icon:")) {
            part = part.substring(part.indexOf(':')+1);

            Item item = Registries.ITEM.get(Identifier.tryParse(part));
            if (item != Items.AIR)
                return new ItemIconElement(new ItemStack(item), flags);

            Matcher matcher = TEXTURE_ICON_PATTERN.matcher(part);
            if (!matcher.matches()) {
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ICON, part);
                return null;
            }

//            for (int i = 0; i <= matcher.groupCount(); i++)
//                System.out.println(i + ": " + matcher.group(i));

            Identifier id = new Identifier(matcher.group(1) + ".png");
            int u = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            int v = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
            int w = matcher.group(4) == null ? -1 : Integer.parseInt(matcher.group(4));
            int h = matcher.group(5) == null ? -1 : Integer.parseInt(matcher.group(5));

            TextureIconElement element = new TextureIconElement(id, u, v, w, h, flags);
            if (!element.isIconAvailable())
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ICON, id.toString());
            return element;

        }

        else if (part.startsWith("itemcount:")) {
            part = part.substring(part.indexOf(':')+1);

            try {
                Item item = Registries.ITEM.get(new Identifier(part));
                if (item == Items.AIR)
                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ITEM_ID, part);
                else
                    return new ItemCountElement(item);
            }
            catch (InvalidIdentifierException e) {
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ITEM_ID, part);
            }
        }

        else if (part.startsWith("item:")) {
            int firstCollinIndex = part.indexOf(':', 6);

            String slot = firstCollinIndex == -1? part.substring(5) : part.substring(5, firstCollinIndex);
            String method = firstCollinIndex == -1? "" : part.substring(firstCollinIndex+1);
            Pair<HudElement,ErrorType> element = SlotItemElement.create(slot, method, flags);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ITEM_PROPERTY ? method : slot);
                return null;
            }
            return element.getLeft();
        }

        else if (part.startsWith("s:") || part.startsWith("setting:")) {
            String setting = part.substring(part.indexOf(':') + 1).toLowerCase();
            Pair<HudElement,Pair<ErrorType,String>> element = SettingsElement.create(setting, flags);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight().getLeft(), element.getRight().getRight());
                return null;
            }
            return flags.anyTextUsed() ? new FormattedElement(element.getLeft(), flags) : element.getLeft();
        }

        else if (part.equals("gizmo"))
            return new DebugGizmoElement(flags);

        else if (part.startsWith("space:")) {
            String widthStr = part.substring(6);
            try {
                return new SpaceElement( Integer.parseInt(widthStr) );
            }
            catch (NumberFormatException e) {
                Errors.addError(profile, debugLine, original, ErrorType.NOT_A_WHOLE_NUMBER, "\"" + widthStr + "\"");
                return null;
            }
        }

        else {
            HudElement element = getSupplierElement(part, enabled, flags);
            if (element != null) {
                return flags.anyTextUsed() ? new FormattedElement(element, flags) : element;
            }
            else {
                Matcher keyMatcher = registryKey.matcher(part);
                if (keyMatcher.matches()) {
                    element = CustomHudRegistry.get(keyMatcher.group(1), part);
                    if (element != null)
                        return element;

                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_VARIABLE, part);
                }
                else
                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_VARIABLE, part);
            }
        }
        return null;
    }

    private static final Pattern registryKey = Pattern.compile("(\\w+).*");

    private static HudElement stat(String prefix, StatType<?> type, Registry<?> registry, String stat, Flags flags, ComplexData.Enabled enabled) {
        if (!stat.startsWith(prefix))
            return null;

        Optional<?> entry = registry.getOrEmpty( new Identifier(stat.substring(prefix.length())) );
        if (entry.isPresent()) {
            enabled.updateStats = true;
            return new TypedStatElement(type, entry.get(), flags);
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static HudElement getSupplierElement(String name, ComplexData.Enabled enabled, Flags flags) {

        Supplier supplier = getStringSupplier(name, enabled);
        if (supplier != null)
            return new StringSupplierElement(supplier);

        supplier = getBooleanSupplier(name, enabled);
        if (supplier != null)
            return new BooleanSupplierElement(supplier);

        supplier = getIntegerSupplier(name, enabled);
        if (supplier != null)
            return flags.precision == -1 ? new NumberSupplierElement(supplier, flags.scale) : new NumberSupplierElement(supplier, flags.scale, flags.precision);

        NumberSupplierElement.Entry entry = getDecimalSupplier(name, enabled);
        if (entry != null)
            return flags.precision == -1 ? new NumberSupplierElement(entry, flags.scale) : new NumberSupplierElement(entry, flags.scale, flags.precision);

        supplier = getStringIntSupplier(name, enabled);
        if (supplier != null)
            return new StringIntSupplierElement(supplier);

        SpecialSupplierElement.Entry entry2 = getSpecialSupplierElements(name, enabled);
        if (entry2 != null)
            return new SpecialSupplierElement(entry2);

        return null;
    }

    private static Supplier<String> getStringSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "version" -> VERSION;
            case "client_version" -> CLIENT_VERSION;
            case "modded_name" -> MODDED_NAME;
            case "display_name", "name" -> DISPLAY_NAME;
            case "username" -> USERNAME;
            case "uuid" -> UUID;
            case "dimension" -> DIMENSION;
            case "dimension_id" -> DIMENSION_ID;
            case "facing" -> FACING;
            case "facing_short" -> FACING_SHORT;
            case "facing_towards_xz" -> FACING_TOWARDS_XZ;
            case "biome" -> BIOME;
            case "biome_id" -> BIOME_ID;
            case "moon_phase_word" -> { enabled.clientChunk = true; yield MOON_PHASE_WORD; }
            case "target_entity", "te" -> TARGET_ENTITY;
            case "target_entity_id", "tei" -> TARGET_ENTITY_ID;
            case "target_entity_name", "ten" -> TARGET_ENTITY_NAME;
            case "target_entity_uuid", "teu" -> TARGET_ENTITY_UUID;
            case "world_name", "world" -> WORLD_NAME;
            case "server_name" -> SERVER_NAME;
            case "server_address", "address", "ip" -> SERVER_ADDRESS;
            case "java_version" -> JAVA_VERSION;
            case "cpu_name" -> CPU_NAME;
            case "gpu_name" -> GPU_NAME;
            case "server_brand" -> SERVER_BRAND;
            case "am_pm" -> { enabled.time = true; yield TIME_AM_PM; }
            default -> null;
        };
    }

    private static Supplier<Boolean> getBooleanSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "vsync" -> VSYNC;
            case "sp", "singleplayer" -> SINGLEPLAYER;
            case "mp", "multiplayer" -> MULTIPLAYER;
            case "chunks_culling" -> CHUNK_CULLING;
            case "overworld" -> IN_OVERWORLD;
            case "nether" -> IN_NETHER;
            case "end" -> IN_END;
            case "raining" -> {enabled.world = true; yield IS_RAINING;}
            case "thundering" -> {enabled.world = true; yield IS_THUNDERING;}
            case "snowing" -> {enabled.world = true; yield IS_SNOWING;}
            case "slime_chunk" -> {enabled.world = true; yield IS_SLIME_CHUNK;}
            case "sprinting" -> SPRINTING;
            case "sneaking" -> SNEAKING;
            case "swimming" -> SWIMMING;
            case "on_ground" -> ON_GROUND;
            case "item_has_durability", "item_has_dur" -> ITEM_HAS_DURABILITY;
            case "offhand_item_has_durability", "oitem_has_dur" -> OFFHAND_ITEM_HAS_DURABILITY;
            case "fishing_is_cast" -> FISHING_IS_CAST;
            case "fishing_is_hooked" -> FISHING_IS_HOOKED;
            case "fishing_has_caught" -> FISHING_HAS_CAUGHT;
            case "fishing_in_open_water" -> FISHING_IN_OPEN_WATER;
            default -> null;
        };
    }

    private static Supplier<Number> getIntegerSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "fps" -> FPS;
            case "max_fps" -> MAX_FPS;
            case "biome_blend" -> BIOME_BLEND;
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
            case "target_block_x", "target_x", "tbx" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_X; }
            case "target_block_y", "target_y", "tby" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_Y; }
            case "target_block_z", "target_z", "tbz" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_Z; }
            case "target_block_distance", "target_distance", "tbd" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_DISTANCE; }
            case "target_fluid_x", "tfx" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_X; }
            case "target_fluid_y", "tfy" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_Y; }
            case "target_fluid_z", "tfz" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_Z; }
            case "target_fluid_distance", "tfd" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_DISTANCE; }
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
            case "client_light_sun", "light_sun" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_SUN; }
            case "client_light_block", "light_block" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_BLOCK; }
            case "server_light_sky" -> { enabled.world = enabled.serverChunk = true; yield SERVER_LIGHT_SKY; }
            case "server_light_block" -> { enabled.world = enabled.serverChunk = true; yield SERVER_LIGHT_BLOCK; }
            case "client_height_map_surface", "chs" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_SURFACE; }
            case "client_height_map_motion_blocking", "chm" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_surface", "shs" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_SURFACE; }
            case "server_height_map_ocean_floor", "sho" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_OCEAN_FLOOR; }
            case "server_height_map_motion_blocking", "shm" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_motion_blocking_no_leaves", "shml" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES; }
            case "moon_phase" -> { enabled.clientChunk = true; yield MOON_PHASE; }
            case "spawn_chunks", "sc" -> { enabled.serverWorld = true; yield SPAWN_CHUNKS; }
            case "monsters" -> { enabled.serverWorld = true; yield MONSTERS; }
            case "creatures" -> { enabled.serverWorld = true; yield CREATURES; }
            case "ambient_mobs" -> { enabled.serverWorld = true; yield AMBIENT_MOBS; }
            case "water_creatures" -> { enabled.serverWorld = true; yield WATER_CREATURES; }
            case "water_ambient_mobs" -> { enabled.serverWorld = true; yield WATER_AMBIENT_MOBS; }
            case "underground_water_creatures" -> { enabled.serverWorld = true; yield UNDERGROUND_WATER_CREATURE; }
            case "axolotls" -> { enabled.serverWorld = true; yield AXOLOTLS; }
            case "misc_mobs" -> { enabled.serverWorld = true; yield MISC_MOBS; }
            case "java_bit" -> JAVA_BIT;
            case "cpu_cores" -> CPU_CORES;
            case "cpu_threads" -> CPU_THREADS;
            case "display_width" -> DISPLAY_WIDTH;
            case "display_height" -> DISPLAY_HEIGHT;
            case "display_refresh_rate" -> DISPLAY_REFRESH_RATE;
            case "mods" -> MODS;
            case "ping" -> PING;
            case "item_durability", "item_dur" -> ITEM_DURABILITY;
            case "item_max_durability", "item_max_dur" -> ITEM_MAX_DURABILITY;
            case "offhand_item_durability", "oitem_dur" -> OFFHAND_ITEM_DURABILITY;
            case "offhand_item_max_durability", "oitem_max_dur" -> OFFHAND_ITEM_MAX_DURABILITY;
            case "hour12", "hour" -> { enabled.time = true; yield TIME_HOUR_12; }
            case "lcps" -> { enabled.clicksPerSeconds = true; yield LCPS; }
            case "rcps" -> { enabled.clicksPerSeconds = true; yield RCPS; }
            default -> null;
        };
    }

    private static Supplier<String> getStringIntSupplier(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "particles", "p": return PARTICLES;
            case "streaming_sounds", "sounds": enabled.sound = true; return STREAMING_SOUNDS;
            case "max_streaming_sounds", "max_sounds": enabled.sound = true; return MAX_STREAMING_SOUNDS;
            case "static_sounds": enabled.sound = true; return STATIC_SOUNDS;
            case "max_static_sounds": enabled.sound = true; return MAX_STATIC_SOUNDS;
            default: return null;
        }
    }

    private static NumberSupplierElement.Entry getDecimalSupplier(String element, ComplexData.Enabled enabled) {
        if (element.startsWith("velocity_"))
            enabled.velocity = true;
        return switch (element) {
            case "x" -> X;
            case "y" -> Y;
            case "z" -> Z;
            case "nether_x", "nx" -> NETHER_X;
            case "nether_z", "nz" -> NETHER_Z;
            case "target_entity_x", "tex" -> TARGET_ENTITY_X;
            case "target_entity_y", "tey" -> TARGET_ENTITY_Y;
            case "target_entity_z", "tez" -> TARGET_ENTITY_Z;
            case "target_entity_distance", "ted" -> TARGET_ENTITY_DISTANCE;
            case "reach_distance", "reach" -> REACH_DISTANCE;
            case "fishing_hook_distance" -> FISHING_HOOK_DISTANCE;
            case "velocity_xz" -> VELOCITY_XZ;
            case "velocity_y" -> VELOCITY_Y;
            case "velocity_xyz" -> VELOCITY_XYZ;
            case "velocity_xz_kmh" -> VELOCITY_XZ_KMH;
            case "velocity_y_kmh" -> VELOCITY_Y_KMH;
            case "velocity_xyz_kmh" -> VELOCITY_XYZ_KMH;
            case "yaw" -> YAW;
            case "pitch" -> PITCH;
            case "day" -> DAY;
            case "mood" -> MOOD;
            case "tps" -> TPS;
            case "memory_used_percentage" -> MEMORY_USED_PERCENTAGE;
            case "memory_used" -> MEMORY_USED;
            case "memory_total" -> TOTAL_MEMORY;
            case "memory_allocated_percentage" -> ALLOCATED_PERCENTAGE;
            case "memory_allocated" -> ALLOCATED;
//            case "memory_off_heap" -> OFF_HEAP; TODO
            case "cpu_usage", "cpu" -> {enabled.cpu = true; yield CPU_USAGE;}
            case "gpu_usage", "gpu" -> {enabled.performanceMetrics = true; yield GPU_USAGE;}
            case "ms_ticks", "tick_ms" -> TICK_MS;
            case "frame_ms_min" -> { enabled.performanceMetrics = true; yield FRAME_MS_MIN;}
            case "frame_ms_max" -> { enabled.performanceMetrics = true; yield FRAME_MS_MAX;}
            case "frame_ms_avg" -> { enabled.performanceMetrics = true; yield FRAME_MS_AVG;}
            case "frame_ms_samples" -> { enabled.performanceMetrics = true; yield FRAME_MS_SAMPLES;}
            case "item_durability_percent", "item_dur_per" -> ITEM_DURABILITY_PERCENT;
            case "offhand_item_durability_percent", "oitem_dur_per" -> OFFHAND_ITEM_DURABILITY_PERCENT;
            case "local_difficulty" -> { enabled.localDifficulty = enabled.world = true; yield LOCAL_DIFFICULTY; }
            case "clamped_local_difficulty" -> { enabled.localDifficulty = enabled.world = true; yield CLAMPED_LOCAL_DIFFICULTY; }
            default -> null;
        };
    }

    private static SpecialSupplierElement.Entry getSpecialSupplierElements(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "hour24": { enabled.time = true; return TIME_HOUR_24; }
            case "minute": { enabled.time = true; return TIME_MINUTES; }
            case "second": { enabled.time = true; return TIME_SECONDS; }
            case "target_block", "tb": {enabled.world = enabled.targetBlock = true; return TARGET_BLOCK;}
            case "target_block_id", "tbi": {enabled.world = enabled.targetBlock = true; return TARGET_BLOCK_ID;}
            case "target_fluid", "tf": {enabled.world = enabled.targetFluid = true; return TARGET_FLUID;}
            case "target_fluid_id", "tfi": {enabled.world = enabled.targetFluid = true; return TARGET_FLUID_ID;}
            case "item": return ITEM;
            case "item_name": return ITEM_NAME;
            case "item_id": return ITEM_ID;
            case "offhand_item", "oitem": return OFFHAND_ITEM;
            case "offhand_item_name": return OFFHAND_ITEM_NAME;
            case "offhand_item_id", "oitem_id": return OFFHAND_ITEM_ID;
            case "clouds": return CLOUDS;
            case "graphics_mode": return GRAPHICS_MODE;
            case "facing_towards_pn_word": return FACING_TOWARDS_PN_WORD;
            case "facing_towards_pn_sign": return FACING_TOWARDS_PN_SIGN;
            default: return null;
        }
    }
    
}
