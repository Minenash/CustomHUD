package com.minenash.customhud.mc1_20.mod_compat;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.registry.VariableRegistry;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.core.registry.VariableRegistry.SupplierEntryType.*;

public class SodiumCompat {

    private static List<String> debugLines;

    public static final Supplier<String> VERSION = SodiumClientMod::getVersion;
    public static final Supplier<String> CHUNK_ARENA_ALLOCATOR = () -> SodiumClientMod.options().advanced.arenaMemoryAllocator.name();
    public static final Supplier<String> STAGING_BUFFER = () -> debugLines.get(3).substring(16);

    public static final Supplier<String> BUFFER_OBJECTS = () -> debugLines.get(1).substring(23);
    public static final Supplier<String> MEMORY_USED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[0];
    public static final Supplier<String> MEMORY_ALLOCATED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[1];

    public static void registerCompat() {

        VariableRegistry.register(Enabled.NONE, STRING, VERSION, "sodium_version");
        VariableRegistry.register(Enabled.NONE, STRING, CHUNK_ARENA_ALLOCATOR, "sodium_chunk_arena_allocator");
        VariableRegistry.register(Enabled.NONE, STRING, STAGING_BUFFER, "sodium_staging_buffers");

        VariableRegistry.register(Enabled.NONE, STR_INT, BUFFER_OBJECTS, "sodium_buffer_objects");
        VariableRegistry.register(Enabled.NONE, STR_INT, MEMORY_USED, "sodium_memory_used");
        VariableRegistry.register(Enabled.NONE, STR_INT, MEMORY_ALLOCATED, "sodium_memory_allocated");

        VariableRegistry.registerComplexData("sodium:debug", () -> {
            SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
            if (renderer != null)
                debugLines = (List<String>) renderer.getMemoryDebugStrings();
        });

    }


}
