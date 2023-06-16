package com.minenash.customhud.elements.icon;

import com.minenash.customhud.core.data.Flags;
import net.minecraft.client.gui.DrawContext;

public class SpaceElement extends ItemRenderUtil {

    private final int width;

    public SpaceElement(int width) {
        super(new Flags());
        this.width = width;
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {}

    @Override
    public int getTextWidth() {
        return width;
    }
}