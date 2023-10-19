package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.HudElements.HudElement;
import net.minecraft.stat.StatFormatter;

import java.util.function.Supplier;

public class NumberSupplierElement implements HudElement {

    public record Entry(Supplier<Number> supplier, int precision, StatFormatter formatter) {}
    public static Entry of(Supplier<Number> supplier, int precision) {
        return new Entry(supplier, precision, null);
    }
    public static Entry of(Supplier<Number> supplier, int precision, StatFormatter formatter) {
        return new Entry(supplier, precision, formatter);
    }

    private final Supplier<Number> supplier;
    private final int precision;
    private final double scale;
    private StatFormatter formatter = null;

    public NumberSupplierElement(Entry entry, double scale, int precision, boolean format) {
        this(entry.supplier, scale, precision == -1 ? entry.precision : precision);
        formatter = format ? entry.formatter : null;
    }

    public NumberSupplierElement(Supplier<Number> supplier, double scale, int precision) {
        this.supplier = supplier;
        this.precision = precision == -1 ? 0 : precision;
        this.scale = scale;
    }

    @Override
    public String getString() {
        try {
            double num = supplier.get().doubleValue() * scale;
            if (Double.isNaN(num))
                return "-";
            if (formatter != null)
                return formatter.format((int)Math.round(num));
            if (precision == 0)
                return Integer.toString((int)num);

            return String.format("%."+precision+"f", num);
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
