package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.data.NumberFlags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ItemTagCountElement implements HudElement {

    private final Identifier tag;
    private final NumberFlags flags;

    public ItemTagCountElement(Identifier tag, Flags flags) {
        this.tag = tag;
        this.flags = NumberFlags.of(flags);
    }

    @Override
    public String getString() {
        return flags.formatString( getNumber().doubleValue() );
    }

    @Override
    public Number getNumber() {
        TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, tag);

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null)
            return 0;

        int count = 0;
        PlayerInventory inv = player.getInventory();
        for (var item : inv.getMainStacks())
            if (item.isIn(tagKey))
                count += item.getCount();

        ItemStack headStack = inv.getStack(EquipmentSlot.HEAD.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE));
        if (headStack.isIn(tagKey)) {
            count += headStack.getCount();
        }

        ItemStack chestStack = inv.getStack(EquipmentSlot.CHEST.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE));
        if (chestStack.isIn(tagKey)) {
            count += chestStack.getCount();
        }

        ItemStack legsStack = inv.getStack(EquipmentSlot.LEGS.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE));
        if (legsStack.isIn(tagKey)) {
            count += legsStack.getCount();
        }

        ItemStack feetStack = inv.getStack(EquipmentSlot.FEET.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE));
        if (feetStack.isIn(tagKey)) {
            count += feetStack.getCount();
        }

        ItemStack offhandStack = inv.getStack(PlayerInventory.OFF_HAND_SLOT);
        if (offhandStack.isIn(tagKey)) {
            count += offhandStack.getCount();
        }

        return count;
    }

    @Override
    public boolean getBoolean() {
        return getNumber().intValue() > 0;
    }
}
