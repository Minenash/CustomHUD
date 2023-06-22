package com.minenash.customhud.core;

public class SupplierEntry<T> {

    public enum Type {BOOLEAN, STRING, STR_INT, INT, DEC, SPECIAL}

    public int enabledMask;
    public Type type;
    public T entry;

    public SupplierEntry(int enabledMask, Type type, T supplier) {
        this.enabledMask = enabledMask;
        this.type = type;
        this.entry = supplier;
    }

}
