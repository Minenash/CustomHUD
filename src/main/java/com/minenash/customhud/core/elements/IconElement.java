package com.minenash.customhud.core.elements;

import com.minenash.customhud.core.data.Flags;
import net.minecraft.client.gui.DrawContext;

public abstract class IconElement implements HudElement, FunctionalElement {

    protected final float scale;
    protected final int shiftX;
    protected final int shiftY;
    protected final boolean referenceCorner;

    protected IconElement(Flags flags) {
        scale = (float) flags.scale;
        shiftX = flags.iconShiftX;
        shiftY = flags.iconShiftY;
        referenceCorner = flags.iconReferenceCorner;
    }

    public abstract void render(DrawContext context, int x, int y, float profileScale);
    public abstract int getTextWidth();

    @Override
    public String getString() {
        return "\uFFFE";
    }
}
