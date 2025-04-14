package com.minenash.customhud.v5;

import net.minecraft.stat.StatFormatter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AttrList {

    public record Entry<T>(List<String> attr, Class<?> clazz, NumberDefaultOptions numDefaultOptions, Function<T,?> func) {
        public static <T> Entry<T> of(List<String> attr, Class<?> clazz, Function<T,?> func) { return new Entry<>(attr, clazz, null, func); }
        public static <T> Entry<T> of(String attr, Class<?> clazz, Function<T,?> func) { return new Entry<>(List.of(attr), clazz, null, func); }
        public static <T> Entry<T> of(List<String> attr, Class<?> clazz, int precision, Function<T,?> func) { return new Entry<>(attr, clazz, NumberDefaultOptions.of(precision), func); }
        public static <T> Entry<T> of(String attr, Class<?> clazz, int precision, Function<T,?> func) { return new Entry<>(List.of(attr), clazz, NumberDefaultOptions.of(precision), func); }
        public static <T> Entry<T> of(List<String> attr, Class<?> clazz, StatFormatter formatter, Function<T,?> func) { return new Entry<>(attr, clazz, NumberDefaultOptions.of(formatter), func); }
        public static <T> Entry<T> of(String attr, Class<?> clazz, StatFormatter formatter, Function<T,?> func) { return new Entry<>(List.of(attr), clazz, NumberDefaultOptions.of(formatter), func); }
    }

    public static <T> Map<String,Attribute<T>> build(Entry<T>... entries) {
        Map<String,Attribute<T>> map = new HashMap<>();

        for (var entry : entries) {
            Attribute<T> attribute = new Attribute<>(entry.clazz, entry.numDefaultOptions, entry.func);
            for (String name : entry.attr)
                map.put(name, attribute);
        }

        return map;
    }
}
