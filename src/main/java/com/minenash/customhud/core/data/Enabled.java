package com.minenash.customhud.core.data;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Enabled {
    public static final int                NONE = 0b000000000000000;
    public static final int        CLIENT_CHUNK = 0b000000000000010;
    public static final int        SERVER_CHUNK = 0b000000000000100;
    public static final int        SERVER_WORLD = 0b000000000001000;
    public static final int    LOCAL_DIFFICULTY = 0b000000000010000;
    public static final int               WORLD = 0b000000000100000;
    public static final int               SOUND = 0b000000001000000;
    public static final int        TARGET_BLOCK = 0b000000010000000;
    public static final int        TARGET_FLUID = 0b000000100000000;
    public static final int                TIME = 0b000001000000000;
    public static final int            VELOCITY = 0b000010000000000;
    public static final int                 CPU = 0b000100000000000;
    public static final int        UPDATE_STATS = 0b001000000000000;
    public static final int  CLICKS_PER_SECONDS = 0b010000000000000;
    public static final int PERFORMANCE_METRICS = 0b100000000000000;

    private int flags = NONE;

    public void add(int flag) {
        flags |= flag;
    }

    public boolean has(int flag) {
        return (flags & flag) == flag;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Enabled e && flags == e.flags;
    }

}
