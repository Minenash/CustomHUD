package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;

import java.util.UUID;

public abstract class IconElement extends FunctionalElement {

    protected final float scale;
    protected final int shiftX;
    protected final int shiftY;
    protected final int width;
    protected final float rotation;
    protected final boolean referenceCorner;
    protected UUID providerID = null;

    protected IconElement(Flags flags, double defaultWidth) {
        scale = (float) flags.scale;
        shiftX = flags.iconShiftX;
        shiftY = flags.iconShiftY;
        width = (int) Math.ceil( flags.iconWidth != -1 ? flags.iconWidth : defaultWidth * scale);
        rotation = flags.rotation;
        referenceCorner = flags.iconReferenceCorner;
    }

    public abstract void render(DrawContext context, RenderPiece piece);
    public int getTextWidth() {
        return width;
    };
    public UUID getProviderID() { return providerID; }

    @Override
    public String getString() {
        return "\uFFFE";
    }

    protected void rotate(Matrix3x2fStack matrices, float renderWidth, float renderHeight) {
        matrices.translate(renderWidth/2, renderHeight/2);
        matrices.rotate(this.rotation);
        matrices.translate(-renderWidth/2, -renderHeight/2);
    }

    public void renderItemStack(DrawContext context, int x, int y, ItemStack stack, boolean fitInLine) {
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(x + shiftX, y + shiftY - 2);
        int size = fitInLine ? 11 : 16;
        if (!referenceCorner)
            matrices.translate(0, -(size*scale-11)/2);
        matrices.scale(size/16F * scale, size/16F * scale);
        rotate(matrices, 16, 16);
        rotate(matrices, 16, 16);

        context.drawItem(stack, 0, 0);
        matrices.popMatrix();

    }

}
