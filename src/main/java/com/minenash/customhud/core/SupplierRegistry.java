package com.minenash.customhud.core;

import java.util.HashMap;
import java.util.Map;

public class SupplierRegistry {

    public static final Map<String, SEntry<?>> reg = new HashMap<>();

    public static <T> void add(int en, SEntry.Type type, T supplier, String... names) {
        for (String name : names)
            reg.put(name, new SEntry<>(en, type, supplier));
    }

    public static SEntry<?> get(String name) {
        return reg.get(name);
    }
}
