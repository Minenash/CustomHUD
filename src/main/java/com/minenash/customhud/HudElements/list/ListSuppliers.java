package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.mixin.accessors.AttributeContainerAccessor;
import com.minenash.customhud.mixin.accessors.DefaultAttributeContainerAccessor;
import com.minenash.customhud.mixin.accessors.InGameHudAccessor;
import com.minenash.customhud.mixin.accessors.SubtitleHudAccessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ListSuppliers {

    public static final Comparator<PlayerListEntry> ENTRY_ORDERING =
            Comparator.comparingInt((PlayerListEntry entry) -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
                    .thenComparing((entry) -> Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, ""))
                    .thenComparing((entry) -> entry.getProfile().getName(), String::compareToIgnoreCase);

    public static final Supplier<List<?>>
        STATUS_EFFECTS = () -> CLIENT.player.getStatusEffects().stream().sorted(Comparator.comparingInt(e -> e.getEffectType().getCategory().ordinal())).toList(),
        STATUS_EFFECTS_POSITIVE = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.BENEFICIAL).toList(),
        STATUS_EFFECTS_NEGATIVE = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.HARMFUL).toList(),
        STATUS_EFFECTS_NEUTRAL = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.NEUTRAL).toList(),

        ONLINE_PLAYERS = () -> CLIENT.getNetworkHandler().getPlayerList().stream().sorted(ENTRY_ORDERING).toList(),
        SUBTITLES = () -> ((SubtitleHudAccessor)((InGameHudAccessor) CLIENT.inGameHud).getSubtitlesHud()).getEntries(),

        TARGET_BLOCK_PROPERTIES = () ->  ComplexData.targetBlock == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetBlock.getEntries().entrySet().toArray()),
        TARGET_BLOCK_TAGS = () -> ComplexData.targetBlock == null ? Collections.EMPTY_LIST : ComplexData.targetBlock.streamTags().toList(),
        PLAYER_ATTRIBUTES = () -> getAttributes(CLIENT.player),
        TARGET_ENTITY_ATTRIBUTES = () -> ComplexData.targetEntity == null ? Collections.EMPTY_LIST : getAttributes(ComplexData.targetEntity),
        HOOKED_ENTITY_ATTRIBUTES = () -> hooked() == null ? Collections.EMPTY_LIST : getAttributes(hooked()),
        ATTRIBUTE_MODIFIERS = () -> ((EntityAttributeInstance) ListManager.getValue()).getModifiers().stream().toList(),
        TEAMS = () -> Arrays.asList(CLIENT.world.getScoreboard().getTeams().toArray()),
        TEAM_MEMBERS = () -> Arrays.asList(((Team) ListManager.getValue()).getPlayerList().toArray()),
        TEAM_PLAYER_FROM_LIST = () -> {
            Team team = (Team) ListManager.getValue();
            return CLIENT.getNetworkHandler().getPlayerList().stream().filter(p -> p.getScoreboardTeam() == team).sorted(ENTRY_ORDERING).toList();
        };

    public static Entity getFullEntity(Entity entity) {
        return CLIENT.getServer() == null || entity == null? entity :
                CLIENT.getServer().getWorld(entity.getWorld().getRegistryKey()).getEntity(entity.getUuid());
    }

    public static List<EntityAttributeInstance> getAttributes(Entity entity) {
        entity = getFullEntity(entity);
        if (!(entity instanceof LivingEntity le) ) return Collections.EMPTY_LIST;
        AttributeContainerAccessor container = (AttributeContainerAccessor) le.getAttributes();
        Map<EntityAttribute, EntityAttributeInstance> instances = new HashMap<>(((DefaultAttributeContainerAccessor)container.getFallback()).getInstances());
        instances.putAll(container.getCustom());
        return (entity.getWorld().isClient ?
                instances.values().stream().filter(a -> a.getAttribute().isTracked()) : instances.values().stream())
                .sorted(Comparator.comparing(a -> I18n.translate(a.getAttribute().getTranslationKey()))).toList();
    }
    private static Entity hooked() {return CLIENT.player.fishHook == null ? null : CLIENT.player.fishHook.getHookedEntity();}

}
