package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;

public class SpaceElement extends IconElement {

    private final Operation width;

    public SpaceElement(Operation width) {
        super(new Flags(), 0);
        this.width = width;
    }

    @Override
    public void render(DrawContext context, int x, int y) {}

    @Override
    public int getTextWidth() {
        return (int)width.getValue();
    }
}