package com.minenash.customhud;

public class Flags {

    public boolean uppercase = false;
    public boolean lowercase = false;
    public boolean smallcaps = false;
    public int precision = -1;

    public Flags() {}

    public Flags(boolean uppercase, boolean lowercase, boolean smallcaps, int precision) {
        this.uppercase = uppercase;
        this.lowercase = lowercase;
        this.smallcaps = smallcaps;
        this.precision = precision;
    }

}
