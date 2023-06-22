package com.minenash.customhud.core;

import java.util.HashMap;
import java.util.Map;

public class SupplierRegistry {

    public static final Map<String, SupplierEntry<?>> reg = new HashMap<>();

    public static <T> void add(int en, SupplierEntry.Type type, T supplier, String... names) {
        for (String name : names)
            reg.put(name, new SupplierEntry<>(en, type, supplier));
    }

    public static SupplierEntry<?> get(String name) {
        return reg.get(name);
    }
}
