package com.minenash.customhud.mod_compat;

import com.minenash.customhud.core.elements.HudElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class CustomHudRegistry implements Iterable<Map.Entry<String, Function<String,HudElement>>>{

    private static final Map<String, Function<String,HudElement>> registry = new HashMap<>();
    private static final List<Runnable> complexData = new ArrayList<>();

    public static void registerElement(String name, Function<String,HudElement> element) {
        registry.put(name, element);
    }

    public static void unregisterElement(String name) {
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



    public static void registerComplexData(Runnable function) {
        complexData.add(function);
    }

    public static void runComplexData() {
        for (Runnable runnable : complexData)
            runnable.run();
    }

}
