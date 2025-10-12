package com.minenash.customhud.errors;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

//TODO: Add WIP with update docs
public enum ErrorType {
    NONE ("", null, "Good Job!"),
    HEADER ("Help", null, "Details"),
    TEST ("Uhhh", "", "The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script"),
    IO ("Help", "../help", "Could not load file: §c"),

    VARIABLE_ERROR ("Report", "https://github.com/Minenash/CustomHUD/issues", "An error has accord trying to parse this variable. " +
            "Please report this along with log. You might want to double check your profile though", true),
    PROFILE_ERROR ("Report", "https://github.com/Minenash/CustomHUD/issues", "A fatal error has accord trying to parse this profile. " +
            "Please report this along with log. You might want to double check your profile though", true),

    EMPTY_VARIABLE ("Variables", "variables", "No Variable Given"),
    UNKNOWN_VARIABLE ("Variables", "variables", "Unknown Variable: §e"),
    UNKNOWN_LIST ("WIP", "references/v4_wip", "Unknown List Variable: §e"),
    UNKNOWN_VARIABLE_FLAG ("Variable Flags", "references/variable_flags", "Unknown Variable Flag: §e"),
    RADIX_TOO_BIG ("Variable Flags", "references/variable_flags", "Radix can't be above 36"),
    RADIX_TOO_SMALL ("Variable Flags", "references/variable_flags", "Radix can't be below 2"),
    UNKNOWN_LIST_VARIABLE_FLAG ("Variable Flags", "references/variable_flags", "Unknown List Variable Flag: §e"),
    UNKNOWN_THEME_FLAG ("Theming", "references/theming", "Unknown Theme Option or Value"),
    UNKNOWN_COLOR ("Theming", "references/theming", "Unknown Color: §e"),
    UNKNOWN_CROSSHAIR ("Theming", "references/theming", "Unknown Crosshair: §e"),
    UNKNOWN_CHART ("Theming", "references/theming", "Unknown Debug Chart: §e"),
    UNKNOWN_HUD_ELEMENT ("WIP", "references/v4_wip", "Unknown Vanilla Hud Element: §e"),
    ILLEGAL_GLOBAL_THEME_FLAG("Theming", "references/theming", "This theme option is global-only"),
    LINE_SPACING_AND_PADDING ("Theming", "references/theming", "LineSpacing can't be used with Padding, adjust padding instead"),
    ZERO_SCALE ("Theming", "references/theming", "Scale can not be 0"),

    INVALID_TIME_FORMAT ("Time Formatting", "references/real_time", "Invalid Time Format: "),
    UNKNOWN_STATISTIC ("Statistics", "references/stats", "Unknown Statistic: §e"),
    UNKNOWN_ITEM_ID (null, null, "Unknown Item ID: §e"),

    UNKNOWN_SLOT ("Slots", "references/item_slots", "Unknown Slot: §e"),
    UNAVAILABLE_SLOT ("Slots", "references/item_slots", "The §e" + "§r slot is not available for players"),
    UNKNOWN_MOD ("WIP", "references/v4_wip", "Mod not installed: §e"),
    UNKNOWN_RESOURCE_PACK ("WIP", "references/v4_wip", "Resource Pack not installed: §e"),
    UNKNOWN_DATA_PACK ("WIP", "references/v4_wip", "Datapack not installed: §e"),

    UNKNOWN_ATTRIBUTE ("WIP", "references/v4_wip", "Unknown Attribute: §e"),
    UNKNOWN_EFFECT_ID ("WIP", "references/v4_wip", "Unknown Effect ID: §e"),
    UNKNOWN_ITEM_METHOD("Item Properties", "variables#items", "Unknown Item Method: §e"),
    UNKNOWN_ATTRIBUTE_METHOD("WIP", "references/v4_wip", "Unknown Attribute Method: §e"),
    UNKNOWN_BLOCK_PROPERTY_METHOD("WIP", "references/v4_wip", "Unknown Blockstate Property Method: §e"),
    UNKNOWN_EFFECT_METHOD("WIP", "references/v4_wip", "Unknown Effect Method: §e"),
    UNKNOWN_TEAM_METHOD("WIP", "references/v4_wip", "Unknown Team Method: §e"),
    UNKNOWN_PLAYER_METHOD("WIP", "references/v4_wip", "Unknown Player Method: §e"),
    UNKNOWN_OBJECTIVE_METHOD("WIP", "references/v4_wip", "Unknown Objective Method: §e"),
    UNKNOWN_BOSSBAR_METHOD("WIP", "references/v4_wip", "Unknown Bossbar Method: §e"),
    UNKNOWN_SCORE_METHOD("WIP", "references/v4_wip", "Unknown Score Method: §e"),
    UNKNOWN_MOD_METHOD("WIP", "references/v4_wip", "Unknown Mod Method: §e"),
    UNKNOWN_PACK_METHOD("WIP", "references/v4_wip", "Unknown Pack Method: §e"),
    UNKNOWN_PROFILER_TIMING_PROPERTY("WIP", "references/v4_wip", "Unknown Profiler Time Property: §e"),
    UNKNOWN_ICON ("Icons", "references/icons", "Unknown item/texture: §e"),

    UNKNOWN_SETTING ("Settings", "references/settings", "Unknown Setting: §e"),
    UNKNOWN_KEYBIND("Settings", "references/settings", "Unknown Keybind: §e"),
    UNKNOWN_SOUND_CATEGORY ("Settings", "references/settings", "Unknown Sound Category: §e"),

    LIST_NOT_STARTED ("WIP", "references/v4_wip", "No =for: §olist§r= to end"),
    LIST_NOT_ENDED ("WIP", "references/v4_wip", "Missing =endfor="),
    CONDITIONAL_NOT_STARTED ("Conditionals", "conditionals", "No =if: §ocond§r= to "),
    CONDITIONAL_NOT_ENDED ("Conditionals", "conditionals", "Missing =endif="),
    CONDITIONAL_UNEXPECTED_VALUE ("Conditionals", "conditionals", "Unexpected Value: §e"),
    CONDITIONAL_WRONG_NUMBER_OF_TOKENS ("Conditionals", "conditionals", "Tokens"),
    MALFORMED_CONDITIONAL ("Conditionals", "conditionals", "Malformed conditional: §e"),
    EMPTY_SECTION("Conditionals", "conditionals", "Empty section"),
    MALFORMED_LIST ("WIP", "references/v4_wip", "Malformed list variable: §e"),
    MALFORMED_BAR ("WIP", "references/v4_wip", "Malformed bar variable: §e"),
    MALFORMED_LOOP ("WIP", "references/v4_wip", "Malformed loop: §e"),
    MALFORMED_TIMER ("WIP", "references/v4_wip", "Malformed timer: §e"),
    MALFORMED_VELOCITY ("WIP", "references/v4_wip", "Invalid velocity option(s): §e"),
    MALFORMED_VELOCITY_SMOOTHING ("WIP", "references/v4_wip", "Malformed velocity smoothing: §e"),
    EMPTY_TOGGLE ("WIP", "references/v4_wip", "No toggle name"),
    UNKNOWN_KEY("WIP", "references/v4_wip", "Invalid key name: §e"),
    GIZMO_NO_ROTATE("WIP", "references/v4_wip", "Gizmo can't be rotated"),
    GIZMO_NO_WORK("WIP", "references/v4_wip", "Due to changes in 1.21.6, {gizmo} no longer works"),

    NOT_A_WHOLE_NUMBER (null, null, "Not a whole number: §e"),

    REQUIRES_MODMENU ("Get Mod Menu", "https://modrinth.com/mod/modmenu", "Requires the mod §aMod Menu");

    public final String message;
    public final MutableText linkText;
    public final String link;

    ErrorType(String linkText, String link, String msg) {
        this.message = msg;
        this.linkText = linkText == null ? null : Text.literal(linkText).formatted(Formatting.AQUA, Formatting.UNDERLINE);
        this.link = "https://customhud.dev/v4/" + link;
    }
    ErrorType(String linkText, String link, String msg, boolean directLink) {
        this.message = msg;
        this.linkText = linkText == null ? null : Text.literal(linkText).formatted(Formatting.AQUA, Formatting.UNDERLINE);
        this.link = directLink ? link : "https://customhud.dev/v4/" + link;
    }
}
