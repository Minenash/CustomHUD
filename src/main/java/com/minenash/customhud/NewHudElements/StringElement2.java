package com.minenash.customhud.NewHudElements;

public class StringElement2 implements HudElement2 {

    private final String str;

    public StringElement2(String str) {
        this.str = str;
    }

    @Override
    public String getString() {
        return str;
    }

    @Override
    public Number getNumber() {
        return str.length();
    }

    @Override
    public boolean getBoolean() {
        return true;
    }
}
