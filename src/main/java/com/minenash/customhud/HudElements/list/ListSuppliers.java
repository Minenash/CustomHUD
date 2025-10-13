package com.minenash.customhud.HudElements.list;

import com.google.common.collect.Lists;
import com.minenash.customhud.HudElements.list.ListProvider.EventListProvider;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.minenash.customhud.complex.SubtitleTracker;
import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.list.AttributeHelpers.*;
import static net.minecraft.entity.player.PlayerInventory.OFF_HAND_SLOT;

@SuppressWarnings("DataFlowIssue")
public class ListSuppliers {
    
    private static final int HEAD_SLOT = EquipmentSlot.HEAD.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE);
    private static final int CHEST_SLOT = EquipmentSlot.CHEST.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE);
    private static final int LEGS_SLOT = EquipmentSlot.LEGS.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE);
    private static final int FEET_SLOT = EquipmentSlot.FEET.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE);
    
    public static final Direction[] DIRS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP, Direction.DOWN};
    public static final Comparator<PlayerListEntry> ENTRY_ORDERING =
            Comparator.comparingInt((PlayerListEntry entry) -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
                    .thenComparing((entry) -> Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, ""))
                    .thenComparing((entry) -> entry.getProfile().name(), String::compareToIgnoreCase);


    public static final List<String> IGNORE_MODS = List.of("minecraft", "fabricloader", "java");
    public static final Comparator<?> MOD_ORDERING = Comparator.comparing(mod -> ((Mod) mod).getTranslatedName().toLowerCase(Locale.ROOT));
    public static final Predicate<?> MOD_PREDICATE = (mod) -> !(((Mod) mod).isHidden() || ((Mod) mod).getBadges().contains(Mod.Badge.LIBRARY) || ((Mod) mod).getBadges().contains(Mod.Badge.MINECRAFT));
    public static final Predicate<?> ALL_ROOT_MODS_PREDICATE = (mod) -> !(((Mod) mod).isHidden() || IGNORE_MODS.contains(((Mod) mod).getId()));
    public static final Comparator<StatusEffectInstance> EFFECT_ORDERING = Comparator.comparingInt(e -> e.getDuration() == -1 ? Integer.MAX_VALUE : e.getDuration());
//    public static final Comparator<StatusEffectInstance> ALL_EFFECT_ORDERING = Comparator.comparingLong(e -> (3000000000L * e.getEffectType().getCategory().ordinal()) + (e.getDuration() == -1 ? Integer.MAX_VALUE : e.getDuration()));

    public static final ListProvider
    STATUS_EFFECTS = () -> Arrays.asList(CLIENT.player.getStatusEffects().stream().sorted(EFFECT_ORDERING).toArray()),
    STATUS_EFFECTS_POSITIVE = () -> Arrays.asList(CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().value().getCategory() == StatusEffectCategory.BENEFICIAL).sorted(EFFECT_ORDERING).toArray()),
    STATUS_EFFECTS_NEGATIVE = () -> Arrays.asList(CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().value().getCategory() == StatusEffectCategory.HARMFUL).sorted(EFFECT_ORDERING).toArray()),
    STATUS_EFFECTS_NEUTRAL = () -> Arrays.asList(CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().value().getCategory() == StatusEffectCategory.NEUTRAL).sorted(EFFECT_ORDERING).toArray()),

    ONLINE_PLAYERS = () -> Arrays.asList(CLIENT.getNetworkHandler().getPlayerList().stream().sorted(ENTRY_ORDERING).toArray()),
    SUBTITLES = () -> SubtitleTracker.INSTANCE.entries,

    TARGET_BLOCK_STATES = () -> ComplexData.targetBlock == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetBlock.getEntries().entrySet().toArray()),
    TARGET_BLOCK_TAGS = () -> ComplexData.targetBlock == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetBlock.streamTags().toArray()),
    TARGET_FLUID_STATES = () -> ComplexData.targetFluid == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetFluid.getEntries().entrySet().toArray()),
    TARGET_FLUID_TAGS = () -> ComplexData.targetFluid == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetFluid.streamTags().toArray()),

    TARGET_BLOCK_POWERS = () -> {
        if (ComplexData.targetBlockPos == null) return Collections.EMPTY_LIST;
        List<ReceivedPower> powers = new ArrayList<>(6);
        for (Direction d : DIRS) {
            BlockPos pos = ComplexData.targetBlockPos.offset(d);
            powers.add(new ReceivedPower(d, CLIENT.world.getEmittedRedstonePower(pos, d), CLIENT.world.getStrongRedstonePower(pos, d)));
        }
        return powers;
    },

    PLAYER_ATTRIBUTES = () -> getEntityAttributes(CLIENT.player),
    TARGET_ENTITY_ATTRIBUTES = () -> ComplexData.targetEntity == null ? Collections.EMPTY_LIST : getEntityAttributes(ComplexData.targetEntity),
    HOOKED_ENTITY_ATTRIBUTES = () -> hooked() == null ? Collections.EMPTY_LIST : getEntityAttributes(hooked()),
    TEAMS = () -> Arrays.asList(CLIENT.world.getScoreboard().getTeams().toArray()),
    TARGET_VILLAGER_OFFERS = () -> ComplexData.villagerOffers,

    ITEMS = () -> AttributeHelpers.compactItems(CLIENT.player.getInventory().getMainStacks()),
    INV_ITEMS = () -> CLIENT.player.getInventory().getMainStacks().subList(9, CLIENT.player.getInventory().getMainStacks().size()),
    ARMOR_ITEMS = () -> {
        PlayerInventory inv = CLIENT.player.getInventory();
        return List.of(
                inv.getStack(HEAD_SLOT),
                inv.getStack(CHEST_SLOT),
                inv.getStack(LEGS_SLOT),
                inv.getStack(FEET_SLOT)
        );
    },
    HOTBAR_ITEMS = () -> CLIENT.player.getInventory().getMainStacks().subList(0, 9),
    ALL_ITEMS = () -> {
        PlayerInventory inv = CLIENT.player.getInventory();
        List<ItemStack> items = new ArrayList<>(inv.getMainStacks());
        items.add(inv.getStack(HEAD_SLOT));
        items.add(inv.getStack(CHEST_SLOT));
        items.add(inv.getStack(LEGS_SLOT));
        items.add(inv.getStack(FEET_SLOT));
        items.add(inv.getStack(OFF_HAND_SLOT));
        return items;
    },
    EQUIPPED_ITEMS = () -> {
        PlayerInventory inv = CLIENT.player.getInventory();
        List<ItemStack> items = new ArrayList<>(5);
        items.add(inv.getStack(HEAD_SLOT));
        items.add(inv.getStack(CHEST_SLOT));
        items.add(inv.getStack(LEGS_SLOT));
        items.add(inv.getStack(FEET_SLOT));
        items.add(inv.getSelectedStack());
        items.add(inv.getStack(OFF_HAND_SLOT));
        return items;
    },
    ITEMS_UNPACKED = () -> {
        List<ItemStack> items = new ArrayList<>(36);
        for (ItemStack stack : CLIENT.player.getInventory().getMainStacks()) {
            List<ItemStack> innerItems = getItemItems(stack, true);
            if (innerItems.isEmpty())
                items.add(stack);
            else
                items.addAll(innerItems);
        }
        return AttributeHelpers.compactItems(items);
    },

    SCOREBOARD_OBJECTIVES = () -> Arrays.asList(scoreboard().getObjectives().toArray()),
            PLAYER_SCOREBOARD_SCORES = () -> Arrays.asList(scoreboard().getScores(CLIENT.getGameProfile().name()).scores.entrySet().toArray()),

    BOSSBARS = () -> bossbars(false),
    ALL_BOSSBARS = () -> bossbars(true),

    RECORDS = () -> MusicAndRecordTracker.records,

    MODS = () -> Arrays.asList(ModMenu.ROOT_MODS.values().stream().filter((Predicate<? super Mod>) MOD_PREDICATE).sorted((Comparator<? super Mod>) MOD_ORDERING).toArray()),
    ALL_ROOT_MODS = () -> Arrays.asList(ModMenu.ROOT_MODS.values().stream().filter((Predicate<? super Mod>) ALL_ROOT_MODS_PREDICATE).sorted((Comparator<? super Mod>) MOD_ORDERING).toArray()),
    ALL_MODS = () -> Arrays.asList(ModMenu.MODS.values().stream().sorted((Comparator<? super Mod>) MOD_ORDERING).toArray()),

    RESOURCE_PACKS = () -> {
        List<ResourcePackProfile> packs = new ArrayList<>(CLIENT.getResourcePackManager().getEnabledProfiles());
        packs.removeIf(pack -> AttributeHelpers.isFabricRP(pack) || pack.getId().equals("vanilla"));
        Collections.reverse(packs);
        return packs;
    },
    DISABLED_RESOURCE_PACKS = () -> {
        List<ResourcePackProfile> profiles = Lists.newArrayList(CLIENT.getResourcePackManager().getProfiles());
        profiles.removeAll(CLIENT.getResourcePackManager().getEnabledProfiles());
        Collections.reverse(profiles);
        return profiles;
    },
    DATA_PACKS = () -> CLIENT.getServer() == null ? Collections.EMPTY_LIST : Arrays.asList(CLIENT.getServer().getDataPackManager().getEnabledProfiles().toArray()),
    DISABLED_DATA_PACKS = () -> {
        if (CLIENT.getServer() == null) return Collections.EMPTY_LIST;

        ResourcePackManager manager = CLIENT.getServer().getDataPackManager();
        List<ResourcePackProfile> profiles = Lists.newArrayList(manager.getProfiles());
        profiles.removeAll(manager.getEnabledProfiles());
        return profiles;
    },

    CHAT_MESSAGES = () -> CLIENT.inGameHud.getChatHud().messages,

    PROFILER_TIMINGS = () -> ComplexData.rootEntries;

    public static final Function<SubtitlesHud.SubtitleEntry, List<?>> SUBTITLE_SOUNDS = (entry) -> new ArrayList<>(entry.sounds);
    public static final Function<ComplexData.ProfilerTimingWithPath, List<?>> TIMINGS_SUB_ENTRIES = (timing) -> timing == null ? Collections.EMPTY_LIST : timing.entries();

    public static final Function<EntityAttributeInstance, List<?>> ATTRIBUTE_MODIFIERS = (attr) -> new ArrayList<>(attr.getModifiers());
    public static final Function<Team, List<?>> TEAM_MEMBERS = (team) -> Arrays.asList(team.getPlayerList().toArray());
    public static final Function<Team, List<?>> TEAM_PLAYERS = (team) -> CLIENT.getNetworkHandler().getPlayerList().stream().filter(p -> p.getScoreboardTeam() == team).sorted(ENTRY_ORDERING).toList();

    public static final Function<ItemStack, List<?>> ITEM_ATTRIBUTES = AttributeHelpers::getItemStackAttributes;
    public static final Function<ItemStack, List<?>> ITEM_ENCHANTS = (stack) -> new ArrayList<>(stack.getEnchantments().getEnchantmentEntries());
    public static final Function<ItemStack, List<?>> ITEM_LORE_LINES = AttributeHelpers::getLore;
    public static final Function<ItemStack, List<?>> ITEM_CAN_DESTROY = (stack) -> getCanX(stack, DataComponentTypes.CAN_BREAK);
    public static final Function<ItemStack, List<?>> ITEM_CAN_PLAY_ON = (stack) -> getCanX(stack, DataComponentTypes.CAN_PLACE_ON);
    public static final Function<ItemStack, List<?>> ITEM_TAGS = (stack) -> stack.streamTags().toList();
    public static final Function<ItemStack, List<?>> ITEM_ITEMS = (stack) -> getItemItems(stack, false);
    public static final Function<ItemStack, List<?>> ITEM_ITEMS_COMPACT = (stack) -> compactItems(getItemItems(stack, false));

    public static final Function<BossBar, List<?>> BOSSBAR_PLAYERS = (bar) -> {
        if (CLIENT.getServer() == null || !(bar instanceof CommandBossBar cboss)) return Collections.EMPTY_LIST;

        List<?> listPlayers = Arrays.asList(CLIENT.getNetworkHandler().getPlayerList().toArray());
        List<PlayerListEntry> out = new ArrayList<>(cboss.getPlayers().size());

        for (var player : cboss.getPlayers())
            for (var listPlayer : listPlayers)
                if (player.getUuid().equals(((PlayerListEntry) listPlayer).getProfile().id()))
                    out.add((PlayerListEntry) listPlayer);

        return out;
    };


    public static final Function<ScoreboardObjective, List<?>> SCOREBOARD_OBJECTIVE_SCORES = (obj) -> scoreboard().getScoreboardEntries(obj).stream().sorted(InGameHud.SCOREBOARD_ENTRY_COMPARATOR).toList();
    public static final Function<ScoreboardObjective, List<?>> SCOREBOARD_OBJECTIVE_SCORES_ONLINE = (obj) -> scoreboard().getScoreboardEntries(obj).stream()
            .filter(score -> entryOnline(score.owner()))
            .sorted(InGameHud.SCOREBOARD_ENTRY_COMPARATOR).toList();


    public static ListProvider SCORES(String name) {
        return () -> Arrays.asList(scoreboard().getScores(name).scores.entrySet().toArray());
    }

    public static <T> ListProvider TAG_ENTRIES(Registry<T> registry, String name) {
        if (name.startsWith("#"))
            name = name.substring(1);
        Identifier id = Identifier.tryParse(name);
        if (id == null)
            return null;
        return () -> {
            List<T> values = new ArrayList<>();
            for (RegistryEntry<T> entry : registry.iterateEntries(TagKey.of(registry.getKey(), id)))
                values.add(entry.value());
            return values;
        };
    }


    // Don't change to method references
    public static final Function<?, List<?>> MOD_AUTHORS = (mod) -> ((Mod) mod).getAuthors();
    public static final Function<?, List<?>> MOD_CONTRIBUTORS = (mod) -> {
        List<Pair<String, String>> contributors = new ArrayList<>();
        for (var e : ((Mod) mod).getContributors().entrySet()) {
            String set = e.getKey();
            for (var contributor : e.getValue()) {
                contributors.add(new Pair<>(contributor, set));
            }
        }
        return contributors;
    };
    public static final Function<?, List<?>> MOD_CREDITS = (mod) -> {
        List<Pair<String, String>> credits = new ArrayList<>();
        for (var e : ((Mod) mod).getCredits().entrySet()) {
            String set = e.getKey();
            for (var contributor : e.getValue()) {
                credits.add(new Pair<>(contributor, set));
            }
        }
        return credits;
    };
    public static final Function<?, List<?>> MOD_BADGES = (mod) -> Arrays.asList(((Mod) mod).getBadges().toArray());
    public static final Function<?, List<?>> MOD_LICENSES = (mod) -> Arrays.asList(((Mod) mod).getLicense().toArray());
    public static final Function<?, List<?>> MOD_PARENTS = (mod) -> {
        Mod parent = ModMenu.MODS.get(((Mod) mod).getParent());
        return parent == null ? Collections.emptyList() : Collections.singletonList(parent);
    };
    public static final Function<?, List<?>> MOD_CHILDREN = (mod) -> ModMenu.PARENT_MAP.get(((Mod) mod));


    public static final Function<TradeOffer, List<?>> OFFER_FIRST_ADJUSTED = (offer) -> Collections.singletonList(offer.getDisplayedFirstBuyItem());
    public static final Function<TradeOffer, List<?>> OFFER_FIRST_BASE = (offer) -> Collections.singletonList(offer.getOriginalFirstBuyItem());
    public static final Function<TradeOffer, List<?>> OFFER_SECOND = (offer) -> Collections.singletonList(offer.getDisplayedSecondBuyItem());
    public static final Function<TradeOffer, List<?>> OFFER_RESULT = (offer) -> Collections.singletonList(offer.getSellItem());


    private static Entity hooked() {
        return CLIENT.player.fishHook == null ? null : CLIENT.player.fishHook.getHookedEntity();
    }


    // EVENTS

    public static EventListProvider<ChatHudLine> ON_CHAT_MESSAGE = new EventListProvider<>("chat_message");


}
