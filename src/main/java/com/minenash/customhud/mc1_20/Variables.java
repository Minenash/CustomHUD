package com.minenash.customhud.mc1_20;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.elements.FormattedElement;
import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.HudElement;
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
import static com.minenash.customhud.core.registry.MetaData.DefaultCategories.*;
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


    @SuppressWarnings("deprecation")
    public static void registerVars() {

        register(NONE, INT, FPS, "fps")                                 .meta(PERFORMANCE, "v1.0", "Frames per Second (FPS)", "Frames per Second");
        register(NONE, DEC, TPS, "tps")                                 .meta(PERFORMANCE, "v1.0", "Ticks per Second (TPS)", "How many ticks happen every second. Maxes out at 20");
        register(NONE, DEC, TICK_MS, "tick_ms", "ms_ticks")             .meta(PERFORMANCE, "v1.0", "Milliseconds per Tick", "The time it takes for a tick in milliseconds");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_MIN, "frame_ms_min").meta(PERFORMANCE, "v3.0", "Milliseconds per Frame - Min", "Average frame time in milliseconds");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_MAX, "frame_ms_max").meta(PERFORMANCE, "v3.0", "Milliseconds per Frame - Max", "Minimum frame time in milliseconds");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_AVG, "frame_ms_avg").meta(PERFORMANCE, "v3.0", "Milliseconds per Frame - Avg", "Maximum frame time in milliseconds");
        register(PERFORMANCE_METRICS, DEC, FRAME_MS_SAMPLES, "frame_ms_samples").meta(PERFORMANCE, "v3.0", "Milliseconds per Frame - Samples", "Number of samples used for Milliseconds per Frame Min/Max/Avg");
        
        register(NONE, STRING, WORLD_NAME, "world_name", "world")                .meta(WORLD_SERVER, "v3.1", "World Name", "The name of the singleplayer world the player is in");
        register(NONE, STRING, SERVER_NAME, "server_name")                       .meta(WORLD_SERVER, "v3.1", "Server Name", "The name of the server (as written in the server entry) the player is in");
        register(NONE, STRING, SERVER_ADDRESS, "server_address", "address", "ip").meta(WORLD_SERVER, "v1.1", "Server IP Address", "The address (as written in the address field) of the server<br>The aliases {server_address} and {ip} added in v3.1");
        register(NONE, STRING, SERVER_BRAND, "server_brand")                     .meta(WORLD_SERVER, "v1.0", "Server Brand", "The server type. Ex: Fabric, Spigot, etc"); //TODO: Add to Docs
        register(NONE, INT, PING, "ping")                                        .meta(WORLD_SERVER, "v1.1", "Ping", "The ping to the server");
        register(NONE, INT, PACKETS_SENT, "packets_sent", "tx")                  .meta(WORLD_SERVER, "v1.0", "Packets Sent", "Number of packets sent by the client to the server");
        register(NONE, INT, PACKETS_RECEIVED, "packets_received", "rx")          .meta(WORLD_SERVER, "v1.0", "Packets Received", "Number of packets received by the client from the server");

        register(CLICKS_PER_SECONDS, INT, LCPS, "lcps", "left_clicks_per_second") .meta(MISC, "v3.0", "Left Clicks per Second", "How many times you left‑clicked in the previous second");
        register(CLICKS_PER_SECONDS, INT, RCPS, "rcps", "right_clicks_per_second").meta(MISC, "v3.0", "Right Clicks per Second", "How many times you right‑clicked in the previous second");
        register(NONE, DEC, REACH_DISTANCE, "reach", "reach_distance")    .meta(MISC, "v3.1", "Player Reach", "How far the player can reach");
        register(NONE, STRING, DISPLAY_NAME, "name", "display_name")      .meta(MISC, "v3.1", "Display Name", "The name of the player as seen in chat and tab");
        register(NONE, STRING, USERNAME, "username")                      .meta(MISC, "v3.1", "Username", "The username of the player");
        register(NONE, STRING, UUID, "uuid")                              .meta(MISC, "v3.1", "Player UUID", "The uuid of the player");
        register(NONE, STRING, VERSION, "version")                        .meta(MISC, "v1.0", "MC Version (1.20)", "The minecraft version. (Ex: 1.16.2, 20w34a)");
        register(NONE, STRING, CLIENT_VERSION, "client_version")          .meta(MISC, "v1.0", "Game Version (fabric)", "Game Version: (probably fabric)");
        register(NONE, STRING, MODDED_NAME, "modded_name")                .meta(MISC, "v1.0", "Client Brand (Fabric)", "Client Brand Name: (probably Fabric)");
        register(NONE, STR_INT, PARTICLES, "particles", "p")              .meta(MISC, "v1.0", "Particles", "Number of particles visible or loaded (not sure)");
        register(CLIENT_CHUNK, STRING, MOON_PHASE_WORD, "moon_phase_word").meta(MISC, "v2.1", "Moon Phase (full moon)", "The phase of the moon in words");
        register(CLIENT_CHUNK, INT, MOON_PHASE, "moon_phase")             .meta(MISC, "v2.1", "Moon Phase (1-8)", "The phase of the moon (1-8)");
        register(NONE, INT, MODS, "mods")                                 .meta(MISC, "v1.0", "Number of Mods", "The total amount of mods installed (according to fabric)");

        register(NONE, DEC, X, "x")                         .meta(POSITION, "v1.0", "X", "The player’s x coordinate. Ex: 320.153");
        register(NONE, DEC, Y, "y")                         .meta(POSITION, "v1.0", "Y", "The player’s y coordinate. Ex: 80.459");
        register(NONE, DEC, Z, "z")                         .meta(POSITION, "v1.0", "Z", "The player’s z coordinate. Ex: 1200.963");
        register(NONE, INT, BLOCK_X, "bx", "block_x")       .meta(POSITION, "v1.0", "X (Whole Number)", "The x coordinate of the block the player’s feet is at. Ex: 320");
        register(NONE, INT, BLOCK_Y, "by", "block_y")       .meta(POSITION, "v1.0", "Y (Whole Number)", "The y coordinate of the block the player’s feet is at. Ex: 80");
        register(NONE, INT, BLOCK_Z, "bz", "block_z")       .meta(POSITION, "v1.0", "Z (Whole Number)", "The z coordinate of the block the player’s feet is at. Ex: 1200");
        register(NONE, DEC, NETHER_X, "nx", "nether_x")     .meta(POSITION, "v1.0", "Nether X", "The nether equivalent of the player’s x coordinate. (If already in the nether, then the overworld equivalent)"); //TODO: Add to Docs
        register(NONE, DEC, NETHER_Z, "nz", "nether_z")     .meta(POSITION, "v1.0", "Nether Z", "The nether equivalent of the player’s z coordinate. (If already in the nether, then the overworld equivalent)"); //TODO: Add to Docs
        register(NONE, INT, IN_CHUNK_X, "icx", "in_chunk_x").meta(POSITION, "v1.0", "In-Chunk X", "The player’s x coordinate inside the chunk (between 0-15)");
        register(NONE, INT, IN_CHUNK_Y, "icy", "in_chunk_y").meta(POSITION, "v1.0", "In-Chunk Y", "The player’s y coordinate inside the chunk (between 0-15)");
        register(NONE, INT, IN_CHUNK_Z, "icz", "in_chunk_z").meta(POSITION, "v1.0", "In-Chunk Z", "The player’s z coordinate inside the chunk (between 0-15)");
        register(NONE, INT, CHUNK_X, "cx", "chunk_x")       .meta(POSITION, "v1.0", "Chunk X", "The chunk’s x coordinate (goes up by 1 every 16 blocks in the x direction)");
        register(NONE, INT, CHUNK_Y, "cy", "chunk_y")       .meta(POSITION, "v1.0", "Chunk Y", "The chunk’s y coordinate (goes up by 1 every 16 blocks in the y direction)");
        register(NONE, INT, CHUNK_Z, "cz", "chunk_z")       .meta(POSITION, "v1.0", "Chunk Z", "The chunk’s z coordinate (goes up by 1 every 16 blocks in the z direction)");
        register(NONE, INT, REGION_X, "rex", "region_x")    .meta(POSITION, "v2.0", "Region X", "<i>No Description Provided</i>");
        register(NONE, INT, REGION_Z, "rez", "region_z")    .meta(POSITION, "v2.0", "Region Z", "<i>No Description Provided</i>");
        register(NONE, INT, REGION_RELATIVE_X, "rrx", "region_relative_x").meta(POSITION, "v2.0", "Region Relative X", "<i>No Description Provided</i>");
        register(NONE, INT, REGION_RELATIVE_Z, "rrz", "region_relative_z").meta(POSITION, "v2.0", "Region Relative Z", "<i>No Description Provided</i>");

        register(NONE, STRING, FACING, "facing")                         .meta(DIRECTION, "v1.0", "Facing (north)", "The cardinal direction the player is facing (north, south, east, west)");
        register(NONE, STRING, FACING_SHORT, "facing_short")             .meta(DIRECTION, "v3.0", "Facing (N)", "The cardinal direction the player is facing, but shorter (N, S, E, W)");
        register(NONE, STRING, FACING_XZ, "facing_towards_xz")           .meta(DIRECTION, "v1.0", "Facing Towards X/Z Axis", "Which horizontal axis the player is facing (x or z)");
        register(NONE, SPECIAL, FACING_PN_WORD, "facing_towards_pn_word").meta(DIRECTION, "v1.0", "Facing Towards +/- Axis", "If the player is looking at the positive or negative direction of the axis (+/-)");
        register(NONE, SPECIAL, FACING_PN_SIGN, "facing_towards_pn_sign").meta(DIRECTION, "v1.0", "Facing Towards pos/neg Axis", "If the player is looking at the positive or negative direction of the axis (positive/negative)");
        register(NONE, DEC, YAW, "yaw")                                  .meta(DIRECTION, "v1.0", "Yaw", "The yaw rotation of the player");
        register(NONE, DEC, PITCH, "pitch")                              .meta(DIRECTION, "v1.0", "Pitch", "The pitch rotation of the player");

        register(VELOCITY, DEC, VELOCITY_XZ, "velocity_xz")          .meta(MOVEMENT, "v1.1", "Velocity (XZ)", "Horizontal (XZ) velocity in blocks per second");
        register(VELOCITY, DEC, VELOCITY_Y, "velocity_y")            .meta(MOVEMENT, "v1.1", "Velocity (Y)", "Vertical (Y) velocity in blocks per second");
        register(VELOCITY, DEC, VELOCITY_XYZ, "velocity_xyz")        .meta(MOVEMENT, "v1.1", "Velocity (XYZ)", "Total (XYZ) velocity in blocks per second");
        register(VELOCITY, DEC, VELOCITY_XZ_KMH, "velocity_xz_kmh")  .meta(MOVEMENT, "v1.2.1", "Velocity (XZ, Kmh)", "Horizontal (XZ) velocity in kilometers per hour (km/h)");
        register(VELOCITY, DEC, VELOCITY_Y_KMH, "velocity_y_kmh")    .meta(MOVEMENT, "v1.2.1", "Velocity (Y, Kmh)", "Vertical (Y) velocity in kilometers per hour (km/h)");
        register(VELOCITY, DEC, VELOCITY_XYZ_KMH, "velocity_xyz_kmh").meta(MOVEMENT, "v1.2.1", "Velocity (XYZ, Kmh)", "Total (XYZ) velocity in kilometers per hour (km/h)");
        register(NONE, BOOLEAN, SPRINTING, "sprinting")              .meta(MOVEMENT, "v3.0", "Is Sprinting", "Is the player sprinting?");
        register(NONE, BOOLEAN, SNEAKING, "sneaking")                .meta(MOVEMENT, "v3.0", "Is Sneaking", "Is the player sneaking?");
        register(NONE, BOOLEAN, SWIMMING, "swimming")                .meta(MOVEMENT, "v3.0", "Is Swimming", "Is the player swimming?");
        register(NONE, BOOLEAN, ON_GROUND, "on_ground")              .meta(MOVEMENT, "v3.0", "Is On Ground", "Is the player on the ground?");

        register(NONE, BOOLEAN, SINGLEPLAYER, "singleplayer", "sp").meta(ENVIRONMENT, "v3.0", "In Singleplayer", "If the player is in a singleplayer world");
        register(NONE, BOOLEAN, MULTIPLAYER, "multiplayer", "mp")  .meta(ENVIRONMENT, "v3.0", "In Multiplayer", "If the player is in a multiplayer server");
        register(NONE, STRING, BIOME, "biome")               .meta(ENVIRONMENT, "v1.0", "Biome", "The biome the player is in. Ex: “Plains”, “Desert”");
        register(NONE, STRING, BIOME_ID, "biome_id")         .meta(ENVIRONMENT, "v1.0", "Biome ID", "The id of the biome the player is in. Ex: “minecraft:plains”, “minecraft:desert”");
        register(NONE, STRING, DIMENSION, "dimension")       .meta(ENVIRONMENT, "v1.0", "Dimension", "The dimention the player is in. Ex: “Overworld”, “The Nether”, “The End”");
        register(NONE, STRING, DIMENSION_ID, "dimension_id") .meta(ENVIRONMENT, "v1.0", "Dimension ID", "The id of the dimension the player is in. Ex: “minecraft:overworld”, “minecraft:the_nether”");
        register(WORLD, BOOLEAN, IS_RAINING, "raining")      .meta(ENVIRONMENT, "v3.0", "Is Raining", "If it's raining/snowing");
        register(WORLD, BOOLEAN, IS_THUNDERING, "thundering").meta(ENVIRONMENT, "v3.0", "Is Thundering", "If there's a thunderstorm");
        register(WORLD, BOOLEAN, IS_SNOWING, "snowing")      .meta(ENVIRONMENT, "v3.1", "Is Snowing", "If it's snowing");
        register(NONE, BOOLEAN, IN_OVERWORLD, "overworld")   .meta(ENVIRONMENT, "v1.1", "In Overworld", "If the player is in the Overworld"); //TODO: ADD TO DOCS
        register(NONE, BOOLEAN, IN_NETHER, "nether")         .meta(ENVIRONMENT, "v1.0", "In Nether", "If the player is in the Nether"); //TODO: ADD TO DOCS
        register(NONE, BOOLEAN, IN_END, "end")               .meta(ENVIRONMENT, "v1.1", "In End", "If the player is in the End"); //TODO: ADD TO DOCS

        register(NONE, INT, ENTITIES_RENDERED, "entities_rendered")    .meta(ENTITIES, "v1.0", "Entities Rendered", "Number of entities being rendered");
        register(NONE, INT, ENTITIES_LOADED, "entities_loaded")        .meta(ENTITIES, "v1.0", "Entities Loaded", "Total entities in the loaded area");
        register(SERVER_WORLD, INT, MONSTERS, "monsters")              .meta(ENTITIES, "v1.0", "Number of Monster", "Total amount of monsters in the loaded chunks");
        register(SERVER_WORLD, INT, CREATURES, "creatures")            .meta(ENTITIES, "v1.0", "Number of Creatures", "The total amount of creatures in the loaded chunks");
        register(SERVER_WORLD, INT, AMBIENT_MOBS, "ambient_mobs")      .meta(ENTITIES, "v1.0", "Number of Ambient Mobs", "The total amount of ambient mobs in the loaded chunks");
        register(SERVER_WORLD, INT, WATER_CREATURES, "water_creatures").meta(ENTITIES, "v1.0", "Number of Water Creatures", "The total amount of water creatures in the loaded chunks");
        register(SERVER_WORLD, INT, WATER_AMBIENT_MOBS, "water_ambient_mobs")                 .meta(ENTITIES, "v1.0", "Number of Ambient Water Mobs", "The total amount of ambient water mobs in the loaded chunks");
        register(SERVER_WORLD, INT, UNDERGROUND_WATER_CREATURE, "underground_water_creatures").meta(ENTITIES, "v2.0", "Number of Underground Water Creatures", "The total amount of underground water creature mobs in the loaded chunks");
        register(SERVER_WORLD, INT, AXOLOTLS, "axolotls")              .meta(ENTITIES, "v2.0", "Number of Axolotls", "The total amount of axolotls in the loaded chunks");
        register(SERVER_WORLD, INT, MISC_MOBS, "misc_mobs")            .meta(ENTITIES, "v1.0", "Number of Misc Mobs", "The total amount of miscellaneous mobs in the loaded chunks");

        register(NONE, DEC, DAY, "day")                      .meta(DAY_TIME, "v1.0", "Day", "The number of in-game days the player has been in the world");
        register(TIME, INT, TIME_HOUR_12, "hour12", "hour")  .meta(DAY_TIME, "v1.0", "MC Hour (12h)", "Hours in 12 hour format in Minecraft’s time scale");
        register(TIME, SPECIAL, TIME_HOUR_24, "hour24")      .meta(DAY_TIME, "v1.0", "MC Hour (24h)", "Hours in 25 hour format In Minecraft’s time scale");
        register(TIME, SPECIAL, TIME_MINUTES, "minute")      .meta(DAY_TIME, "v1.0", "MC Minute", "Minutes in Minecraft’s time scale");
        register(TIME, SPECIAL, TIME_SECONDS, "second")      .meta(DAY_TIME, "v1.0", "MC Second", "Seconds in Minecraft’s time scale");
        register(TIME, STRING, TIME_AM_PM, "am_pm")          .meta(DAY_TIME, "v1.0", "MC Time AM/PM", "Before noon (AM) or After noon (PM) in Minecraft’s time scale");
        register(NONE, INT, SOLAR_TIME, "time", "solar_time").meta(DAY_TIME, "v3.1", "Solar Time in Ticks", "Time in ticks between 0 - 24000 (1 mc day)");
        register(NONE, INT, LUNAR_TIME, "lunar_time")        .meta(DAY_TIME, "v3.1", "Lunar Time in Ticks", "Time in ticks between 0 - 192000 (8 mc days)");

        register(DIFFICULTY, DEC, LOCAL_DIFFICULTY, "local_difficulty")                     .meta(DIFF_LIGHT, "v1.0", "Local Difficulty", "The difficulty of the chunk the player is in");
        register(DIFFICULTY, DEC, CLAMPED_LOCAL_DIFFICULTY, "clamped_local_difficulty")     .meta(DIFF_LIGHT, "v1.0", "Local Difficulty (Clamped)", "The difficulty of the chunk the player is in (clamped)");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT, "light", "client_light")                  .meta(DIFF_LIGHT, "v1.0", "Light Level - Total", "The total light level of where the player is.");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_SKY, "light_sky", "client_light_sky")      .meta(DIFF_LIGHT, "v1.0", "Light Level - Sky", "The light level from the sky, doesn't take into account time and weather");
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_SUN, "light_sun", "client_light_sun")      .meta(DIFF_LIGHT, "v3.1", "Light Level - Sun", "The light level from the sky, taking into account time and weather"); //TODO: Fix docs to add "Added in v3.1"
        register(CLIENT_CHUNK, INT, CLIENT_LIGHT_BLOCK, "light_block", "client_light_block").meta(DIFF_LIGHT, "v1.0", "Light Level - Block", "The light level from blocks");

        register(NONE, INT, CHUNKS_RENDERED, "chunks_rendered")                 .meta(CHUNKS, "v1.0", "Chunks Rendered", "Number of chunks being rendered");
        register(NONE, INT, CHUNKS_LOADED, "chunks_loaded")                     .meta(CHUNKS, "v1.0", "Chunks Loaded", "Number of chunks in the loaded area");
        register(NONE, BOOLEAN, CHUNK_CULLING, "chunks_culling")                .meta(CHUNKS, "v1.0", "Chunk Culling", "If Chunk culling is on");
        register(WORLD, BOOLEAN, IS_SLIME_CHUNK, "slime_chunk")                 .meta(CHUNKS, "v2.0", "In Slime Chunk", "If the chunk is a slime chunk");
        register(WORLD, INT, FORCED_LOADED_CHUNKS, "force_loaded_chunks", "fc") .meta(CHUNKS, "v1.0", "Force Chunks Loaded", "Number of forced loaded chunks");
        register(SERVER_WORLD, INT, SPAWN_CHUNKS, "spawn_chunks", "sc")         .meta(CHUNKS, "v1.0", "Number of Spawn Chunks", "The total amount of spawn chunks in the world (usually 289).");

        register(SOUND, STR_INT, STREAMING_SOUNDS, "streaming_sounds", "sounds").meta(SOUNDS, "v1.0", "Sounds Playing", "The number of streaming sounds currently playing (like breaking/placing blocks)");
        register(SOUND, STR_INT, MAX_STREAMING_SOUNDS, "max_streaming_sounds", "max_sounds").meta(SOUNDS, "v1.0", "Maximum Sounds Playing", "The max amount of streaming sounds that can play at once");
        register(SOUND, STR_INT, STATIC_SOUNDS, "static_sounds")                .meta(SOUNDS, "v1.0", "Static Sounds Playing", "The number of static sounds currently playing (ambient sounds)");
        register(SOUND, STR_INT, MAX_STATIC_SOUNDS, "max_static_sounds")        .meta(SOUNDS, "v1.0", "Maximum Static Sounds Playing", "The max amount of static sounds that can play at once");
        register(NONE, DEC, MOOD, "mood")                                       .meta(SOUNDS, "v1.0", "Mood", "How close the player is to the next mood ambience sound being played");

        register(WORLD | TARGET_BLOCK_EN, SPECIAL, TARGET_BLOCK, "target_block", "tb")                     .meta(TARGET, "v2.0", "Target Block (Grass)", "The name of the block the player's looking at");
        register(WORLD | TARGET_BLOCK_EN, SPECIAL, TARGET_BLOCK_ID, "target_block_id", "tbi")              .meta(TARGET, "v2.0", "Target Block ID", "The id of the block the player's looking at");
        register(WORLD | Enabled.TARGET_BLOCK_EN, INT, TARGET_BLOCK_X, "target_block_x", "target_x", "tbx").meta(TARGET, "v1.0", "Target Block X", "The x coordinate of the block the player’s looking at");
        register(WORLD | Enabled.TARGET_BLOCK_EN, INT, TARGET_BLOCK_Y, "target_block_y", "target_y", "tby").meta(TARGET, "v1.0", "Target Block Y", "The y coordinate of the block the player’s looking at");
        register(WORLD | Enabled.TARGET_BLOCK_EN, INT, TARGET_BLOCK_Z, "target_block_z", "target_z", "tbz").meta(TARGET, "v1.0", "Target Block Z", "The z coordinate of the block the player’s looking at");
        register(WORLD | Enabled.TARGET_BLOCK_EN, INT, TARGET_BLOCK_DISTANCE, "target_block_distance", "target_distance", "tbd").meta(TARGET, "v3.1", "Target Block Distance", "How many blocks away is the block the player's looking at");
        register(WORLD | TARGET_FLUID_EN, SPECIAL, TARGET_FLUID, "target_fluid", "tf")         .meta(TARGET, "v2.0", "Target Fluid (Water)", "The name of the fluid the player's looking at");
        register(WORLD | TARGET_FLUID_EN, SPECIAL, TARGET_FLUID_ID, "target_fluid_id", "tfi")  .meta(TARGET, "v2.0", "Target Fluid ID", "The id of the fluid the player's looking at");
        register(WORLD | Enabled.TARGET_FLUID_EN, INT, TARGET_FLUID_X, "target_fluid_x", "tfx").meta(TARGET, "v2.0", "Target Fluid X", "The x coordinate of the fluid the player’s looking at");
        register(WORLD | Enabled.TARGET_FLUID_EN, INT, TARGET_FLUID_Y, "target_fluid_y", "tfy").meta(TARGET, "v2.0", "Target Fluid Y", "The y coordinate of the fluid the player’s looking at");
        register(WORLD | Enabled.TARGET_FLUID_EN, INT, TARGET_FLUID_Z, "target_fluid_z", "tfz").meta(TARGET, "v2.0", "Target Fluid Z", "The z coordinate of the fluid the player’s looking at");
        register(WORLD | Enabled.TARGET_FLUID_EN, INT, TARGET_FLUID_DISTANCE, "target_fluid_distance", "tfd").meta(TARGET, "v3.1", "Target Fluid Distance", "How many blocks away is the fluid the player's looking at");
        register(NONE, STRING, TARGET_ENTITY, "target_entity", "te")                .meta(TARGET, "v2.0", "Target Entity (Pig)", "The type of the entity the player's looking at (Ex: Pig, Player)");
        register(NONE, STRING, TARGET_ENTITY_ID, "target_entity_id", "tei")         .meta(TARGET, "v2.0", "Target Entity ID", "The id of the entity the player's looking at");
        register(NONE, STRING, TARGET_ENTITY_NAME, "target_entity_name", "ten")     .meta(TARGET, "v3.1", "Target Entity Custom Name", "The name of the entity the player's looking at (Ex: Notch, Fluffy)");
        register(NONE, STRING, TARGET_ENTITY_UUID, "target_entity_uuid", "teu")     .meta(TARGET, "v3.1", "Target Entity UUID", "The uuid of the entity the player's looking at");
        register(NONE, DEC, TARGET_ENTITY_X, "target_entity_x", "tex")              .meta(TARGET, "v2.0", "Target Entity X", "The x coordinate of the entity the player’s looking at");
        register(NONE, DEC, TARGET_ENTITY_Y, "target_entity_y", "tey")              .meta(TARGET, "v2.0", "Target Entity Y", "The y coordinate of the entity the player’s looking at");
        register(NONE, DEC, TARGET_ENTITY_Z, "target_entity_z", "tez")              .meta(TARGET, "v2.0", "Target Entity Z", "The z coordinate of the entity the player’s looking at");
        register(NONE, DEC, TARGET_ENTITY_DISTANCE, "target_entity_distance", "ted").meta(TARGET, "v3.1", "Target Entity Distance", "How many blocks away is the entity the player's looking at"); //TODO: Fix Docs

        register(NONE, BOOLEAN, FISHING_IS_CAST, "fishing_is_cast")            .meta(FISHING, "v3.1", "Is Cast", "If the fishing rod is cast");
        register(NONE, BOOLEAN, FISHING_IS_HOOKED, "fishing_is_hooked")        .meta(FISHING, "v3.1", "Is Hooked into Entity", "If the fishing rod is hooked into an entity");
        register(NONE, BOOLEAN, FISHING_HAS_CAUGHT, "fishing_has_caught")      .meta(FISHING, "v3.1", "Fish Caught on Hook", "If a fish, treasure, or junk is currently hooked and the bobber is bobbing");
        register(NONE, BOOLEAN, FISHING_IN_OPEN_WATER, "fishing_in_open_water").meta(FISHING, "v3.1", "Fishing in Open Water", "If the bobber is in open water (a 5x4x5 area of water/air/lilly, needed to get treasure)");
        register(NONE, DEC, FISHING_HOOK_DISTANCE, "fishing_hook_distance")    .meta(FISHING, "v3.1", "Fishing Hook Distance", "How far away the bobber is from you");
        register(NONE, STRING, HOOKED_ENTITY, "hooked_entity", "he")           .meta(FISHING, "v3.1", "Hooked Entity (Pig)", "The type of the entity hooked by the fishing rod (Ex: Pig, Player)");
        register(NONE, STRING, HOOKED_ENTITY_ID, "hooked_entity_id", "hei")    .meta(FISHING, "v3.1", "Hooked Entity ID", "The id of the entity hooked by the fishing rod");
        register(NONE, STRING, HOOKED_ENTITY_NAME, "hooked_entity_name", "hen").meta(FISHING, "v3.1", "Hooked Entity Custom Name", "The name of the entity hooked by the fishing rod (Ex: Notch, Fluffy)");
        register(NONE, STRING, HOOKED_ENTITY_UUID, "hooked_entity_uuid", "heu").meta(FISHING, "v3.1", "Hooked Entity UUID", "The uuid of the entity hooked by the fishing rod");
        register(NONE, DEC, HOOKED_ENTITY_X, "hooked_entity_x", "hex")         .meta(FISHING, "v3.1", "Hooked Entity X", "The x coordinate of the entity hooked by the fishing rod");
        register(NONE, DEC, HOOKED_ENTITY_Y, "hooked_entity_y", "hey")         .meta(FISHING, "v3.1", "Hooked Entity Y", "The y coordinate of the entity hooked by the fishing rod");
        register(NONE, DEC, HOOKED_ENTITY_Z, "hooked_entity_z", "hez")         .meta(FISHING, "v3.1", "Hooked Entity Z", "The z coordinate of the entity hooked by the fishing rod");
        register(NONE, DEC, HOOKED_ENTITY_DISTANCE, "hooked_entity_distance", "hed").meta(FISHING, "v3.1", "Hooked Entity Distance", "How many blocks away the entity hooked by the fishing rod is");

        register(CPU, DEC, CPU_USAGE, "cpu_usage", "cpu")                       .meta(PC_INFO, "v2.0", "CPU Usage", "CPU Usage as a percentage");
        register(PERFORMANCE_METRICS, DEC, GPU_USAGE, "gpu_usage", "gpu")       .meta(PC_INFO, "v2.0", "GPU Usage", "GPU Usage as a percentage");
        register(NONE, DEC, MEMORY_USED_PERCENTAGE, "memory_used_percentage")   .meta(PC_INFO, "v1.0", "Memory Used (%)", "Percentage of used memory to total memory");
        register(NONE, DEC, MEMORY_USED, "memory_used")                         .meta(PC_INFO, "v1.0", "Memory Used (MB)", "Memory being used");
        register(NONE, DEC, TOTAL_MEMORY, "memory_total")                       .meta(PC_INFO, "v1.0", "Memory Total", "Total memory minecraft has access to");
        register(NONE, DEC, ALLOCATED_PERCENTAGE, "memory_allocated_percentage").meta(PC_INFO, "v1.0", "Memory Allocated (%)", "Percentage of allocated to total memory");
        register(NONE, DEC, ALLOCATED, "memory_allocated")                      .meta(PC_INFO, "v1.0", "Memory Allocated (MB)", "Total memory will attempt to use");

        register(NONE, STRING, CPU_NAME, "cpu_name")         .meta(PC_INFO, "v2.1", "CPU Name", "The name of the CPU");
        register(NONE, STRING, GPU_NAME, "gpu_name")         .meta(PC_INFO, "v2.1", "GPU Name", "The name of the GPU");
        register(NONE, STRING, JAVA_VERSION, "java_version") .meta(PC_INFO, "v1.0", "Java Version", "The version of java");
        register(NONE, INT, JAVA_BIT, "java_bit")            .meta(PC_INFO, "v1.0", "Java 64bit or 32bit", "If the java running is 32bit or 64bit");
        register(NONE, INT, CPU_CORES, "cpu_cores")          .meta(PC_INFO, "v2.1", "Number of CPU Cores", "Number of cores your cpu has");
        register(NONE, INT, CPU_THREADS, "cpu_threads")      .meta(PC_INFO, "v2.1", "Number of CPU Threads", "Number of threads your cpu has");
        register(NONE, INT, DISPLAY_WIDTH, "display_width")  .meta(PC_INFO, "v1.0", "Display Width", "Minecraft’s windows width");
        register(NONE, INT, DISPLAY_HEIGHT, "display_height").meta(PC_INFO, "v1.0", "Display Height", "Minecraft’s windows height");
        register(NONE, INT, DISPLAY_REFRESH_RATE, "display_refresh_rate").meta(PC_INFO, "v2.1", "Display Refresh", "Minecraft’s windows refresh rate");

        register(NONE, INT, QUEUED_TASKS, "queued_tasks").meta(TECHNICAL, "v1.0", "Queued Tasks (pC)", "Pending chunks to be batched (pC in the F3 screen)");
        register(NONE, INT, UPLOAD_QUEUE, "upload_queue").meta(TECHNICAL, "v1.0", "Upload Queue (pU)", "Pending uploads to video card (pU in the F3 screen)");
        register(NONE, INT, BUFFER_COUNT, "buffer_count").meta(TECHNICAL, "v1.0", "Buffer Count (aB)", "Available buffers to use in the batching process. (aB in the F3 screen)");
        register(CLIENT_CHUNK, INT, CLIENT_HEIGHT_MAP_SURFACE, "client_height_map_surface", "chs")                .meta(TECHNICAL, "v1.0", "Height Map - Surface", "The Y coordinate of the highest non-air block at the player’s X/Z coordinate. (reported by the client)");
        register(CLIENT_CHUNK, INT, CLIENT_HEIGHT_MAP_MOTION_BLOCKING, "client_height_map_motion_blocking", "chm").meta(TECHNICAL, "v1.0", "Height Map - Motion Blocking", "The Y coordinate of the highest block at the player’s X/Z coordinate that has a blocking-motion material or is liquid. (reported by the client)");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_OCEAN_FLOOR, "server_height_map_ocean_floor", "sho")        .meta(TECHNICAL, "v1.0", "Height Map - Ocean Floor", "The Y coordinate of the highest block at the player’s X/Z coordinate that has a blocking-motion material. (reported by the server)");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES, "server_height_map_motion_blocking_no_leaves", "shml").meta(TECHNICAL, "v1.0", "Height Map - Motion block, No Leaves", "The Y coordinate of the highest block at the player’s X/Z coordinate that has a blocking-motion material and is not leaves. (reported by the server)");


        register(NONE, INT, MAX_FPS, "max_fps")                              .metaD(DEPRECATED, "v1.0", "Max FPS", "Fps Limit set in the settings, \"-\" if set to max");
        register(NONE, INT, BIOME_BLEND, "biome_blend")                      .metaD(DEPRECATED, "v1.0", "Biome Blend", "The player's biome blend setting");
        register(NONE, INT, SIMULATION_DISTANCE, "simulation_distance", "sd").metaD(DEPRECATED, "v2.0", "Simulation Distance", "Simulation Distance set in the settings");
        register(NONE, INT, RENDER_DISTANCE, "render_distance")              .metaD(DEPRECATED, "v1.0", "Render Distance", "The player’s render distance setting");
        register(NONE, BOOLEAN, VSYNC, "vsync")                              .metaD(DEPRECATED, "v1.0", "VSync", "If vsync is enabled or not");
        register(NONE, SPECIAL, CLOUDS, "clouds")                            .metaD(DEPRECATED, "v1.0", "Clouds Quality", "Cloud Quality (Off, Fast, Fancy)");
        register(NONE, SPECIAL, GRAPHICS_MODE, "graphics_mode")              .metaD(DEPRECATED, "v1.0", "Graphics Mode", "Graphics Quality (Fast, Fancy, or Fabulous)");
        register(NONE, BOOLEAN, ITEM_HAS_DURABILITY, "item_has_durability", "item_has_dur")                 .metaD(DEPRECATED, "v2.0", "Mainhand Item Has Durability", "If the item in the player's main hand can have durability");
        register(NONE, BOOLEAN, OFFHAND_ITEM_HAS_DURABILITY, "offhand_item_has_durability", "oitem_has_dur").metaD(DEPRECATED, "v2.0", "Offhand Item has Durability", "If the item in the player's offhand can have durability");
        register(NONE, INT, ITEM_DURABILITY, "item_durability", "item_dur")                                 .metaD(DEPRECATED, "v2.0", "Mainhand Item Durability", "Durability of item in player's main hand");
        register(NONE, INT, ITEM_MAX_DURABILITY, "item_max_durability", "item_max_dur")                     .metaD(DEPRECATED, "v2.0", "Mainhand Item max Durability", "Max durability of item in player's main hand");
        register(NONE, INT, OFFHAND_ITEM_DURABILITY, "offhand_item_durability", "oitem_dur")                .metaD(DEPRECATED, "v2.0", "Offhand Item Durability", "Durability of item in player's offhand");
        register(NONE, INT, OFFHAND_ITEM_MAX_DURABILITY, "offhand_item_max_durability", "oitem_max_dur")    .metaD(DEPRECATED, "v2.0", "Offhand Max Item Durability", "Max durability of item in player's offhand");
        register(NONE, DEC, ITEM_DURABILITY_PERCENT, "item_durability_percent", "item_dur_per")             .metaD(DEPRECATED, "v2.0", "Mainhand Item Durability (%)", "Percentage of durability left of item in player's main hand");
        register(NONE, DEC, OFFHAND_ITEM_DURABILITY_PERCENT, "offhand_item_durability_percent", "oitem_dur_per").metaD(DEPRECATED, "v2.0", "Offhand Item Durability (%)", "Percentage of durability left of item in player's offhand");
        register(NONE, SPECIAL, ITEM, "item")                          .metaD(DEPRECATED, "v2.0", "Mainhand Item (Stone Sword)", "Name of item in player's main hand");
        register(NONE, SPECIAL, ITEM_NAME, "item_name")                .metaD(DEPRECATED, "v2.0 (Maybe)", "Mainhand Item Custom Name", "Custom name of item in the player's main hand");
        register(NONE, SPECIAL, ITEM_ID, "item_id")                    .metaD(DEPRECATED, "v2.0", "Mainhand Item ID", "ID of item in the player's main hand");
        register(NONE, SPECIAL, OFFHAND_ITEM, "offhand_item", "oitem") .metaD(DEPRECATED, "v2.0", "Offhand Item (Stone Sword)", "Name of item in the player's offhand");
        register(NONE, SPECIAL, OFFHAND_ITEM_NAME, "offhand_item_name").metaD(DEPRECATED, "v2.0 (Maybe)", "Offhand Item Custom Name", "Custom name of item in the player's offhand");
        register(NONE, SPECIAL, OFFHAND_ITEM_ID, "offhand_item_id", "oitem_id")  .metaD(DEPRECATED, "v2.0", "Offhand Item ID", "ID of item in the player's offhand");
        register(WORLD | SERVER_CHUNK, INT, SERVER_LIGHT_SKY, "server_light_sky").metaD(DEPRECATED, "v1.0", "Light Level - Sky (Server)", "The light level from the sky, doesn't take into account time and weather (reported by the server)"); //TODO: Fix Docs
        register(WORLD | SERVER_CHUNK, INT, SERVER_LIGHT_BLOCK, "server_light_block")             .metaD(DEPRECATED, "v1.0", "Light Level - Block (Server)", "The light level from blocks (reported by the server)");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_SURFACE, "server_height_map_surface", "shs").metaD(DEPRECATED, "v1.0", "Height Map - Surface (Server)", "The Y coordinate of the highest non-air block at the player’s X/Z coordinate. (reported by the server)");
        register(SERVER_CHUNK, INT, SERVER_HEIGHT_MAP_MOTION_BLOCKING, "server_height_map_motion_blocking", "shm").metaD(DEPRECATED, "v1.0", "Height Map - Motion Blocking (Server)", "The Y coordinate of the highest block at the player’s X/Z coordinate that has a blocking-motion material or is liquid. (reported by the server)");










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

        final Pattern TEXTURE_ICON_PATTERN = Pattern.compile("((?:[a-z0-9/._-]+:)?[a-z0-9/._-]+)(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?");
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
