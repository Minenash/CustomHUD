package com.minenash.customhud.core.registry;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.elements.HudElement;
import com.minenash.customhud.core.elements.SupplierElements;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings({"rawtypes", "unchecked"})
public class VariableRegistry {

    private static final Map<String, Function<VariableParseContext, HudElement>> registry = new TreeMap<>();
    private static final Map<String, Runnable> complexData = new TreeMap<>();

    public enum SupplierEntryType {BOOLEAN, STRING, STR_INT, INT, DEC, SPECIAL}
    private record SupplierEntry<T>(int enabledMask, SupplierEntryType type, T entry) {}
    private static final Map<String, SupplierEntry<?>> supplierRegistry = new HashMap<>();

    public static boolean register(String id, Function<VariableParseContext, HudElement> parser) {
        if (registry.containsKey(id)) return false;
        registry.put(id, parser);
        return true;
    }

    public static <T> void register(SupplierEntryType type, T supplier, String... names) {
        register(Enabled.NONE, type, supplier, names);
    }

    public static <T> void register(int en, SupplierEntryType type, T supplier, String... names) {
        for (String name : names)
            supplierRegistry.put(name, new SupplierEntry<>(en, type, supplier));
    }

    public static boolean unregister(String id) {
        return registry.remove(id) != null || supplierRegistry.remove(id) != null;
    }

    public static HudElement get(VariableParseContext context) {

        SupplierEntry entry = supplierRegistry.get(context.base());

        if (entry != null) {
            context.enabled().add(entry.enabledMask());

            return switch (entry.type()) {
                case BOOLEAN -> new SupplierElements.Bool((Supplier) entry.entry());
                case STRING -> new SupplierElements.Str((Supplier) entry.entry());
                case STR_INT -> new SupplierElements.StrInt((Supplier) entry.entry());
                case INT -> new SupplierElements.Num((Supplier) entry.entry(), context.flags().scale, context.flags().precision);
                case DEC -> new SupplierElements.Num((SupplierElements.Num.Entry) entry.entry(), context.flags());
                case SPECIAL -> new SupplierElements.Special((SupplierElements.Special.Entry) entry.entry());
            };
        }

        for (var parser : registry.values()) {
            HudElement element = parser.apply(context);
            if (element != null) return element;
        }
        return null;
    }

    public static boolean registerComplexData(String id, Runnable code) {
        if (complexData.containsKey(id)) return false;
        complexData.put(id, code);
        return true;
    }

    public static boolean unregisterComplexData(String id) {
        return complexData.remove(id) != null;
    }

    public static boolean hasComplexData(String id) {
        return complexData.containsKey(id);
    }

    public static void runComplexData() {
        for (Runnable runnable : complexData.values())
            runnable.run();
    }

}
