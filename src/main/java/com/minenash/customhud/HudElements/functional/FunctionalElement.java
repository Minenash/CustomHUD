package com.minenash.customhud.HudElements.functional;

import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudTheme;

public class FunctionalElement implements HudElement {

    @Override public String getString() { return null; }
    @Override public Number getNumber() { return null; }
    @Override public boolean getBoolean() { return false; }

    public static class ChangeTheme extends FunctionalElement {
        public final HudTheme theme;
        public ChangeTheme(HudTheme theme) { this.theme = theme; }
    }

    public static class ChangeColor extends FunctionalElement {
        public final int color;
        public ChangeColor(int color) { this.color = color; }
    }

    public static class NewLine extends FunctionalElement {}



}
