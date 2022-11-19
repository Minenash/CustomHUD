package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.HudElements.HudElement;

import java.util.function.Supplier;

public class NumberSupplierElement implements HudElement {

    public record Entry(Supplier<Number> supplier, int precision) {}
    public static Entry of(Supplier<Number> supplier, int precision) {
        return new Entry(supplier, precision);
    }

    private final Supplier<Number> supplier;
    private final int precision;
    private final double scale;

    public NumberSupplierElement(Entry entry, double scale) {
        this(entry.supplier, scale, entry.precision);
    }

    public NumberSupplierElement(Entry entry, double scale, int precision) {
        this(entry.supplier, scale, precision);
    }

    public NumberSupplierElement(Supplier<Number> supplier, double scale) {
        this(supplier, scale, 0);
    }

    public NumberSupplierElement(Supplier<Number> supplier, double scale, int precision) {
        this.supplier = supplier;
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public String getString() {
        try {
            if (precision == 0)
                return Integer.toString((int)(supplier.get().doubleValue() * scale));

            double exponent = Math.pow(10, precision);
            return Double.toString( (int)(supplier.get().doubleValue() * scale * exponent) / exponent );
        }
        catch (Exception _e) {
            return "-";
        }
    }

    @Override
    public Number getNumber() {
        return sanitize(supplier, Double.NaN);
    }

    @Override
    public boolean getBoolean() {
        return sanitize(supplier, Double.NaN).doubleValue() > 0;
    }

}
