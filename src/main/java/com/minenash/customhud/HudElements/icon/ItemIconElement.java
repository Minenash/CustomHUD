package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class ItemIconElement extends IconElement {

    private final ItemStack stack;

    public ItemIconElement(ItemStack stack, Flags flags) {
        super(flags, 11);
        this.stack = stack;
    }

    @Override
    public Number getNumber() {
        return Item.getRawId(stack.getItem());
    }

    @Override
    public boolean getBoolean() {
        return stack.isEmpty();
    }

    public void render(DrawContext context, int x, int y, float profileScale) {
        renderItemStack(context, x, y, stack);
    }
}
