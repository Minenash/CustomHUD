package com.minenash.customhud.mc1_20.mod_compat;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.registry.MetaData;
import com.minenash.customhud.core.registry.VariableRegistry;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.core.data.Enabled.NONE;
import static com.minenash.customhud.core.registry.MetaData.DefaultCategories.FROM_MODS;
import static com.minenash.customhud.core.registry.VariableRegistry.SupplierEntryType.*;
import static com.minenash.customhud.core.registry.VariableRegistry.register;

public class SodiumCompat {

    private static List<String> debugLines;

    public static final Supplier<String> VERSION = SodiumClientMod::getVersion;
    public static final Supplier<String> CHUNK_ARENA_ALLOCATOR = () -> SodiumClientMod.options().advanced.arenaMemoryAllocator.name();
    public static final Supplier<String> STAGING_BUFFER = () -> debugLines.get(3).substring(16);

    public static final Supplier<String> BUFFER_OBJECTS = () -> debugLines.get(1).substring(23);
    public static final Supplier<String> MEMORY_USED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[0];
    public static final Supplier<String> MEMORY_ALLOCATED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[1];

    public static void registerCompat() {

        register(NONE, STRING, VERSION, "sodium_version").meta(FROM_MODS, "v2.0", "Sodium Version", "The version of the installed mod sodium");
        register(NONE, STRING, CHUNK_ARENA_ALLOCATOR, "sodium_chunk_arena_allocator").meta(FROM_MODS, "v2.0", "Sodium Chunk Arena Allocator", "<i>No Description Provided</i>");
        register(NONE, STRING, STAGING_BUFFER, "sodium_staging_buffers").meta(FROM_MODS, "v2.0", "Sodium Staging Buffers", "<i>No Description Provided</i>");

        register(NONE, STR_INT, BUFFER_OBJECTS, "sodium_buffer_objects").meta(FROM_MODS, "v2.0", "Sodium Buffer Objects", "<i>No Description Provided</i>");
        register(NONE, STR_INT, MEMORY_USED, "sodium_memory_used").meta(FROM_MODS, "v2.0", "Sodium Memory Used", "<i>No Description Provided</i>");
        register(NONE, STR_INT, MEMORY_ALLOCATED, "sodium_memory_allocated").meta(FROM_MODS, "v2.0", "Sodium Memory Allocated", "<i>No Description Provided</i>");

        VariableRegistry.registerComplexData("sodium:debug", () -> {
            SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
            if (renderer != null)
                debugLines = (List<String>) renderer.getMemoryDebugStrings();
        });

    }


}
