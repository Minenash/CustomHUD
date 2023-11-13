package com.minenash.customhud.HudElements;

import com.minenash.customhud.data.Flags;

public class IntElement implements HudElement {
    private final float color;
    private final int precision;
    private final boolean hex;

    public IntElement(int color, Flags flags) {
        this.color = (float) (color * flags.scale);
        this.precision = flags.precision == -1 ? 0 : flags.precision;
        this.hex = flags.hex;
    }

    @Override
    public String getString() {
        return hex ? Integer.toHexString((int) color).toUpperCase()
                : precision == 0 ? Integer.toString((int) color)
                : String.format("%."+precision+"f", color);
    }

    @Override
    public Number getNumber() {
        return color;
    }

    @Override
    public boolean getBoolean() {
        return color > 0;
    }

}
