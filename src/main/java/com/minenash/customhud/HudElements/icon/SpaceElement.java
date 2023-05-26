package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.util.math.MatrixStack;

public class SpaceElement extends IconElement {

    private final int width;

    public SpaceElement(int width) {
        super(new Flags());
        this.width = width;
    }

    @Override
    public void render(MatrixStack stack, int x, int y, float profileScale) {}

    @Override
    public int getTextWidth() {
        return width;
    }
}