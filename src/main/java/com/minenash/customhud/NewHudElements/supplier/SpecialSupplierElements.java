package com.minenash.customhud.NewHudElements.supplier;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.NewHudElements.HudElement2;

import java.util.function.Supplier;

public class SpecialSupplierElements implements HudElement2 {

    public static final Entry TIME_HOUR_24 = of( () -> String.format("%02d", ComplexData.timeOfDay / 1000),
                                                 () -> ComplexData.timeOfDay / 1000,
                                                 () -> ComplexData.timeOfDay / 1000 >= 12);

    public static final Entry TIME_MINUTE = of( () -> String.format("%02d",(int)((ComplexData.timeOfDay % 1000) / (1000/60F))),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)) != 0);

    public record Entry(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {}
    public static Entry of(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {
        return new Entry(stringSupplier, numberSupplier, booleanSupplier);
    }

    private final Entry entry;

    public SpecialSupplierElements(Entry entry) {
        this.entry = entry;
    }

    @Override
    public String getString() {
        return sanitize(entry.stringSupplier, "-");
    }

    @Override
    public Number getNumber() {
        return sanitize(entry.numberSupplier, Double.NaN);
    }

    @Override
    public boolean getBoolean() {
        return sanitize(entry.booleanSupplier, false);
    }

}
