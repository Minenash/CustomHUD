package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.supplier.NumberSupplierElement;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Flags;
import net.minecraft.stat.StatFormatter;

import java.util.TimerTask;

public class TimerElement implements HudElement {

    private final int precision;
    private final double scale;
    private final StatFormatter formatter;

    private final Operation interval;
    private final Operation end;

    private long lastMS = System.currentTimeMillis();
    private int value = 0;

    public TimerElement(Operation end, Operation interval, Flags flags) {
        precision = flags.precision == -1 ? 0 : flags.precision;
        scale = flags.scale;
        formatter = flags.hex ? NumberSupplierElement.HEX : null;

        this.interval = interval;
        this.end = end;
    }

    public int get() {
        long currentMs = System.currentTimeMillis();
        long diff = currentMs - lastMS;
        int inter = Math.max(1,(int)(interval.getValue()*1000));
        lastMS = currentMs - (diff % inter);
        value += diff / inter;
        if (value >= end.getValue())
            value = 0;
        return value;
    }

    @Override
    public String getString() {
        return NumberSupplierElement.formatString(get()*scale, formatter, precision);
    }

    @Override
    public Number getNumber() {
        return get();
    }

    @Override
    public boolean getBoolean() {
        return get() > 0;
    }
}
