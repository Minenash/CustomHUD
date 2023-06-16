package com.minenash.customhud.core.elements;

import java.util.function.Supplier;

public class SupplierElements {

    public static class Bool implements HudElement {
        private final Supplier<Boolean> supplier;
        public Bool(Supplier<Boolean> supplier) { this.supplier = supplier; }

        @Override public String getString() { return sanitize(supplier, false) ? "true" : "false"; }
        @Override public Number getNumber() { return sanitize(supplier, false) ? 1 : 0; }
        @Override public boolean getBoolean() { return sanitize(supplier, false); }
    }

    public static class Special implements HudElement {
        public record Entry(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {}
        public static Entry of(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {
            return new Entry(stringSupplier, numberSupplier, booleanSupplier);
        }


        private final Entry entry;
        public Special(Entry entry) { this.entry = entry;}

        @Override public String getString() { return sanitize(entry.stringSupplier, "-"); }
        @Override public Number getNumber() { return sanitize(entry.numberSupplier, Double.NaN); }
        @Override public boolean getBoolean() { return sanitize(entry.booleanSupplier, false); }
    }

    public static class StrInt implements HudElement {
        private final Supplier<String> supplier;
        public StrInt(Supplier<String> supplier) {
            this.supplier = supplier;
        }

        @Override public String getString() { return sanitize(supplier, "-"); }
        @Override public boolean getBoolean() { return getNumber().doubleValue() > 0; }
        @Override public Number getNumber() {
            try {
                String value = supplier.get();
                return value == null ? Double.NaN : Integer.parseInt(value);
            }
            catch (Exception ignored) {
                return Double.NaN;
            }
        }
    }

    public static class Str implements HudElement {
        private final Supplier<String> supplier;
        public Str(Supplier<String> supplier) {
            this.supplier = supplier;
        }

        @Override public String getString() { return sanitize(supplier, "-"); }
        @Override public boolean getBoolean() { return getNumber().intValue() > 0; }
        @Override public Number getNumber() {
            try { return supplier.get().length(); }
            catch (Exception ignored) { return 0; }
        }
    }

    public static class Num implements HudElement {
        public record Entry(Supplier<Number> supplier, int precision) {}
        public static Entry of(Supplier<Number> supplier, int precision) {
            return new Entry(supplier, precision);
        }

        private final Supplier<Number> supplier;
        private final int precision;
        private final double scale;

        public Num(Entry entry, double scale, int precision) { this(entry.supplier, scale, precision); }
        public Num(Supplier<Number> supplier, double scale, int precision) {
            this.supplier = supplier;
            this.precision = precision;
            this.scale = scale;
        }

        @Override public Number getNumber() { return sanitize(supplier, Double.NaN); }
        @Override public boolean getBoolean() { return sanitize(supplier, Double.NaN).doubleValue() > 0; }
        @Override public String getString() {
            try {
                double num = supplier.get().doubleValue() * scale;
                if (precision == 0)
                    return Integer.toString((int)num);

                return String.format("%."+precision+"f", num);
            }
            catch (Exception _e) {
                return "-";
            }
        }
    }

}
