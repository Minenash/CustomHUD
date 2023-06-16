package com.minenash.customhud.elements.icon;

import com.minenash.customhud.core.elements.IconElement;
import com.minenash.customhud.core.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIconElement extends IconElement {

    private final ItemStack stack;
    private final int width;

    public ItemIconElement(ItemStack stack, Flags flags) {
        super(flags);
        this.stack = stack;
        this.width = flags.iconWidth != -1 ? flags.iconWidth : (int)(11*scale);
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
    public int getTextWidth() {
        return width;
    }

    public void render(DrawContext context, int x, int y, float profileScale) {
        ItemRenderUtil.renderItemStack(x+shiftX, y+shiftY, profileScale, stack, referenceCorner, scale);
    }
}
