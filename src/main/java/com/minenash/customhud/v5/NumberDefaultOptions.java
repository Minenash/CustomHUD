package com.minenash.customhud.v5;

import net.minecraft.stat.StatFormatter;

public record NumberDefaultOptions(int precision, StatFormatter formatter) {

    public static NumberDefaultOptions of(int precision) {
        return new NumberDefaultOptions(precision, null);
    }

    public static NumberDefaultOptions of(StatFormatter formatter) {
        return new NumberDefaultOptions(-1, formatter);
    }
}
