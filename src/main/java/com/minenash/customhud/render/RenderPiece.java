package com.minenash.customhud.render;

public class RenderPiece {

    public final Object element;
    public final int color;
    public final boolean shadow;
    public final int y;
    public int x;

    public RenderPiece(Object element, int x, int y, int color, boolean shadow) {
        this.element = element;
        this.x = x;
        this.y = y;
        this.color = color;
        this.shadow = shadow;
    }

}
