package com.minenash.customhud.render;

import net.minecraft.util.Identifier;

public class RenderPiece {

    public final Object element;
    public final Object value;
    public final Identifier font;
    public final int color;
    public final int bgColor;
    public final boolean shadow;
    public int y;
    public int x;
    public int lineWith;
    public boolean shiftTextUpOrFitItemIcon;

    public RenderPiece(Object element, Object value, Identifier font, int x, int y, int color, int bgColor, boolean shadow, boolean shiftTextUpOrFitItemIcon) {
        this.element = element;
        this.value = value;
        this.font = font;
        this.x = x;
        this.y = y;
        this.color = color;
        this.bgColor = bgColor;
        this.shadow = shadow;
        this.shiftTextUpOrFitItemIcon = shiftTextUpOrFitItemIcon;
    }

    public RenderPiece adjust(int x, int y) {
        this.x += x;
        this.y += y;
        return this;
    }

}
