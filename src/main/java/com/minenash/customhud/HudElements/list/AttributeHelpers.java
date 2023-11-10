package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.mixin.accessors.AttributeContainerAccessor;
import com.minenash.customhud.mixin.accessors.DefaultAttributeContainerAccessor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;
import static net.minecraft.item.ItemStack.DISPLAY_KEY;
import static net.minecraft.item.ItemStack.LORE_KEY;

public class AttributeHelpers {

    public static final Function<String, EntityAttribute> ENTITY_ATTR_READER = (src) -> Registries.ATTRIBUTE.get(Identifier.tryParse(src));
    public static final Function<String, Integer> SLOT_READER = (src) -> {
        if (src.isBlank())
            return null;
        try {
            return ItemSlotArgumentType.itemSlot().parse(new StringReader(switch (src) {
                case "head", "chest", "legs", "feet" -> "armor." + src;
                case "mainhand", "offhand" -> "weapon." + src;
                case "main", "off" -> "weapon." + src + "hand";
                default -> {
                    if (src.charAt(0) == 'h' && src.charAt(1) != 'o') yield "hotbar." + src.substring(1);
                    if (src.charAt(0) == 'i' && src.charAt(1) != 'n') yield "inventory." + src.substring(1);
                    yield src;
                }
            }));
        } catch (CommandSyntaxException e) {
            return null;
        }
    };


    public static Entity getFullEntity(Entity entity) {
        return CLIENT.getServer() == null || entity == null? entity :
                CLIENT.getServer().getWorld(entity.getWorld().getRegistryKey()).getEntity(entity.getUuid());
    }
    public static EntityAttributeInstance getEntityAttr(Entity entity, EntityAttribute attribute) {
        Entity e = getFullEntity(entity);
        if (!(e instanceof LivingEntity le)) return null;
        return le.getAttributeInstance(attribute);
    }

    public static List<EntityAttributeInstance> getEntityAttributes(Entity entity) {
        entity = getFullEntity(entity);
        if (!(entity instanceof LivingEntity le) ) return Collections.EMPTY_LIST;
        AttributeContainerAccessor container = (AttributeContainerAccessor) le.getAttributes();
        Map<EntityAttribute, EntityAttributeInstance> instances = new HashMap<>(((DefaultAttributeContainerAccessor)container.getFallback()).getInstances());
        instances.putAll(container.getCustom());
        return (entity.getWorld().isClient ?
                instances.values().stream().filter(a -> a.getAttribute().isTracked()) : instances.values().stream())
                .sorted(Comparator.comparing(a -> I18n.translate(a.getAttribute().getTranslationKey()))).toList();
    }

    public record ItemAttribute(EntityAttribute attribute, EntityAttributeModifier modifier, String slot) {}
    public static List<ItemAttribute> getItemStackAttributes(ItemStack stack) {
        if (!stack.hasNbt() || !stack.getNbt().contains("AttributeModifiers", 9)) {
            List<ItemAttribute> attributes = new ArrayList<>();
            for (EquipmentSlot slot : EquipmentSlot.values())
                for (var entry : stack.getItem().getAttributeModifiers(slot).entries())
                    attributes.add( new ItemAttribute(entry.getKey(), entry.getValue(), slot.getName()) );
            return attributes;
        }

        List<ItemAttribute> attributes = new ArrayList<>();
        NbtList nbtList = stack.getNbt().getList("AttributeModifiers", 10);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);

            EntityAttribute attribute = Registries.ATTRIBUTE.get(Identifier.tryParse(nbtCompound.getString("AttributeName")));
            if (attribute == null) continue;
            EntityAttributeModifier modifier = EntityAttributeModifier.fromNbt(nbtCompound);
            if (modifier != null
                    && modifier.getId().getLeastSignificantBits() != 0L
                    && modifier.getId().getMostSignificantBits() != 0L) {
                String slot = nbtCompound.getString("Slot");
                if (slot.isEmpty()) slot = "all";
                attributes.add( new ItemAttribute(attribute, modifier, slot) );
            }
        }

        return attributes;
    }

    public static List<String> getLore(ItemStack stack) {
        List<String> lines = new ArrayList<>();
        if (stack.hasNbt()) {
            if (stack.getNbt().contains(DISPLAY_KEY, NbtElement.COMPOUND_TYPE)) {
                NbtCompound nbtCompound = stack.getNbt().getCompound(DISPLAY_KEY);
                if (nbtCompound.getType(LORE_KEY) == NbtElement.LIST_TYPE) {
                    NbtList nbtList = nbtCompound.getList(LORE_KEY, NbtElement.STRING_TYPE);
                    for (int j = 0; j < nbtList.size(); ++j) {
                        try {
                            MutableText mutableText2 = Text.Serializer.fromJson(nbtList.getString(j));
                            if (mutableText2 == null) continue;
                            lines.add(mutableText2.getString());
                        }
                        catch (Exception ignored) {}
                    }
                }
            }
        }
        return lines;
    }

    public static List<ItemStack> compactItems(List<ItemStack> stacks) {
        List<ItemStack> compact = new ArrayList<>();
        outer:
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            for (ItemStack cStack : compact) {
                if (ItemStack.canCombine(stack, cStack)) {
                    cStack.setCount(cStack.getCount() + stack.getCount());
                    continue outer;
                }
            }
            compact.add(stack.copy());
        }
        return compact;
    }

    public static Scoreboard scoreboard() {
        return CLIENT.getServer() != null ? CLIENT.getServer().getScoreboard() : CLIENT.world.getScoreboard();
    }
    public static boolean scoreboardPlayer(String player) {
        return  null != (CLIENT.getServer() != null ? CLIENT.getServer().getPlayerManager().getPlayer(player) : CLIENT.getNetworkHandler().getPlayerListEntry(player));
    }


}
