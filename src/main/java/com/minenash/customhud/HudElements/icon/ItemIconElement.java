package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
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

    public void render(MatrixStack matrix, int x, int y) {
        renderItemStack(x+shiftX, y+shiftY, stack);
    }
}
