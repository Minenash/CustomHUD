package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.mixin.accessors.InGameHudAccessor;
import com.minenash.customhud.mixin.accessors.SubtitleHudAccessor;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Nullables;
import net.minecraft.world.GameMode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ListSuppliers {

    private static final Comparator<PlayerListEntry> ENTRY_ORDERING =
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
        TARGET_BLOCK_TAGS = () -> ComplexData.targetBlock == null ? Collections.EMPTY_LIST : ComplexData.targetBlock.streamTags().toList();

}
