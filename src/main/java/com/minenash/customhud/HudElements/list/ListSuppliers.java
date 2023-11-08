package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.mixin.accessors.InGameHudAccessor;
import com.minenash.customhud.mixin.accessors.SubtitleHudAccessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;

import java.util.*;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.list.AttributeHelpers.getEntityAttributes;

@SuppressWarnings("DataFlowIssue")
public class ListSuppliers {

    public static final Comparator<PlayerListEntry> ENTRY_ORDERING =
            Comparator.comparingInt((PlayerListEntry entry) -> entry.getGameMode() == GameMode.SPECTATOR ? 1 : 0)
                    .thenComparing((entry) -> Nullables.mapOrElse(entry.getScoreboardTeam(), Team::getName, ""))
                    .thenComparing((entry) -> entry.getProfile().getName(), String::compareToIgnoreCase);

    public static final ListProvider
        STATUS_EFFECTS = () -> CLIENT.player.getStatusEffects().stream().sorted(Comparator.comparingInt(e -> e.getEffectType().getCategory().ordinal())).toList(),
        STATUS_EFFECTS_POSITIVE = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.BENEFICIAL).toList(),
        STATUS_EFFECTS_NEGATIVE = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.HARMFUL).toList(),
        STATUS_EFFECTS_NEUTRAL = () -> CLIENT.player.getStatusEffects().stream().filter(e -> e.getEffectType().getCategory() == StatusEffectCategory.NEUTRAL).toList(),

        ONLINE_PLAYERS = () -> CLIENT.getNetworkHandler().getPlayerList().stream().sorted(ENTRY_ORDERING).toList(),
        SUBTITLES = () -> ((SubtitleHudAccessor)((InGameHudAccessor) CLIENT.inGameHud).getSubtitlesHud()).getEntries(),

        TARGET_BLOCK_STATES = () ->  ComplexData.targetBlock == null ? Collections.EMPTY_LIST : Arrays.asList(ComplexData.targetBlock.getEntries().entrySet().toArray()),
        TARGET_BLOCK_TAGS = () -> ComplexData.targetBlock == null ? Collections.EMPTY_LIST : ComplexData.targetBlock.streamTags().toList(),
        PLAYER_ATTRIBUTES = () -> getEntityAttributes(CLIENT.player),
        TARGET_ENTITY_ATTRIBUTES = () -> ComplexData.targetEntity == null ? Collections.EMPTY_LIST : getEntityAttributes(ComplexData.targetEntity),
        HOOKED_ENTITY_ATTRIBUTES = () -> hooked() == null ? Collections.EMPTY_LIST : getEntityAttributes(hooked()),
        TEAMS = () -> Arrays.asList(CLIENT.world.getScoreboard().getTeams().toArray());

    public static final Function<EntityAttributeInstance,List<?>> ATTRIBUTE_MODIFIERS = (attr) -> attr.getModifiers().stream().toList();
    public static final Function<Team,List<?>> TEAM_MEMBERS = (team) -> Arrays.asList(team.getPlayerList().toArray());
    public static final Function<Team,List<?>> TEAM_PLAYERS = (team) -> CLIENT.getNetworkHandler().getPlayerList().stream().filter(p -> p.getScoreboardTeam() == team).sorted(ENTRY_ORDERING).toList();


    private static Entity hooked() {return CLIENT.player.fishHook == null ? null : CLIENT.player.fishHook.getHookedEntity();}

}
