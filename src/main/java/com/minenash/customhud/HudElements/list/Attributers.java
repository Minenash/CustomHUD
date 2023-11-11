package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.FormattedElement;
import com.minenash.customhud.HudElements.FuncElements;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.FuncElements.*;
import com.minenash.customhud.HudElements.functional.FunctionalElement.CreateListElement;
import com.minenash.customhud.HudElements.icon.BossbarIcon;
import com.minenash.customhud.HudElements.icon.PlayerHeadIconElement;
import com.minenash.customhud.HudElements.icon.ItemSupplierIconElement;
import com.minenash.customhud.HudElements.icon.StatusEffectIconElement;
import com.minenash.customhud.data.Flags;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.list.AttributeFunctions.*;
import static com.minenash.customhud.HudElements.list.AttributeFunctions.ITEM_ATTR_MODIFIER_NAME;
import static com.minenash.customhud.HudElements.list.ListSuppliers.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class Attributers {

    @FunctionalInterface
    public interface Attributer {
        HudElement get(Supplier supplier, String attr, Flags flags);
    }

    //TODO: IS ONLY LIST ONLY
    public static final Attributer EFFECT = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,STATUS_NAME);
        case "id" -> new Str(sup,STATUS_ID);
        case "duration", "dur" -> new Num(sup, STATUS_DURATION, flags);
        case "amplification", "amp" -> new Num(sup, STATUS_AMPLIFICATION, flags);
        case "ambient" -> new Bool(sup,STATUS_AMBIENT);
        case "show_particles", "particles" -> new Bool(sup,STATUS_SHOW_PARTICLES);
        case "show_icon" -> new Bool(sup,STATUS_SHOW_ICON);
        case "category", "cat" -> new Special(sup,STATUS_CATEGORY);
        case "icon" -> new StatusEffectIconElement(flags, true); //LIST ONLY
        case "icon_no_bg" -> new StatusEffectIconElement(flags, false); //LIST ONLY
        default -> null;
    };

    public static final Attributer PLAYER = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,PLAYER_ENTRY_NAME);
        case "id" -> new Str(sup,PLAYER_ENTRY_UUID);
        case "team" -> new Str(sup, PLAYER_ENTRY_TEAM);
        case "latency" -> new Num(sup,PLAYER_ENTRY_LATENCY, flags);
        case "list_score" -> new Num(sup,PLAYER_ENTRY_LIST_SCORE, flags);
        case "gamemode" -> new Special(sup,PLAYER_ENTRY_GAMEMODE);
        case "survival" -> new Bool(sup,PLAYER_ENTRY_SURVIVAL);
        case "creative" -> new Bool(sup,PLAYER_ENTRY_CREATIVE);
        case "adventure" -> new Bool(sup,PLAYER_ENTRY_ADVENTURE);
        case "spectator" -> new Bool(sup,PLAYER_ENTRY_SPECTATOR);
        case "head" -> new PlayerHeadIconElement(flags); //TODO FIX
        default -> null;
    };

    public static final Attributer SUBTITLE = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, SUBTITLE_NAME);

        case "age" -> new Num(sup, SUBTITLE_AGE, flags);
        case "time" -> new Num(sup, SUBTITLE_TIME, flags);
        case "x" -> new Num(sup, SUBTITLE_X, flags);
        case "y" -> new Num(sup, SUBTITLE_Y, flags);
        case "z" -> new Num(sup, SUBTITLE_Z, flags);
        case "dist", "distance" -> new Num(sup, SUBTITLE_DISTANCE, flags);

        case "dir", "direction" -> new Str(sup, SUBTITLE_DIRECTION);
        case "left" -> new Bool(sup, SUBTITLE_LEFT);
        case "right" -> new Bool(sup, SUBTITLE_RIGHT);
        default -> null;
    };

    public static final Attributer BLOCK_STATE = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, BLOCK_STATE_NAME);
        case "type" -> new Special(sup, BLOCK_STATE_TYPE);
        case "full_type" -> new Str(sup,BLOCK_STATE_FULL_TYPE);
        case "value" -> new Str(sup, BLOCK_STATE_VALUE);
        default -> null;
    };

    public static final Attributer BLOCK_TAG = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,BLOCK_TAG_NAME);
        case "id" -> new Str(sup,BLOCK_TAG_ID);
        default -> null;
    };

    public static final Attributer ENCHANTMENT = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,ENCHANT_NAME);
        case "full" -> new Str(sup,ENCHANT_FULL);
        case "level" -> new Special(sup,ENCHANT_LEVEL);
        case "num", "number" -> new Num(sup,ENCHANT_NUM, flags);
        case "rarity" -> new Str(sup,ENCHANT_RARITY);
        default -> null;
    };

    public static final Attributer ITEM_LORE_LINE = (sup, name, f) -> name.equals("line") ? new Str(sup, DIRECT) : null;
    public static final Attributer ITEM_INFO_INFO = (sup, name, f) -> name.equals("info") ? new Str(sup, DIRECT) : null;

    public static final Attributer ITEM_ATTRIBUTE_MODIFIER = (sup, name, flags) -> switch (name) {
        case "slot" -> new Str(sup,ITEM_ATTR_SLOT);
        case "attribute","attr" -> new Str(sup,ITEM_ATTR_NAME);
        case "attribute_id","attr_id" -> new Str(sup,ITEM_ATTR_ID);
        case "tracked" -> new Bool(sup,ITEM_ATTR_TRACKED);
        case "default_value" -> new Num(sup,ITEM_ATTR_VALUE_DEFAULT, flags);
        case "attribute_value","attr_value" -> new Num(sup,ITEM_ATTR_VALUE, flags);
        case "modifier_name","mod_name" -> new Str(sup,ITEM_ATTR_MODIFIER_NAME);
        case "modifier_id","mod_id" -> new Str(sup,ITEM_ATTR_MODIFIER_ID);
        case "mod_amount","amount" -> new Num(sup,ITEM_ATTR_MODIFIER_VALUE, flags);
        case "op", "operation" -> new Str(sup,ITEM_ATTR_MODIFIER_OPERATION);
        case "op_name", "operation_name" -> new Str(sup,ITEM_ATTR_MODIFIER_OPERATION_NAME);
        default -> null;
    };

    public static final Attributer ITEM_CAN_X = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,BLOCK_NAME);
        case "id" -> new Str(sup,BLOCK_ID);
        default -> null;
    };

    public static final Attributer ITEM = (sup, name, flags) -> switch (name) {
        case "", "item" -> new Special(sup, ITEM_NAME, ITEM_RAW_ID, ITEM_IS_NOT_EMPTY);
        case "id" -> new Special(sup, ITEM_ID, ITEM_RAW_ID, ITEM_IS_NOT_EMPTY);
        case "name" -> new Special(sup, ITEM_CUSTOM_NAME);
        case "count" -> new NumBool(sup, ITEM_COUNT, ITEM_IS_NOT_EMPTY, flags);
        case "max_count" -> new NumBool(sup, ITEM_MAX_COUNT, ITEM_IS_STACKABLE, flags);
        case "dur","durability" -> new NumBool(sup, ITEM_DURABILITY, ITEM_HAS_DURABILITY, flags);
        case "max_dur","max_durability" -> new NumBool(sup, ITEM_MAX_DURABILITY, ITEM_HAS_MAX_DURABILITY, flags);
        case "dur_per","durability_percentage" -> new NumBool(sup, ITEM_DURABILITY_PERCENT, ITEM_HAS_MAX_DURABILITY, flags);
        case "unbreakable" -> new Bool(sup, ITEM_UNBREAKABLE);
        case "repair_cost" -> new Num(sup, ITEM_REPAIR_COST, flags);
        case "icon" -> new ItemSupplierIconElement(sup, flags);
        case "hide_flags" -> new Num(sup, ITEM_HIDE_FLAGS_NUM, flags);

        case "enchants" -> new CreateListElement(sup, ITEM_ENCHANTS, ENCHANTMENT);
        case "lore" -> new CreateListElement(sup, ITEM_LORE_LINES, ITEM_LORE_LINE);
        case "attributes", "attrs" -> new CreateListElement(sup, ITEM_ATTRIBUTES, ITEM_ATTRIBUTE_MODIFIER);
        case "can_destroy" -> new CreateListElement(sup, ITEM_CAN_DESTROY, ITEM_CAN_X);
        case "can_place_on" -> new CreateListElement(sup, ITEM_CAN_PLAY_ON, ITEM_CAN_X);
        case "info_shown" -> new CreateListElement(sup, ITEM_SHOWN, ITEM_INFO_INFO);
        case "info_hidden" -> new CreateListElement(sup, ITEM_HIDDEN, ITEM_INFO_INFO);
        default -> null;
    };

    public static final Attributer ATTRIBUTE_MODIFIER = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,ATTRIBUTE_MODIFIER_NAME);
        case "id" -> new Str(sup,ATTRIBUTE_MODIFIER_ID);
        case "value" -> new Num(sup,ATTRIBUTE_MODIFIER_VALUE, flags);
        case "op", "operation" -> new Str(sup,ATTRIBUTE_MODIFIER_OPERATION);
        case "op_name", "operation_name" -> new Str(sup,ATTRIBUTE_MODIFIER_OPERATION_NAME);
        default -> null;
    };

    public static final Attributer ATTRIBUTE = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup,ATTRIBUTE_NAME);
        case "id" -> new Str(sup,ATTRIBUTE_ID);
        case "tracked" -> new Bool(sup,ATTRIBUTE_TRACKED);
        case "default_value" -> new Num(sup,ATTRIBUTE_VALUE_DEFAULT, flags);
        case "base_value" -> new Num(sup,ATTRIBUTE_VALUE_BASE, flags);
        case "value" -> new Num(sup,ATTRIBUTE_VALUE, flags);
        case "modifiers" -> new CreateListElement(sup,ListSuppliers.ATTRIBUTE_MODIFIERS, ATTRIBUTE_MODIFIER);
        default -> null;
    };

    public static final Attributer TEAM_MEMBER = (sup, name, f) -> name.equals("member") ? new Str(sup, DIRECT) : null;

    public static final Attributer TEAM = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, TEAM_NAME);
        case "id" -> new Str(sup, TEAM_ID);
        case "friendly_fire" -> new Bool(sup, TEAM_FRIENDLY_FIRE);
        case "see_friendly_invisibility", "friendly_invis" -> new Bool(sup, TEAM_FRIENDLY_INVIS);
        case "name_tag_visibility", "name_tag" -> new Special(sup, TEAM_NAME_TAG_VISIBILITY);
        case "death_msg_visibility", "death_msg" -> new Special(sup, TEAM_DEATH_MGS_VISIBILITY);
        case "collision" -> new Special(sup, TEAM_COLLISION);
        case "members" -> new CreateListElement(sup,ListSuppliers.TEAM_MEMBERS, TEAM_MEMBER);
        case "online_players", "players" -> new CreateListElement(sup, TEAM_PLAYERS, PLAYER);
        default -> null;
    };

    public static final Attributer SCOREBOARD_OBJECTIVE_SCORE = (sup, name, flags) -> switch (name) {
        case "name", "holder" -> new Str(sup, OBJECTIVE_SCORE_HOLDER);
        case "score", "value" -> new Num(sup, OBJECTIVE_SCORE_VALUE, flags);
        default -> null;
    };


    public static final Attributer SCOREBOARD_OBJECTIVE = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, OBJECTIVE_NAME);
        case "id" -> new Str(sup, OBJECTIVE_ID);
        case "criteria","criterion" -> new Str(sup, OBJECTIVE_CRITIERIA);
        case "display_slot" -> new Str(sup, OBJECTIVE_DISPLAY_SLOT);
        case "scores" -> new CreateListElement(sup, SCOREBOARD_OBJECTIVE_SCORES, SCOREBOARD_OBJECTIVE_SCORE);
        case "online_scores" -> new CreateListElement(sup, SCOREBOARD_OBJECTIVE_SCORES_ONLINE, SCOREBOARD_OBJECTIVE_SCORE);
        default -> null;
    };

    public static final Attributer SCOREBOARD_SCORE = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, SCORES_OBJECTIVE_NAME);
        case "id" -> new Str(sup, SCORES_OBJECTIVE_ID);
        case "criteria","criterion" -> new Str(sup, SCORES_OBJECTIVE_CRITIERIA);
        case "display_slot" -> new Str(sup, SCORES_OBJECTIVE_DISPLAY_SLOT);
        case "score","value" -> new Num(sup, SCORES_VALUE, flags);
        default -> null;
    };

    public static final Attributer BOSSBAR = (sup, name, flags) -> switch (name) {
        case "name" -> new Str(sup, BOSSBAR_NAME);
        case "uuid" -> new Str(sup, BOSSBAR_UUID);
        case "id" -> new Str(sup, BOSSBAR_ID); //SP Only
        case "percent","per","value","" -> new Num(sup, BOSSBAR_PERCENT, flags);
        case "darken_sky" -> new Bool(sup, BOSSBAR_DARKEN_SKY);
        case "dragon_music" -> new Bool(sup, BOSSBAR_DRAGON_MUSIC);
        case "thickens_fog" -> new Bool(sup, BOSSBAR_THICKENS_FOG);
        case "style" -> new Special(sup, BOSSBAR_STYLE);
        case "color_name" -> new Str(sup, BOSSBAR_COLOR_NAME);
        case "enabled", "visible" -> new Bool(sup, BOSSBAR_IS_VISIBLE); //SP Only
        case "players" -> new CreateListElement(sup, BOSSBAR_PLAYERS, PLAYER); //SP Only
        case "icon", "bar" -> new BossbarIcon(sup, flags);
        default -> null;
    };



    public static final Map<ListProvider, Attributer> ATTRIBUTER_MAP = new HashMap<>();
    static {
        ATTRIBUTER_MAP.put(STATUS_EFFECTS, EFFECT);
        ATTRIBUTER_MAP.put(STATUS_EFFECTS_POSITIVE, EFFECT);
        ATTRIBUTER_MAP.put(STATUS_EFFECTS_NEGATIVE, EFFECT);
        ATTRIBUTER_MAP.put(STATUS_EFFECTS_NEUTRAL, EFFECT);
        ATTRIBUTER_MAP.put(ONLINE_PLAYERS, PLAYER);
        ATTRIBUTER_MAP.put(SUBTITLES, SUBTITLE);
        ATTRIBUTER_MAP.put(TARGET_BLOCK_STATES, BLOCK_STATE);
        ATTRIBUTER_MAP.put(TARGET_BLOCK_TAGS, BLOCK_TAG);
        ATTRIBUTER_MAP.put(PLAYER_ATTRIBUTES, ATTRIBUTE);
        ATTRIBUTER_MAP.put(TARGET_ENTITY_ATTRIBUTES, ATTRIBUTE);
        ATTRIBUTER_MAP.put(HOOKED_ENTITY_ATTRIBUTES, ATTRIBUTE);
        ATTRIBUTER_MAP.put(TEAMS, TEAM);
        ATTRIBUTER_MAP.put(INV_ITEMS, ITEM);
        ATTRIBUTER_MAP.put(ARMOR_ITEMS, ITEM);
        ATTRIBUTER_MAP.put(HOTBAR_ITEMS, ITEM);
        ATTRIBUTER_MAP.put(ITEMS, ITEM);
        ATTRIBUTER_MAP.put(SCOREBOARD_OBJECTIVES, SCOREBOARD_OBJECTIVE);
        ATTRIBUTER_MAP.put(PLAYER_SCOREBOARD_SCORES, SCOREBOARD_SCORE);
        ATTRIBUTER_MAP.put(BOSSBARS, BOSSBAR);
        ATTRIBUTER_MAP.put(ALL_BOSSBARS, BOSSBAR);

        // ATTRIBUTER_MAP.put(ATTRIBUTE_MODIFIERS, ATTRIBUTE_MODIFIER);
        // ATTRIBUTER_MAP.put(TEAM_MEMBERS, TEAM_MEMBER);
        // ATTRIBUTER_MAP.put(TEAM_PLAYERS, PLAYER);

        // ITEM_LORE
        // ENCHANTMENT
    }

    public static HudElement get(ListProvider list, Supplier<?> value, String name, Flags flags) {
        Attributer attributer = ATTRIBUTER_MAP.get(list);
        if (attributer == null) {
            System.out.println("[FIX ME]: Attributer not in Map!");
            return null;
        }
        HudElement element = attributer.get(value, name, flags);
        if (element instanceof FuncElements<?> && flags.anyTextUsed())
            return new FormattedElement(element, flags);
        return element;
    }

}
