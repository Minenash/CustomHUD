package com.minenash.customhud.mc1_20.elements;

import com.minenash.customhud.core.elements.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;

public class ItemCountElement implements HudElement {

    private final Item item;

    public ItemCountElement(Item item) {
        this.item = item;
    }

    @Override
    public String getString() {
        return getNumber().toString();
    }

    @Override
    public Number getNumber() {
        return MinecraftClient.getInstance().player.getInventory().count(item);
    }

    @Override
    public boolean getBoolean() {
        return getNumber().intValue() > 0;
    }
}
