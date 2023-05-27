package com.minenash.customhud.errors;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public enum ErrorType {
    NONE ("", null, "Good Job!"),
    HEADER ("Help", null, "Details"),
    TEST ("Uhhh", "", "The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script"),
    IO ("Help", "../help", "Could not load file: §c"),
    UNKNOWN_VARIABLE ("Variables", "variables", "Unknown Variable: §e"),
    UNKNOWN_VARIABLE_FLAG ("Variable Flags", "references/variable_flags", "Unknown Variable Flag: §e"),
    UNKNOWN_THEME_FLAG ("Theming", "references/theming", "Unknown Theme Option or Value"),
    UNKNOWN_COLOR ("Theming", "references/theming", "Unknown Color: §e"),
    ILLEGAL_GLOBAL_THEME_FLAG("Theming", "references/theming", "This theme option is global-only"),

    INVALID_TIME_FORMAT ("Time Formatting", "references/real_time", "Invalid Time Format: "),
    UNKNOWN_STATISTIC ("Statistics", "references/stats", "Unknown Statistic: §e"),
    UNKNOWN_ITEM_ID (null, null, "Unknown Item ID: §e"),

    UNKNOWN_SLOT ("Slots", "references/item_slots", "Unknown Slot: §e"),
    UNAVAILABLE_SLOT ("Slots", "references/item_slots", "The §e" + "§r slot is not available for players"),
    UNKNOWN_ITEM_PROPERTY ("Item Properties", "variables#items", "Unknown Item Property: §e"),

    UNKNOWN_ICON ("Icons", "references/icons", "Unknown item/texture: §e"),

    UNKNOWN_SETTING ("Settings", "references/settings", "Unknown Setting: §e"),
    UNKNOWN_KEYBOARD_KEY ("Settings", "references/settings", "Unknown Keyboard Key: §e"),
    UNKNOWN_SOUND_CATEGORY ("Settings", "references/settings", "Unknown Sound Category: §e"),

    CONDITIONAL_NOT_STARTED ("Conditionals", "conditionals", "No =if: §ocond§r= to "),
    CONDITIONAL_NOT_ENDED ("Conditionals", "conditionals", "Missing =endif="),
    MALFORMED_CONDITIONAL ("Conditionals", "conditionals", "Malformed conditional: "),
    CONDITIONAL_UNEXPECTED_VALUE ("Conditionals", "conditionals", "Unexpected Value: §e"),
    CONDITIONAL_WRONG_NUMBER_OF_TOKENS ("Conditionals", "conditionals", "Expected 4 tokens, found §e"),

    NOT_A_WHOLE_NUMBER (null, null, "Not a whole number: ");

    final String message;
    final MutableText linkText;
    final String link;

    ErrorType(String linkText, String link, String msg) {
        this.message = msg;
        this.linkText = linkText == null ? null : Text.literal(linkText).formatted(Formatting.AQUA, Formatting.UNDERLINE);
        this.link = "https://customhud.dev/v3/" + link;
    }
}
