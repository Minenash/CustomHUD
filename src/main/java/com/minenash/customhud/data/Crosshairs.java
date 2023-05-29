package com.minenash.customhud.data;

public enum Crosshairs {
    NORMAL ("normal"),
    VANILLA ("vanilla"),
    DEBUG ("debug"),
    NONE ("none");

    final String name;
    Crosshairs(String name) {
        this.name = name;
    }

    static Crosshairs parse(String name) {
        for (Crosshairs c : Crosshairs.values())
            if (c.name.equals(name))
                return c;
        return null;
    }

    public String getName() {
        return name;
    }
}
