package com.minenash.customhud.v5;

import net.minecraft.stat.StatFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ElementRegistry {

    public static final Map<String, Supplier<?>> registry = new HashMap<>();
    public static final Map<String, NumberDefaultOptions> numDefaultOptions = new HashMap<>();

    public static void register(String name, Supplier<?> supplier) {
        registry.put(name, supplier);
    }

    public static void register(String[] names, Supplier<?> supplier) {
        for (String name : names)
            registry.put(name, supplier);
    }

    public static void register(String name, int precision, Supplier<?> supplier) {
        registry.put(name, supplier);
        numDefaultOptions.put(name, NumberDefaultOptions.of(precision));
    }

    public static void register(String[] names, int precision, Supplier<?> supplier) {
        for (String name : names)
            register(name, precision, supplier);
    }

    public static void register(String name, StatFormatter formatter, Supplier<?> supplier) {
        registry.put(name, supplier);
        numDefaultOptions.put(name, NumberDefaultOptions.of(formatter));
    }

    public static void register(String[] names, StatFormatter formatter, Supplier<?> supplier) {
        for (String name : names)
            register(name, formatter, supplier);
    }

    public static String[] n(String... names) {
        return names;
    }



}
