package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.NewHudElements.HudElement2;
import com.minenash.customhud.NewHudElements.supplier.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.minenash.customhud.HudElements.SupplierElement.*;
import static com.minenash.customhud.HudElements.SupplierElement.DISPLAY_HEIGHT;

public class Profile {

    private static final Pattern LINE_PARING_PATTERN = Pattern.compile("([^{}&]*)(\\{\\{.*?}}|&?\\{.*?})?");
    private static final Pattern CONDITIONAL_PARSING_PATTERN = Pattern.compile("(.*?), ?\"(.*?)\"(, ?\"(.*?)\")?");
    private static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?Section: ?(TopLeft|TopRight|BottomLeft|BottomRight) ?(, ?([-+]?\\d+))? ?(, ?([-+]?\\d+))?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?TargetRange: ?(\\d+|max) ?==");
    private static final Pattern SPACING_FLAG_PATTERN = Pattern.compile("== ?LineSpacing: ?([-+]?\\d+) ?==");
    private static final Pattern SCALE_FLAG_PATTERN = Pattern.compile("== ?Scale: ?(\\d+.?\\d*|.?\\d+) ?==");
    private static final Pattern COLOR_FLAG_PATTERN = Pattern.compile("== ?(Back|Fore)groundColou?r: ?(0x|#)?([0-9a-fA-F]+) ?==");

    public List<List<HudElement>>[] sections = new List[4];
    public ComplexData.Enabled enabled = new ComplexData.Enabled();
    public int[][] offsets = new int[4][2];

    public int bgColor;
    public int fgColor;
    public int lineSpacing;
    public float targetDistance;
    public float scale;

    public static Profile parseProfile(Path path) {
        List<String> lines;

        try {
            if(!Files.exists(path.getParent()))
                Files.createDirectory(path.getParent());
            if (!Files.exists(path))
                Files.createFile(path);
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Profile profile = new Profile();

        for (int i = 0; i < 4; i++) {
            profile.sections[i] = new ArrayList<>();
            profile.offsets[i] = new int[2];
        }
        profile.bgColor = 0x44000000;
        profile.fgColor = 0xffffffff;
        profile.targetDistance = 20;
        profile.lineSpacing = 2;
        profile.scale = 1;

        int sectionId = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("&([0-9a-fk-or])", "§$1");
            if (line.startsWith("//") || (sectionId == -1 && line.trim().isEmpty()))
                continue;
            if (sectionId == -1) {
                Matcher matcher = TARGET_RANGE_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.targetDistance = matcher.group(1).equals("max") ? 725 : Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = COLOR_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    if (matcher.group(1).equals("Fore"))
                        profile.fgColor = parseHexNumber(matcher.group(3));
                    else
                        profile.bgColor = parseHexNumber(matcher.group(3));
                    continue;
                }
                matcher = SPACING_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.lineSpacing = Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = SCALE_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.scale = Float.parseFloat(matcher.group(1));
                    continue;
                }
            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(line);
            if (matcher.matches()) {

                switch (matcher.group(1).toLowerCase()) {
                    case "topleft": sectionId = 0; break;
                    case "topright": sectionId = 1; break;
                    case "bottomleft": sectionId = 2; break;
                    case "bottomright": sectionId = 3; break;
                }
                profile.offsets[sectionId][0] = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                profile.offsets[sectionId][1] = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;
                continue;
            }
            if (sectionId == -1) {
                sectionId = 0;
            }

            profile.sections[sectionId].add(parseElements(line, i + 1,profile.enabled));
        }

        for (int i = 0; i < 4; i++) {
            if (profile.sections[i].isEmpty())
                profile.sections[i] = null;
        }
        return profile;
    }

    public static int parseHexNumber(String str) {
        long color = Long.parseLong(str,16);
        return (int) (color >= 0x100000000L ? color - 0x100000000L : color);
    }

    public static List<HudElement> parseElements(String str, int debugLine, ComplexData.Enabled enabled) {
        List<String> parts = new ArrayList<>();

        Matcher matcher = LINE_PARING_PATTERN.matcher(str == null ? "" : str);
        while (matcher.find()) {
            parts.add(matcher.group(1));
            parts.add(matcher.group(2));
        }

        List<HudElement> elements = new ArrayList<>();

        for (String part : parts) {
            if (part == null || part.isEmpty())
                continue;

            if (!part.startsWith("{"))
                elements.add(new StringElement(part));

            else if (part.startsWith("{real_time:"))
                elements.add(new RealTimeElement(new SimpleDateFormat(part.substring(11,part.length()-1))));

            else if (part.startsWith("{{")) {
                Matcher args = CONDITIONAL_PARSING_PATTERN.matcher(part.substring(2,part.length()-2));
                if (!args.matches()) {
                    CustomHud.LOGGER.warn("Malformed conditional " + part + " on line " + debugLine);
                    continue;
                }
                Supplier<String> conditional = getSupplier(args.group(1),enabled);
                if (conditional == null) {
                    CustomHud.LOGGER.warn("[Cond] Unknown Variable " + args.group(1) + " on line " + debugLine);
                    continue;
                }
                List<HudElement> positive = parseElements(args.group(2), debugLine,enabled);
                List<HudElement> negative = args.groupCount() > 2 ? parseElements(args.group(4), debugLine,enabled) : new ArrayList<>();
                elements.add(new ConditionalElement(conditional, positive, negative));
            }
            else {
//                HudElement2 element = getSupplierElement(part.substring(1, part.length() - 1), enabled);
//                if (element != null)
//                    elements.add(element);
//                else
//                    CustomHud.LOGGER.warn("Unknown Variable " + part + " on line " + debugLine);
//                
//                Supplier<String> supplier = getSupplier(part.substring(1, part.length() - 1), enabled);
//                if (supplier != null)
//                    elements.add(new SupplierElement(supplier));
//                else
//                    CustomHud.LOGGER.warn("Unknown Variable " + part + " on line " + debugLine);
            }
        }

        return elements;
    }
    
    private static Supplier<String> getSupplier(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "fps": return FPS;
            case "max_fps": return MAX_FPS;
            case "vsync": return VSYNC;
            case "version": return VERSION;
            case "client_version": return CLIENT_VERSION;
            case "modded_name": return MODDED_NAME;
            case "graphics_mode": return GRAPHICS_MODE;
            case "clouds": return CLOUDS;
            case "biome_blend": return BIOME_BLEND;
            case "ms_ticks": return MS_TICKS;
            case "tps": return TPS;
            case "server_brand": return SERVER_BRAND;
            case "packets_sent":
            case "tx":	return PACKETS_SENT;
            case "packets_received":
            case "rx": return PACKETS_RECEIVED;
            case "chunks_rendered": return CHUNKS_RENDERED;
            case "chunks_loaded": return CHUNKS_LOADED;
            case "chunks_culling": return CHUNK_CULLING;
            case "render_distance": return RENDER_DISTANCE;
            case "queued_tasks": return QUEUED_TASKS;
            case "upload_queue": return UPLOAD_QUEUE;
            case "buffer_count": return BUFFER_COUNT;
            case "entities_rendered": return ENTITIES_RENDERED;
            case "entities_loaded": return ENTITIES_LOADED;
            case "particles":
            case "p": return PARTICLES;
            case "dimension": return DIMENSION;
            case "dimension_id": return DIMENSION_ID;
            case "overworld": return IN_OVERWORLD;
            case "nether": return IN_NETHER;
            case "end": return IN_END;
            case "force_loaded_chunks":
            case "fc": enabled.world = true; return FORCED_LOADED_CHUNKS;
            case "x": return X;
            case "y": return Y;
            case "z": return Z;
            case "block_x":
            case "bx": return BLOCK_X;
            case "block_y":
            case "by": return BLOCK_Y;
            case "block_z":
            case "bz": return BLOCK_Z;
            case "nether_x":
            case "nx": return NETHER_X;
            case "nether_z":
            case "nz": return NETHER_Z;
            case "target_x":
            case "tbx": enabled.targetBlock = true; return TARGET_X;
            case "target_y":
            case "tby": enabled.targetBlock = true; return TARGET_Y;
            case "target_z":
            case "tbz": enabled.targetBlock = true; return TARGET_Z;
            case "in_chunk_x":
            case "icx": return IN_CHUNK_X;
            case "in_chunk_y":
            case "icy": return IN_CHUNK_Y;
            case "in_chunk_z":
            case "icz": return IN_CHUNK_Z;
            case "chunk_x":
            case "cx": return CHUNK_X;
            case "chunk_y":
            case "cy": return CHUNK_Y;
            case "chunk_z":
            case "cz": return CHUNK_Z;
            case "velocity_xz": enabled.velocity = true; return VELOCITY_XZ;
            case "velocity_y": enabled.velocity = true; return VELOCITY_Y;
            case "velocity_xyz": enabled.velocity = true; return VELOCITY_XYZ;
            case "velocity_xz_kmh": enabled.velocity = true; return VELOCITY_XZ_KMH;
            case "velocity_y_kmh": enabled.velocity = true; return VELOCITY_Y_KMH;
            case "velocity_xyz_kmh": enabled.velocity = true; return VELOCITY_XYZ_KMH;
            case "facing": return FACING;
            case "facing_towards_xz": return FACING_TOWARDS_XZ;
            case "facing_towards_pn_word": return FACING_TOWARDS_PN_WORD;
            case "facing_towards_pn_sign": return FACING_TOWARDS_PN_SIGN;
            case "yaw": return YAW;
            case "pitch": return PITCH;
            case "client_light":
            case "light": enabled.clientChunk = true; return CLIENT_LIGHT;
            case "client_light_sky":
            case "light_sky": enabled.clientChunk = true; return CLIENT_LIGHT_SKY;
            case "client_light_block":
            case "light_block": enabled.clientChunk = true; return CLIENT_LIGHT_BLOCK;
            case "server_light_sky": enabled.world = true; enabled.serverChunk = true; return SERVER_LIGHT_SKY;
            case "server_light_block": enabled.world = true; enabled.serverChunk = true; return SERVER_LIGHT_BLOCK;
            case "client_height_map_surface":
            case "chs": enabled.clientChunk = true; return CLIENT_HEIGHT_MAP_SURFACE;
            case "client_height_map_motion_blocking":
            case "chm": enabled.clientChunk = true; return CLIENT_HEIGHT_MAP_MOTION_BLOCKING;
            case "server_height_map_surface":
            case "shs": enabled.serverChunk = true; return SERVER_HEIGHT_MAP_SURFACE;
            case "server_height_map_ocean_floor":
            case "sho": enabled.serverChunk = true; return SERVER_HEIGHT_MAP_OCEAN_FLOOR;
            case "server_height_map_motion_blocking":
            case "shm": enabled.serverChunk = true; return SERVER_HEIGHT_MAP_MOTION_BLOCKING;
            case "server_height_map_motion_blocking_no_leaves":
            case "shml": enabled.serverChunk = true; return SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES;
            case "biome": return BIOME;
            case "biome_id": return BIOME_ID;
            case "local_difficulty": enabled.localDifficulty = true; return LOCAL_DIFFICULTY;
            case "clamped_local_difficulty": enabled.localDifficulty = true; return CLAMPED_LOCAL_DIFFICULTY;
            case "day": return DAY;
            case "spawn_chunks":
            case "sc": enabled.serverWorld = true; return SPAWN_CHUNKS;
            case "monsters": enabled.serverWorld = true; return MONSTERS;
            case "creatures": enabled.serverWorld = true; return CREATURES;
            case "ambient_mobs": enabled.serverWorld = true; return AMBIENT_MOBS;
            case "water_creatures": enabled.serverWorld = true; return WATER_CREATURES;
            case "water_ambient_mobs": enabled.serverWorld = true; return WATER_AMBIENT_MOBS;
            case "misc_mobs": enabled.serverWorld = true; return MISC_MOBS;
            case "streaming_sounds":
            case "sounds": enabled.sound = true; return STREAMING_SOUNDS;
            case "max_streaming_sonds":
            case "max_sounds": enabled.sound = true; return MAX_STREAMING_SOUNDS;
            case "static_sounds": enabled.sound = true; return STATIC_SOUNDS;
            case "max_static_sounds": enabled.sound = true; return MAX_STATIC_SOUNDS;
            case "mood": return MOOD;
            case "java_version": return JAVA_VERSION;
            case "java_bit": return JAVA_BIT;
            case "memory_used_percentage": return MEMORY_USED_PERCENTAGE;
            case "memory_used": return MEMORY_USED;
            case "total_memory": return TOTAL_MEMORY;
            case "allocated_percentage": return ALLOCATED_PERCENTAGE;
            case "allocated": return ALLOCATED;
            case "client_chunk_cache_capacity": enabled.clientChunkCache = true; return CLIENT_CHUNK_CACHE_CAPACITY;
            case "client_chunk_cache": enabled.clientChunkCache = true; return CLIENT_CHUNK_CACHE;
            case "server_chunk_cache": enabled.serverWorld = true; return SERVER_CHUNK_CACHE;
            case "display_width": return DISPLAY_WIDTH;
            case "display_height": return DISPLAY_HEIGHT;
            case "mods": return MODS;
            case "ping": return PING;
            case "address": return ADDRESS;
            case "hour":
            case "hour12": enabled.time = true; return TIME_HOUR_12;
            case "hour24": enabled.time = true; return TIME_HOUR_24;
            case "minute": enabled.time = true; return TIME_MINUTE;
            case "am_pm": enabled.time = true; return TIME_AM_PM;
            default: return null;
        }
    }

}
