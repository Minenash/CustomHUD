package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;

public class SpaceElement extends IconElement {

    private final int width;

    public SpaceElement(int width) {
        super(new Flags(), 0);
        this.width = width;
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {}

    @Override
    public int getTextWidth() {
        return width;
    }
}