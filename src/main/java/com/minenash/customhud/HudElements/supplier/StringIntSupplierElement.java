package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

public class StringIntSupplierElement implements HudElement {

    public static final Supplier<String> PARTICLES = () -> MinecraftClient.getInstance().particleManager.getDebugString();

    public static final Supplier<String> STREAMING_SOUNDS = () -> ComplexData.sounds[0];
    public static final Supplier<String> MAX_STREAMING_SOUNDS = () -> ComplexData.sounds[1];
    public static final Supplier<String> STATIC_SOUNDS = () -> ComplexData.sounds[2];
    public static final Supplier<String> MAX_STATIC_SOUNDS = () -> ComplexData.sounds[3];

    public static final Supplier<String> CLIENT_CHUNK_CACHE_CAPACITY = () -> ComplexData.clientChunkCache[0];
    public static final Supplier<String> CLIENT_CHUNK_CACHE = () -> ComplexData.clientChunkCache[1];
    public static final Supplier<String> SERVER_CHUNK_CACHE = () -> ComplexData.serverWorld == null ? null : ComplexData.serverWorld.getDebugString().substring(18);

    private final Supplier<String> supplier;

    public StringIntSupplierElement(Supplier<String> supplier) {
        this.supplier = supplier;
    }


    @Override
    public String getString() {
        return sanitize(supplier, "-");
    }

    @Override
    public Number getNumber() {
        try {
            String value = supplier.get();
            return value == null ? Double.NaN : Integer.parseInt(value);
        }
        catch (Exception e) {
            return Double.NaN;
        }
    }

    @Override
    public boolean getBoolean() {
        return getNumber().doubleValue() > 0;
    }

}
