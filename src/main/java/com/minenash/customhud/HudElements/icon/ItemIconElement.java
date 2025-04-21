package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.CustomHudRenderer3;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ItemIconElement extends IconElement {

    private final ItemStack stack;
    private final boolean crosshair;

    public ItemIconElement(ItemStack stack, boolean crosshair, Flags flags) {
        super(flags, 11);
        this.stack = stack;
        this.crosshair = crosshair;
    }

    @Override
    public Number getNumber() {
        return Item.getRawId(stack.getItem());
    }

    @Override
    public boolean getBoolean() {
        return stack.isEmpty();
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        if (crosshair) RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );
        renderItemStack(context, piece.x, piece.y, stack, piece.shiftTextUpOrFitItemIcon);
        RenderSystem.defaultBlendFunc();
    }

    @Override
    public int getTextWidth() {
        return CustomHudRenderer3.theme.fitItemIconsToLine ? width : (int) (width * 16F/11);
    }
}
