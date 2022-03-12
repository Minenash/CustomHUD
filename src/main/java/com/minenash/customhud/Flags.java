package com.minenash.customhud;

public class Flags {

    public enum TextCase {UPPER, LOWER, TITLE}

    public TextCase textCase = null;
    public boolean smallCaps = false;
    public boolean noDelimiters = false;
    public int precision = -1;

    public boolean anyUsed() {
        return textCase != null || smallCaps || noDelimiters;
    }

    public Flags() {}

    public Flags(TextCase textCase, boolean smallCaps, int precision) {
        this.textCase = textCase;
        this.smallCaps = smallCaps;
        this.precision = precision;
    }

}
