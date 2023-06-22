package com.minenash.customhud.mc1_20.mod_compat;

import com.minenash.customhud.core.elements.SupplierElements;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.mc1_20.mod_compat.CustomHudRegistry.registerElement;
import static com.minenash.customhud.mc1_20.mod_compat.CustomHudRegistry.registerComplexData;

public class SodiumCompat {

    private static List<String> debugLines;

    public static final Supplier<String> VERSION = SodiumClientMod::getVersion;
    public static final Supplier<String> CHUNK_ARENA_ALLOCATOR = () -> SodiumClientMod.options().advanced.arenaMemoryAllocator.name();
    public static final Supplier<String> STAGING_BUFFER = () -> debugLines.get(3).substring(16);

    public static final Supplier<String> BUFFER_OBJECTS = () -> debugLines.get(1).substring(23);
    public static final Supplier<String> MEMORY_USED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[0];
    public static final Supplier<String> MEMORY_ALLOCATED = () -> debugLines.get(2).substring(15, debugLines.get(2).length() - 4).split("/")[1];

    public static void registerCompat() {

        registerElement("sodium_version", (_str) -> new SupplierElements.Str(VERSION));
        registerElement("sodium_chunk_arena_allocator", (_str) -> new SupplierElements.Str(CHUNK_ARENA_ALLOCATOR));
        registerElement("sodium_staging_buffers", (_str) -> new SupplierElements.Str(STAGING_BUFFER));

        registerElement("sodium_buffer_objects", (_str) -> new SupplierElements.StrInt(BUFFER_OBJECTS));
        registerElement("sodium_memory_used", (_str) -> new SupplierElements.StrInt(MEMORY_USED));
        registerElement("sodium_memory_allocated", (_str) -> new SupplierElements.StrInt(MEMORY_ALLOCATED));

        registerComplexData(() -> {
            SodiumWorldRenderer renderer = SodiumWorldRenderer.instanceNullable();
            if (renderer != null)
                debugLines = (List<String>) renderer.getMemoryDebugStrings();
        });

    }


}
