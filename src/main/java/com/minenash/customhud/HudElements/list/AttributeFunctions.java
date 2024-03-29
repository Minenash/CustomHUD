package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.FuncElements.Num;
import com.minenash.customhud.HudElements.FuncElements.Num.NumEntry;
import com.minenash.customhud.HudElements.FuncElements.Special.Entry;
import com.minenash.customhud.HudElements.list.AttributeHelpers.ItemAttribute;
import net.minecraft.block.Block;
import net.minecraft.client.gui.hud.SubtitlesHud.SubtitleEntry;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.*;
import net.minecraft.stat.StatFormatter;
import net.minecraft.state.property.Property;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;

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



    // STATUS EFFECTS
    public static final Function<StatusEffectInstance,String> STATUS_NAME = (status) -> I18n.translate(status.getTranslationKey());
    public static final Function<StatusEffectInstance,String> STATUS_ID = (status) -> Registries.STATUS_EFFECT.getId(status.getEffectType()).toString();
    public static final NumEntry<StatusEffectInstance> STATUS_DURATION = Num.of(HMS, StatusEffectInstance::getDuration);
    public static final Function<StatusEffectInstance,Number> STATUS_AMPLIFICATION = StatusEffectInstance::getAmplifier;
    public static final Function<StatusEffectInstance,Boolean> STATUS_AMBIENT = StatusEffectInstance::isAmbient;
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_PARTICLES = StatusEffectInstance::shouldShowParticles;
    public static final Function<StatusEffectInstance,Boolean> STATUS_SHOW_ICON = StatusEffectInstance::shouldShowIcon;
    public static final Function<StatusEffectInstance,Number> STATUS_COLOR = (status) -> status.getEffectType().getColor();
    public static final Entry<StatusEffectInstance> STATUS_CATEGORY = new Entry<>(
            (status) -> WordUtils.capitalize(status.getEffectType().getCategory().name().toLowerCase()),
            (status) -> status.getEffectType().getCategory().ordinal(),
            (status) -> status.getEffectType().getCategory().ordinal() != 1);


    // PLAYERS (From PlayerList)
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
    public static final Function<SubtitleEntry,Number> SUBTITLE_TIME = (subtitle) -> (3*CLIENT.options.getNotificationDisplayTime().getValue()) - (Util.getMeasuringTimeMs() - subtitle.getTime()) / 1000D;
    public static final Function<SubtitleEntry,Number> SUBTITLE_ALPHA = (subtitle) -> {
        double d = CLIENT.options.getNotificationDisplayTime().getValue();
        int p = MathHelper.floor(MathHelper.clampedLerp(255.0F, 75.0F, (float)(Util.getMeasuringTimeMs() - subtitle.getTime()) / (float)(3000.0 * d)));
        return  (p << 24);
    };
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


    // ITEMS
    public static final Function<ItemStack, String> ITEM_ID = (stack) -> Registries.ITEM.getId(stack.getItem()).toString();
    public static final Function<ItemStack, String> ITEM_NAME = (stack) -> stack.getItem().getName().getString();
    public static final Function<ItemStack, Number> ITEM_RAW_ID = (stack) -> Item.getRawId(stack.getItem());
    public static final Function<ItemStack, Boolean> ITEM_IS_NOT_EMPTY = (stack) -> !stack.isEmpty();
    public static final Entry<ItemStack> ITEM_CUSTOM_NAME = new Entry<>(
            (stack) -> stack.getName().getString(),
            (stack) -> stack.getName().getString().length(),
            (stack) -> !stack.getName().getString().equals(stack.getItem().getName().getString()));

    public static final Function<ItemStack, Number> ITEM_COUNT = (stack) -> stack.getCount();
    public static final Function<ItemStack, Number> ITEM_MAX_COUNT = (stack) -> stack.getMaxCount();
    public static final Function<ItemStack, Boolean> ITEM_IS_STACKABLE = (stack) -> stack.getMaxCount() > 1;
    public static final Function<ItemStack, Boolean> ITEM_HAS_DURABILITY = (stack) -> stack.getItem().getMaxDamage() - CLIENT.player.getMainHandStack().getDamage() > 0;
    public static final Function<ItemStack, Boolean> ITEM_HAS_MAX_DURABILITY = (stack) -> stack.getItem().getMaxDamage() > 0;
    public static final Function<ItemStack, Number> ITEM_DURABILITY = (stack) -> stack.getMaxDamage() - stack.getDamage();
    public static final Function<ItemStack, Number> ITEM_MAX_DURABILITY = (stack) -> stack.getMaxDamage();
    public static final Function<ItemStack, Number> ITEM_DURABILITY_PERCENT = (stack) -> 100 - stack.getDamage() / (float) stack.getMaxDamage() * 100;
    public static final Function<ItemStack, Number> ITEM_DURABILITY_COLOR = (stack) ->  stack.getItem().getMaxDamage() > 0 ? stack.getItemBarColor() : null;
    public static final Function<ItemStack, Boolean> ITEM_UNBREAKABLE = (stack) -> stack.hasNbt() && stack.getNbt().getBoolean("Unbreakable");
    public static final Function<ItemStack, Number> ITEM_REPAIR_COST = (stack) -> stack.getRepairCost();
    public static final Function<ItemStack, Number> ITEM_HIDE_FLAGS_NUM = (stack) -> stack.getHideFlags();
    public static final Entry<ItemStack> ITEM_RARITY = new Entry<>(
            (stack) -> stack.getItem().getRarity(stack).name(),
            (stack) -> stack.getItem().getRarity(stack).formatting.getColorValue(),
            (stack) -> stack.getItem().getRarity(stack) != Rarity.COMMON
    );

    // CAN X
    public static final Function<Block, String> BLOCK_ID = (block) -> Registries.BLOCK.getId(block).toString();
    public static final Function<Block, String> BLOCK_NAME = (block) -> I18n.translate(block.getTranslationKey());

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


    // ITEM ATTRIBUTE MODIFIERS
    public static final Function<ItemAttribute,String> ITEM_ATTR_SLOT = (attr) -> attr.slot();
    public static final Function<ItemAttribute,String> ITEM_ATTR_NAME = (attr) -> I18n.translate(attr.attribute().getTranslationKey());
    public static final Function<ItemAttribute,String> ITEM_ATTR_ID = (attr) -> Registries.ATTRIBUTE.getId(attr.attribute()).toString();
    public static final Function<ItemAttribute,Boolean> ITEM_ATTR_TRACKED = (attr) -> attr.attribute().isTracked();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE_DEFAULT = (attr) -> attr.attribute().getDefaultValue();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE_BASE = (attr) -> CLIENT.player.getAttributeBaseValue(attr.attribute());
    public static final Function<ItemAttribute,Number> ITEM_ATTR_VALUE = (attr) -> CLIENT.player.getAttributeValue(attr.attribute());
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_NAME = (attr) -> {
        String key = "attribute.name." + attr.modifier().getName();
        return I18n.hasTranslation(key) ? I18n.translate(key) : attr.modifier().getName(); };
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_ID = (attr) -> attr.modifier().getId().toString();
    public static final Function<ItemAttribute,Number> ITEM_ATTR_MODIFIER_VALUE = (attr) -> attr.modifier().getValue();
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_OPERATION_NAME = (attr) -> switch (attr.modifier().getOperation()) {
        case ADDITION -> "Addition";
        case MULTIPLY_BASE -> "Multiplication Base";
        case MULTIPLY_TOTAL -> "Multiplication Total"; };
    public static final Function<ItemAttribute,String> ITEM_ATTR_MODIFIER_OPERATION = (attr) -> switch (attr.modifier().getOperation()) {
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
    public static final Entry<Team> TEAM_COLOR = new Entry<>(
            (team) -> team.getColor().getName(),
            (team) -> team.getColor().getColorValue(),
            (team) -> team.getColor() != Formatting.RESET
    );


    // SCOREBOARD OBJECTIVES
    public static final Function<ScoreboardObjective,String> OBJECTIVE_NAME = (obj) -> obj.getDisplayName().getString();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_ID = (obj) -> obj.getName();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_CRITIERIA = (obj) -> CLIENT.getServer() == null ? "unknown" : obj.getCriterion().getName();
    public static final Function<ScoreboardObjective,String> OBJECTIVE_DISPLAY_SLOT = (obj) -> {
        Scoreboard scoreboard = AttributeHelpers.scoreboard();
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values())
            if (scoreboard.getObjectiveForSlot(slot) == obj)
                return slot.name().toLowerCase();
        return "none";
    };


    // SCOREBOARD OBJECTIVE SCORE
    public static final Function<ScoreboardPlayerScore,String> OBJECTIVE_SCORE_HOLDER = (score) -> score.getPlayerName();
    public static final Function<ScoreboardPlayerScore,Number> OBJECTIVE_SCORE_VALUE = (score) -> score.getScore();


    // SCOREBOARD SCORE
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,String> SCORES_OBJECTIVE_NAME = (entry) -> entry.getKey().getDisplayName().getString();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,String> SCORES_OBJECTIVE_ID = (entry) -> entry.getKey().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,String> SCORES_OBJECTIVE = (entry) -> CLIENT.getServer() == null ? "unknown" : entry.getKey().getCriterion().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,Number> SCORES_VALUE = (entry) -> entry.getValue().getScore();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,String> SCORES_OBJECTIVE_CRITIERIA = (entry) -> CLIENT.getServer() == null ? "unknown" : entry.getKey().getCriterion().getName();
    public static final Function<Map.Entry<ScoreboardObjective, ScoreboardPlayerScore>,String> SCORES_OBJECTIVE_DISPLAY_SLOT = (entry) -> {
        Scoreboard scoreboard = AttributeHelpers.scoreboard();
        for (ScoreboardDisplaySlot slot : ScoreboardDisplaySlot.values())
            if (scoreboard.getObjectiveForSlot(slot) == entry.getKey())
                return slot.name().toLowerCase();
        return "none";
    };


    // BOSSBARS TODO: ADD COLOR
    public static final Function<BossBar,String> BOSSBAR_NAME = (bar) -> bar.getName().getString();
    public static final Function<BossBar,String> BOSSBAR_UUID = (bar) -> bar.getUuid().toString();
    public static final Function<BossBar,Number> BOSSBAR_PERCENT = (bar) -> bar.getPercent();
    public static final Function<BossBar,Boolean> BOSSBAR_DARKEN_SKY = (bar) -> bar.shouldDarkenSky();
    public static final Function<BossBar,Boolean> BOSSBAR_DRAGON_MUSIC = (bar) -> bar.hasDragonMusic();
    public static final Function<BossBar,Boolean> BOSSBAR_THICKENS_FOG = (bar) -> bar.shouldThickenFog();
    public static final Entry<BossBar> BOSSBAR_COLOR = new Entry<>(
            (bar) -> WordUtils.capitalize(bar.getColor().getName().toLowerCase()),
            (bar) -> AttributeHelpers.getBossBarColor(bar),
            (bar) -> bar.getColor() != BossBar.Color.WHITE
    );
    public static final Entry<BossBar> BOSSBAR_TEXT_COLOR = new Entry<>(
            (bar) -> WordUtils.capitalize(bar.getColor().getTextFormat().getName().toLowerCase()),
            (bar) -> bar.getColor().getTextFormat().getColorValue(),
            (bar) -> bar.getColor() != BossBar.Color.WHITE
    );
    public static final Entry<BossBar> BOSSBAR_STYLE = new Entry<>(
            (bar) -> switch (bar.getStyle()) {
                case PROGRESS -> "Progress";
                case NOTCHED_6 -> "Notched 6";
                case NOTCHED_10 -> "Notched 10";
                case NOTCHED_12 -> "Notched 12";
                case NOTCHED_20 -> "Notched 20";
            },
            (bar) -> bar.getStyle().ordinal(),
            (bar) -> bar.getStyle().ordinal() != 0
    );
    public static final Function<BossBar,String> BOSSBAR_ID = (bar) -> {
        if (CLIENT.getServer() == null) return null;
        for (var entry : CLIENT.getServer().getBossBarManager().commandBossBars.entrySet())
            if (entry.getValue() == bar) return entry.getKey().toString();
        return null;
    };
    public static final Function<BossBar,Boolean> BOSSBAR_IS_VISIBLE = (bar) -> bar instanceof CommandBossBar cbb ? cbb.isVisible() : null;





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
