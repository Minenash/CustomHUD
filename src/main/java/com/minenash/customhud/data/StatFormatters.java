package com.minenash.customhud.data;

import net.minecraft.stat.StatFormatter;

public class StatFormatters {

    public static final StatFormatter TICKS_HMS = ticks -> {
        if (ticks < 0) return "∞";
        int rawSeconds = ticks / 20;
        int seconds = rawSeconds % 60;
        int minutes = (rawSeconds / 60) % 60;
        int hours = (rawSeconds / 60 / 60);
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };
    public static final StatFormatter SEC_HMS = secs -> {
        int seconds = secs % 60;
        int minutes = (secs / 60) % 60;
        int hours = (secs / 60 / 60);
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };

    public static final StatFormatter MIL_HMS = millisecs -> {
        int secs = millisecs / 1000;
        int seconds = secs % 60;
        int minutes = (secs / 60) % 60;
        int hours = (secs / 60 / 60);
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };

    public static final StatFormatter MIL_HMS_H_REQUIRED = millisecs -> {
        int secs = millisecs / 1000;
        int seconds = secs % 60;
        int minutes = (secs / 60) % 60;
        int hours = (secs / 60 / 60);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    };

}
