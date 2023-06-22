package com.minenash.customhud.mc1_20.elements.supplier;

import com.minenash.customhud.mc1_20.ComplexData;
import net.minecraft.client.MinecraftClient;

import java.util.function.Supplier;

public class StringIntSuppliers {

    public static final Supplier<String> PARTICLES = () -> MinecraftClient.getInstance().particleManager.getDebugString();

    public static final Supplier<String> STREAMING_SOUNDS = () -> ComplexData.sounds[0];
    public static final Supplier<String> MAX_STREAMING_SOUNDS = () -> ComplexData.sounds[1];
    public static final Supplier<String> STATIC_SOUNDS = () -> ComplexData.sounds[2];
    public static final Supplier<String> MAX_STATIC_SOUNDS = () -> ComplexData.sounds[3];

}
