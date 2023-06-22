package com.minenash.customhud.core.elements;

import com.minenash.customhud.core.data.HudTheme;

public interface FunctionalElement {

    record ChangeTheme(HudTheme theme) implements HudElement, FunctionalElement {}
    record ChangeColor(int color) implements HudElement, FunctionalElement {}
    class NewLine implements HudElement, FunctionalElement{}

}
