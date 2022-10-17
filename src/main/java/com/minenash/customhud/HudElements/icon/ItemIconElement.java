package com.minenash.customhud.HudElements.icon;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIconElement extends IconElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    private final ItemStack stack;
    private final float scale;
    private final int width;

    public ItemIconElement(ItemStack stack, float scale, int width) {
        this.stack = stack;
        this.scale = scale;
        this.width = width;
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
    public int getTextureWidth() {
        return width;
    }

    public int render(MatrixStack matrix, int x, int y) {
        renderItemStack(x, y, stack, scale);
        return width;
    }
}
