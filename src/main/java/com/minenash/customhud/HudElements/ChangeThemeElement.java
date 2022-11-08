package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudTheme;

public class ChangeThemeElement implements HudElement {

    public final HudTheme theme;

    public ChangeThemeElement(HudTheme theme) {
        this.theme = theme;
    }


    @Override
    public String getString() {
        return null;
    }

    @Override
    public Number getNumber() {
        return null;
    }

    @Override
    public boolean getBoolean() {
        return false;
    }
}
