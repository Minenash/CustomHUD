package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.joml.Quaternionf;

import java.util.List;

public abstract class IconElement extends FunctionalElement {

    protected final float scale;
    protected final int shiftX;
    protected final int shiftY;
    protected final int width;
    protected final Quaternionf rotation;
    protected final Quaternionf rotationInverse;
    protected final boolean referenceCorner;

    protected IconElement(Flags flags, double defaultWidth) {
        scale = (float) flags.scale;
        shiftX = flags.iconShiftX;
        shiftY = flags.iconShiftY;
        width = (int) Math.ceil( flags.iconWidth != -1 ? flags.iconWidth : defaultWidth * scale);
        rotation = new Quaternionf().rotationZ(flags.rotation);
        rotationInverse = new Quaternionf().rotationZ(-flags.rotation);
        referenceCorner = flags.iconReferenceCorner;
    }

    public abstract void render(DrawContext context, int x, int y, float profileScale);
    public int getTextWidth() {
        return width;
    };

    @Override
    public String getString() {
        return "\uFFFE";
    }

    protected void rotate(MatrixStack matrices, float renderWidth, float renderHeight) {
        matrices.translate(renderWidth/2, renderHeight/2, 0);
        matrices.multiply(rotation);
        matrices.translate(-renderWidth/2, -renderHeight/2, 0);
    }

    public void renderItemStack(DrawContext context, int x, int y, ItemStack stack) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x + shiftX, y + shiftY - 2, 0);
        if (!referenceCorner)
            matrices.translate(0, -(11*scale-11)/2, 0);
        matrices.scale(11/16F * scale, 11/16F * scale, 1);
        rotate(matrices, 16, 16);

        context.drawItem(stack, 0, 0);
        matrices.pop();

    }

    public void setList(List<?> values) {}

}
