package com.minenash.customhud;

import com.minenash.customhud.HudElements.HudElement;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public class CustomHudRegistry implements Iterable<Map.Entry<String, Function<String,HudElement>>>{

    private static final Map<String, Function<String,HudElement>> registry = new HashMap<>();

    public static void register(String name, Function<String,HudElement> element) {
        registry.put(name, element);
    }

    public static void unregister(String name) {
        registry.remove(name);
    }

    public static HudElement get(String key, String variable) {
        Function<String,HudElement> function = registry.get(key);
        return function == null ? null : function.apply(variable);
    }

    public static boolean has(String key) {
        return registry.containsKey(key);
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<String, Function<String,HudElement>>> iterator() {
        return registry.entrySet().iterator();
    }
}
