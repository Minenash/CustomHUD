package com.minenash.customhud.core.render;

public class RenderPiece {

    public static class Foreground extends RenderPiece {
        public final Object element;
        public final String font;
        public final int color;
        public final boolean shadow;
        public final int y;
        public int x;

        public Foreground(Object element, int x, int y, int color, String font, boolean shadow) {
            this.element = element;
            this.x = x;
            this.y = y;
            this.color = color;
            this.font = font;
            this.shadow = shadow;
        }
    }

    public static class Background extends RenderPiece {
        public final int x1, y1, x2, y2, color;

        public Background(int x1, int y1, int x2, int y2, int color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.color = color;
        }
    }

}
