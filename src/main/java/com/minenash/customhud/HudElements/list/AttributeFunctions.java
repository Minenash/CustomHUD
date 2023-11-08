package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.FuncElements.Num;
import com.minenash.customhud.HudElements.FuncElements.Num.NumEntry;
import com.minenash.customhud.HudElements.FuncElements.Special.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.hud.SubtitlesHud.SubtitleEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.*;
import net.minecraft.stat.StatFormatter;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.supplier.NumberSupplierElement.of;


@SuppressWarnings("ALL")
public class AttributeFunctions {
    private static final StatFormatter HMS = ticks -> {
        int rawSeconds = ticks / 20;
        int seconds = rawSeconds % 60;
        int minutes = (rawSeconds / 60) % 60;
        int hours = (rawSeconds / 60 / 60);
        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };


    public static final Function<String,String> DIRECT = (str) -> str;



    // STATUS EFFECTS TODO: ADD STATUS COLOR
    public static final Function<StatusEffectInstance,String> STATUS_NAME = (status) -> I18n.translate(status.getTranslationKey());
    public static final Function<StatusEffectInstance,String> STATUS_ID = (status) -> Registries.STATUS_EFFECT.getId(status.getEffectType()).toString();
    public static final NumEntry<StatusEffectInstance> STATUS_DURATION = Num.of(HMS, StatusEffectInstance::getDuration);
    public static final Function<StatusEffectInstance,Number> STATUS_AMPLIFICATION = StatusEffectInstance::getAmplifier;
    public static final Function<StatusEffectInstance,Boolean> STATUS_AMBIENT = StatusEffectInstance::isAmbient;
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_PARTICLES = StatusEffectInstance::shouldShowParticles;
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_ICON = StatusEffectInstance::shouldShowIcon;
    public static final Entry<StatusEffectInstance> STATUS_CATEGORY = new Entry<>(
            (status) -> WordUtils.capitalize(status.getEffectType().getCategory().name().toLowerCase()),
            (status) -> status.getEffectType().getCategory().ordinal(),
            (status) -> status.getEffectType().getCategory().ordinal() != 1);


    // PLAYERS (From PlayerList) TODO: ADD TEAM COLOR AND IS_VERIFIED
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_NAME = (player) -> player.getProfile().getName();
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_UUID = (player) -> player.getProfile().getId().toString();
    public static final Function<PlayerListEntry,String> PLAYER_ENTRY_TEAM = (player) -> player.getScoreboardTeam().getName(); //TODO: CHANGE TEAM VAR
    public static final Function<PlayerListEntry,Number> PLAYER_ENTRY_LATENCY = (player) -> player.getLatency();
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_SURVIVAL = (player) -> player.getGameMode() == GameMode.SURVIVAL;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_CREATIVE = (player) -> player.getGameMode() == GameMode.CREATIVE;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_ADVENTURE = (player) -> player.getGameMode() == GameMode.ADVENTURE;
    public static final Function<PlayerListEntry,Boolean> PLAYER_ENTRY_SPECTATOR = (player) -> player.getGameMode() == GameMode.SPECTATOR;
    public static final Entry<PlayerListEntry> PLAYER_ENTRY_GAMEMODE = new Entry<> (
            (player) -> player.getGameMode().getName(),
            (player) -> player.getGameMode().getId(),
            (player) -> true);
    public static final Function<PlayerListEntry,Number> PLAYER_ENTRY_LIST_SCORE = (player) -> {
        Scoreboard scoreboard = CLIENT.world.getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
        return scoreboard.getPlayerScore(player.getProfile().getName(), objective).getScore();
    };

    // SUBTITLES TODO: ADD ALPHA COLOR
    public static final Function<SubtitleEntry,String> SUBTITLE_NAME = (subtitle) -> subtitle.getText().getString();
    public static final Function<SubtitleEntry,Number> SUBTITLE_AGE = (subtitle) -> (Util.getMeasuringTimeMs() - subtitle.getTime()) / 1000D;
    public static final Function<SubtitleEntry,Number> SUBTITLE_TIME = (subtitle) -> 3 - (Util.getMeasuringTimeMs() - subtitle.getTime()) / 1000D;
    public static final Function<SubtitleEntry,Number> SUBTITLE_DISTANCE = (subtitle) -> subtitle.getPosition().distanceTo(CLIENT.player.getEyePos());
    public static final Function<SubtitleEntry,Number> SUBTITLE_X = (subtitle) -> subtitle.getPosition().getX();
    public static final Function<SubtitleEntry,Number> SUBTITLE_Y = (subtitle) -> subtitle.getPosition().getY();
    public static final Function<SubtitleEntry,Number> SUBTITLE_Z = (subtitle) -> subtitle.getPosition().getZ();
    public static final Function<SubtitleEntry,Boolean> SUBTITLE_LEFT = (subtitle) -> subtitle$getDirection(subtitle) == -1;
    public static final Function<SubtitleEntry,Boolean> SUBTITLE_RIGHT = (subtitle) -> subtitle$getDirection(subtitle) == 1;
    public static final Function<SubtitleEntry,String> SUBTITLE_DIRECTION = (subtitle) -> {
        int dir = subtitle$getDirection(subtitle);
        return dir == 0 ? "=" : dir == 1 ? ">" : "<";
    };


    // BLOCK STATES
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_NAME = (property) -> property.getKey().getName();
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_VALUE = (property) -> property.getValue().toString();
    public static final Function<Map.Entry<Property<?>,Comparable<?>>,String> BLOCK_STATE_FULL_TYPE = (property) -> property.getKey().getType().getSimpleName();
    public static final Entry<Map.Entry<Property<?>,Comparable<?>> > BLOCK_STATE_TYPE = new Entry<> (
            (property) -> switch (blockstate$getPropertyType(property.getKey().getType())) {
                case 1 -> "Boolean";
                case 2 -> "Number";
                case 3 -> "Enum";
                default -> "String"; },
            (property) -> blockstate$getPropertyType(property.getKey().getType()),
            (property) -> blockstate$getPropertyType(property.getKey().getType()) != 0);


    // BLOCK TAGS
    public static final Function<TagKey<Block>,String> BLOCK_TAG_ID = (tag) -> tag.id().toString();
    public static final Function<TagKey<Block>,String> BLOCK_TAG_NAME = (tag) -> tag.id().getNamespace().equals("minecraft") ?
            tag.id().getPath() : tag.id().toString();


    // ENCHANTMENTS
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_NAME = (enchant) -> I18n.translate(enchant.getKey().getTranslationKey());
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_RARITY = (enchant) -> enchant.getKey().getRarity().toString().toLowerCase();
    public static final Function<Map.Entry<Enchantment,Integer>,Number> ENCHANT_NUM = (enchant) -> enchant.getValue();
    public static final Function<Map.Entry<Enchantment,Integer>,String> ENCHANT_FULL =
            (enchant) -> I18n.translate(enchant.getKey().getTranslationKey()) + " " + I18n.translate("enchantment.level." + enchant.getValue());
    public static final Entry<Map.Entry<Enchantment,Integer>> ENCHANT_LEVEL = new Entry<> (
            (enchant) -> I18n.translate("enchantment.level." + enchant.getValue()),
            (enchant) -> enchant.getValue(),
            (enchant) -> true);


    // ATTRIBUTES
    public static final Function<EntityAttributeInstance,String> ATTRIBUTE_NAME = (attr) -> I18n.translate(attr.getAttribute().getTranslationKey());
    public static final Function<EntityAttributeInstance,String> ATTRIBUTE_ID = (attr) -> Registries.ATTRIBUTE.getId(attr.getAttribute()).toString();
    public static final Function<EntityAttributeInstance,Boolean> ATTRIBUTE_TRACKED = (attr) -> attr.getAttribute().isTracked();
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE_DEFAULT = (attr) -> attr.getAttribute().getDefaultValue();
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE_BASE = EntityAttributeInstance::getBaseValue;
    public static final Function<EntityAttributeInstance,Number> ATTRIBUTE_VALUE = EntityAttributeInstance::getValue;


    // ATTRIBUTE MODIFIERS
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_NAME = (modifier) -> modifier.getName();
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_ID = (modifier) -> modifier.getId().toString();
    public static final Function<EntityAttributeModifier,Number> ATTRIBUTE_MODIFIER_VALUE = (modifier) -> modifier.getValue();
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_OPERATION_NAME = (modifier) -> switch (modifier.getOperation()) {
        case ADDITION -> "Addition";
        case MULTIPLY_BASE -> "Multiplication Base";
        case MULTIPLY_TOTAL -> "Multiplication Total"; };
    public static final Function<EntityAttributeModifier,String> ATTRIBUTE_MODIFIER_OPERATION = (modifier) -> switch (modifier.getOperation()) {
        case ADDITION -> "+";
        case MULTIPLY_BASE -> "☒";
        case MULTIPLY_TOTAL -> "×"; };


    // TEAM
    public static final Function<Team,String> TEAM_NAME = (team) -> team.getDisplayName().getString();
    public static final Function<Team,String> TEAM_ID = Team::getName;
    public static final Function<Team,Boolean> TEAM_FRIENDLY_FIRE = Team::isFriendlyFireAllowed;
    public static final Function<Team,Boolean> TEAM_FRIENDLY_INVIS = Team::shouldShowFriendlyInvisibles;
    public static final Entry<Team> TEAM_NAME_TAG_VISIBILITY = new Entry<>(
            (team) -> team.getNameTagVisibilityRule().getDisplayName().getString(),
            (team) -> team.getNameTagVisibilityRule().value,
            (team) -> team$visibleToPlayer(team, team.getNameTagVisibilityRule()));
    public static final Entry<Team> TEAM_DEATH_MGS_VISIBILITY = new Entry<>(
            (team) -> team.getNameTagVisibilityRule().getDisplayName().getString(),
            (team) -> team.getNameTagVisibilityRule().value,
            (team) -> team$visibleToPlayer(team, team.getDeathMessageVisibilityRule()));
    public static final Entry<Team> TEAM_COLLISION = new Entry<>(
            (team) -> team.getCollisionRule().getDisplayName().getString(),
            (team) -> team.getCollisionRule().value,
            (team) -> team.getCollisionRule() != AbstractTeam.CollisionRule.NEVER);







    // HELPER METHODS


    public static int subtitle$getDirection(SubtitleEntry subtitle) {
        float xRotation = -CLIENT.player.getPitch() * ((float)Math.PI / 180);
        float yRotation = -CLIENT.player.getYaw() * ((float)Math.PI / 180);

        Vec3d vec3d2 = new Vec3d(0.0, 0.0, -1.0).rotateX(xRotation).rotateY(yRotation);
        Vec3d vec3d3 = new Vec3d(0.0, 1.0, 0.0).rotateX(xRotation).rotateY(yRotation);
        Vec3d vec3d5 = subtitle.getPosition().subtract(CLIENT.player.getEyePos()).normalize();
        double e = vec3d2.crossProduct(vec3d3).dotProduct(vec3d5);

        return -vec3d2.dotProduct(vec3d5) > 0.5 || e == 0? 0 : e < 0 ? 1 : -1;
    }

    public static int blockstate$getPropertyType(Class<?> type) {
        return type == Boolean.class ? 1 : Number.class.isAssignableFrom(type) ? 2 : type.isEnum() ? 3 : 0;
    }

    public static boolean team$visibleToPlayer(Team team, AbstractTeam.VisibilityRule rule) {
        return rule == AbstractTeam.VisibilityRule.ALWAYS
                || (rule == AbstractTeam.VisibilityRule.HIDE_FOR_OTHER_TEAMS && CLIENT.player.isTeamPlayer(team))
                || (rule == AbstractTeam.VisibilityRule.HIDE_FOR_OWN_TEAM && !CLIENT.player.isTeamPlayer(team));
    }


}
