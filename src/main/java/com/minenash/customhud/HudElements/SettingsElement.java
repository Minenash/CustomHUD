package com.minenash.customhud.HudElements;

import com.minenash.customhud.Flags;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.DecimalSupplierElement;
import com.minenash.customhud.HudElements.supplier.IntegerSupplierElement;
import com.minenash.customhud.mixin.GameOptionsInvoker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SettingsElement {

    public static boolean initialized = false;

    //Boolean, Integer, Double
    private static final Map<String, SimpleOption<?>> simpleOptions = new HashMap<>();

    private static void init() {
        ((GameOptionsInvoker)MinecraftClient.getInstance().options).invokeAccept(new GameOptions.Visitor() {
            @Override
            public <T> void accept(String key, SimpleOption<T> option) {
                T value = option.getValue();
                if (value instanceof Boolean || value instanceof Integer || value instanceof Double)
                    simpleOptions.put(key, option);
            }

            @Override
            public int visitInt(String key, int current) {
                return 0;
            }

            @Override
            public boolean visitBoolean(String key, boolean current) {
                return current;
            }

            @Override
            public String visitString(String key, String current) {
                return current;
            }

            @Override
            public float visitFloat(String key, float current) {
                return current;
            }

            @Override
            public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                return current;
            }
        });
    }

    public static HudElement create(String setting, Flags flags) {
        if (!initialized)
            init();
        initialized = true;

        SimpleOption<?> option = simpleOptions.get(setting);
        if (option == null)
            return null;
        if (option.getValue() instanceof Boolean)
            return new BooleanSupplierElement(() -> (Boolean) option.getValue());
        if (option.getValue() instanceof Integer)
            return new IntegerSupplierElement(() -> (Integer) option.getValue());
        if (option.getValue() instanceof Double)
            return new DecimalSupplierElement(DecimalSupplierElement.of( () -> (Double) option.getValue(), flags.precision != -1 ? flags.precision : 1), 1);
        return null;
    }

}
