package com.minenash.customhud.core;

public class SEntry<T> {

    public enum Type {BOOLEAN, STRING, STR_INT, INT, DEC, SPECIAL}

    public int enabledMask;
    public Type type;
    public T entry;

    public SEntry(int enabledMask, Type type, T supplier) {
        this.enabledMask = enabledMask;
        this.type = type;
        this.entry = supplier;
    }

}
