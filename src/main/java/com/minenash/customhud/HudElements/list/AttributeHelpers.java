package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.data.ProfileOption;
import com.minenash.customhud.mixin.accessors.AttributeContainerAccessor;
import com.minenash.customhud.mixin.accessors.BlockPredicatesCheckerAccessor;
import com.minenash.customhud.mixin.accessors.DefaultAttributeContainerAccessor;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.block.Block;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.argument.ItemSlotArgumentType;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static com.minenash.customhud.CustomHud.CLIENT;

public class AttributeHelpers {

    public static final Function<String, EntityAttribute> ENTITY_ATTR_READER = (src) -> Registries.ATTRIBUTE.get(Identifier.tryParse(src));
    public static final Function<String, ResourcePackProfile> DATA_PACK_READER = (src) ->
        CLIENT.getServer() == null ? null : CLIENT.getServer().getDataPackManager().getProfile(src);

    private static final Pattern TIMING_PERIOD_TO_SPECIAL = Pattern.compile("(?<!\\\\)\\.");
    public static final Function<String, String> PROFILER_TIMING_READER = (src) -> {
        src = TIMING_PERIOD_TO_SPECIAL.matcher(src).replaceAll("\u001e");
        if (!src.startsWith("root\u001e"))
            src = "root\u001e" + src;
        return src;
    };

    public static final Function<String, Integer> SLOT_READER = (src) -> {
        if (src.isBlank())
            return null;
        try {
            return ItemSlotArgumentType.itemSlot().parse(new StringReader(switch (src) {
                case "head", "chest", "legs", "feet" -> "armor." + src;
                case "mainhand", "offhand" -> "weapon." + src;
                case "main", "off" -> "weapon." + src + "hand";
                default -> {
                    if (src.length() < 2) yield src;
                    if (src.charAt(0) == 'h' && src.charAt(1) != 'o') yield "hotbar." + src.substring(1);
                    if (src.charAt(0) == 'i' && src.charAt(1) != 'n') yield "inventory." + src.substring(1);
                    yield src;
                }
            }));
        } catch (CommandSyntaxException e) {
            return null;
        }
    };

    public record ReceivedPower(Direction direction, int power, int strongPower) {}

    public static PlayerListEntry getPlayer(String src) {
        PlayerListEntry p = CLIENT.getNetworkHandler().getPlayerListEntry(src);
        if (p != null)
            return p;
        try {
            return CLIENT.getNetworkHandler().getPlayerListEntry(UUID.fromString(src));
        }
        catch (Exception ignored) {}
        return null;
    }

    public static Entity getFullEntity(Entity entity) {
        return CLIENT.getServer() == null || entity == null? entity :
                CLIENT.getServer().getWorld(entity.getWorld().getRegistryKey()).getEntity(entity.getUuid());
    }
    public static EntityAttributeInstance getEntityAttr(Entity entity, EntityAttribute attribute) {
        Entity e = getFullEntity(entity);
        if (!(e instanceof LivingEntity le)) return null;
        return le.getAttributeInstance(Registries.ATTRIBUTE.getEntry(attribute));
    }

    public static List<?> getEntityAttributes(Entity entity) {
        entity = getFullEntity(entity);
        if (!(entity instanceof LivingEntity le) ) return Collections.EMPTY_LIST;
        AttributeContainerAccessor container = (AttributeContainerAccessor) le.getAttributes();
        Map<EntityAttribute, EntityAttributeInstance> instances = new HashMap<>(((DefaultAttributeContainerAccessor)container.getFallback()).getInstances());
        instances.putAll(container.getCustom());
        return Arrays.asList( (entity.getWorld().isClient ?
                instances.values().stream().filter(a -> a.getAttribute().value().isTracked()) : instances.values().stream())
                .sorted(Comparator.comparing(a -> I18n.translate(a.getAttribute().value().getTranslationKey()))).toArray() );
    }

    public record ItemAttribute(EntityAttribute attribute, EntityAttributeModifier modifier, String slot) {}
    public static List<ItemAttribute> getItemStackAttributes(ItemStack stack) {
        List<ItemAttribute> attributes = new ArrayList<>();

        AttributeModifiersComponent component = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (component != null)
            for (var entry : component.modifiers())
                attributes.add( new ItemAttribute(entry.attribute().value(), entry.modifier(), entry.slot().asString()) );
        return attributes;
    }

    public static List<Text> getLore(ItemStack stack) {
        LoreComponent component = stack.get(DataComponentTypes.LORE);
        return component != null ? component.lines() : new ArrayList<>();
    }

    public static List<Block> getCanX(ItemStack stack, ComponentType<BlockPredicatesChecker> type) {
        BlockPredicatesChecker component = stack.get(type);
        Set<Block> blocks = new HashSet<>();
        if (component != null) {
            for (var e : ((BlockPredicatesCheckerAccessor) component).getPredicates()) {
                if (e.blocks().isPresent())
                    for (var ee : e.blocks().get())
                        blocks.add(ee.value());
            }
        }
        return new ArrayList<>(blocks);
    }

    public static List<ItemStack> compactItems(List<ItemStack> stacks) {
        List<ItemStack> compact = new ArrayList<>();
        outer:
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            for (ItemStack cStack : compact) {
                if (ItemStack.areItemsAndComponentsEqual(stack, cStack)) {
                    cStack.setCount(cStack.getCount() + stack.getCount());
                    continue outer;
                }
            }
            compact.add(stack.copy());
        }
        return compact;
    }

    public static List<ItemStack> getItemItems(ItemStack stack, boolean returnStack) {
        if (stack.isEmpty())
            return returnStack ? Collections.singletonList(stack) : Collections.EMPTY_LIST;

        Iterator<ItemStack> iter = null;
        get_iter:
        {
            var bundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (bundle != null) {
                iter = bundle.iterate().iterator();
                break get_iter;
            }
            var container = stack.get(DataComponentTypes.CONTAINER);
            if (container != null) {
                iter = container.iterateNonEmpty().iterator();
                break get_iter;
            }
//            var blockEntity = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
//            if (blockEntity != null) {
//                blockEntity.
//            }
        }

        if (iter == null || !iter.hasNext())
            return returnStack ? Collections.singletonList(stack) : Collections.EMPTY_LIST;

        int count = stack.getCount();
        List<ItemStack> items = new ArrayList<>();
        while (iter.hasNext()) {
            ItemStack item = iter.next();
            items.add(item.copyWithCount(item.getCount() * count));
        }
        return items;

//        NbtCompound nbt = stack.getNbt();
//        if (stack.isEmpty() || nbt == null)
//            return returnStack ? Collections.singletonList(stack) : Collections.EMPTY_LIST;
//        if (nbt.contains("Items", NbtElement.LIST_TYPE))
//            return getItemItemsInternal(stack, nbt.getList("Items", NbtElement.COMPOUND_TYPE), returnStack );
//        if (!nbt.contains("BlockEntityTag"))
//            return returnStack ? Collections.singletonList(stack) : Collections.EMPTY_LIST;
//        nbt = nbt.getCompound("BlockEntityTag");
//        if (!nbt.contains("Items", NbtElement.LIST_TYPE))
//            return returnStack ? Collections.singletonList(stack) : Collections.EMPTY_LIST;
//        return getItemItemsInternal(stack, nbt.getList("Items", NbtElement.COMPOUND_TYPE), returnStack );
    }
//    private static List<ItemStack> getItemItemsInternal(ItemStack stack, NbtList list, boolean returnStack) {
//        List<ItemStack> items = new ArrayList<>(list.size());
//
//        for(int i = 0; i < list.size(); ++i) {
//            List<ItemStack> inner = getItemItems(ItemStack.fromNbt(list.getCompound(i)), true);
//            for (ItemStack is : inner)
//                is.setCount(is.getCount() * stack.getCount());
//            items.addAll(inner);
//        }
//
//        if (items.isEmpty() && returnStack)
//            return Collections.singletonList(stack);
//
//        return items;
//    }

    public static Scoreboard scoreboard() {
        return CLIENT.getServer() != null ? CLIENT.getServer().getScoreboard() : CLIENT.world.getScoreboard();
    }
    public static boolean entryOnline(String entry) {
        if (null != (CLIENT.getServer() != null ? CLIENT.getServer().getPlayerManager().getPlayer(entry) : CLIENT.getNetworkHandler().getPlayerListEntry(entry)))
            return true;
        if (ComplexData.serverWorld == null)
            return false;
        try {
            return ComplexData.serverWorld.entityManager.has(UUID.fromString(entry));
        }
        catch (Exception ignored) {}
        return false;
    }

    public static List<?> bossbars(boolean all) {
        if (CLIENT.getServer() == null)
            return Arrays.asList(CLIENT.inGameHud.getBossBarHud().bossBars.entrySet().toArray());

        List<BossBar> serverBossbars = new ArrayList<>();
        serverBossbars.addAll(CLIENT.getServer().getBossBarManager().commandBossBars.values());
        serverBossbars.addAll(ComplexData.bossbars.values());

        if (all)
            return serverBossbars;

        Set<UUID> client = CLIENT.inGameHud.getBossBarHud().bossBars.keySet();
        return Arrays.asList( serverBossbars.stream().filter(bar -> client.contains(bar.getUuid())).toArray() );
    }

    public static BossBar getBossBar(String input) {
        boolean client = CLIENT.getServer() == null;
        try {
            UUID uuid = UUID.fromString(input);
            if (client)
                return CLIENT.inGameHud.getBossBarHud().bossBars.get(uuid);
            for (BossBar bar : CLIENT.getServer().getBossBarManager().commandBossBars.values())
                if (bar.getUuid() == uuid)
                    return bar;
            BossBar bb = ComplexData.bossbars.get(uuid);
            if (bb != null)
                return bb;
        }
        catch (Exception ignored) {}

        if (client) {
            for (BossBar bar : CLIENT.inGameHud.getBossBarHud().bossBars.values())
                if (bar.getName().getString().equalsIgnoreCase(input))
                    return bar;
        }
        else {
            BossBar bar = CLIENT.getServer().getBossBarManager().get(Identifier.tryParse(input));
            if (bar != null)
                return bar;
            for (BossBar bar2 : CLIENT.getServer().getBossBarManager().commandBossBars.values())
                if (bar2.getName().getString().equalsIgnoreCase(input))
                    return bar2;
            for (BossBar bar2 : ComplexData.bossbars.values())
                if (bar2.getName().getString().equalsIgnoreCase(input))
                    return bar2;
        }
        return null;
    }

    public static int getBossBarColor(BossBar bar) {
        return switch (bar.getColor()) {
            case PINK -> 0xEC00B8;
            case BLUE -> 0x00B7EC;
            case RED -> 0xEC3500;
            case GREEN -> 0x1DEC00;
            case YELLOW -> 0xE9EC00;
            case PURPLE -> 0x7B00EC;
            case WHITE -> 0xECECEC;
        };
    }

    public static double getRelativeYaw(Vec3d player, Vec3d other) {
        return MathHelper.wrapDegrees(CLIENT.player.getYaw() - Math.toDegrees( MathHelper.atan2(-(other.getX() - player.getX()), other.getZ() - player.getZ()) ));
    }
    public static double getRelativePitch(Vec3d player, Vec3d other) {
        double xDist = other.getX() - player.getX();
        double zDist = other.getZ() - player.getZ();
        return MathHelper.wrapDegrees(CLIENT.player.getPitch() + Math.toDegrees( MathHelper.atan2(other.getY() - player.getY(), Math.sqrt(xDist*xDist + zDist*zDist ) )));
    }

    public static boolean isFabricRP(ResourcePackProfile pack) {
        TextContent content = pack.getInfo().title().getContent();
        return pack.getId().equals("fabric") || content instanceof TranslatableTextContent ttc && (ttc.getKey().equals("pack.name.fabricMod") || ttc.getKey().equals("pack.name.fabricMods"));
    }

}
