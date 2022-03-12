package com.minenash.customhud.HudElements;

public class StringElement implements HudElement {

    private final String str;

    public StringElement(String str) {
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
