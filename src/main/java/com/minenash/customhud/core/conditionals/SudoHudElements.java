package com.minenash.customhud.core.conditionals;

import com.minenash.customhud.core.elements.HudElement;

public class SudoHudElements {

    public record Num(Number number) implements HudElement {
        @Override public String getString() { return number.toString(); }
        @Override public Number getNumber() { return number; }
        @Override public boolean getBoolean() { return number.doubleValue() > 0; }
    }

    public record Bool(boolean bool) implements HudElement {
        @Override public String getString() { return Boolean.toString(bool); }
        @Override public Number getNumber() { return bool ? 1 : 0; }
        @Override public boolean getBoolean() { return bool; }
    }

}
