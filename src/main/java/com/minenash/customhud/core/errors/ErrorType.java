package com.minenash.customhud.core.errors;

import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class ErrorType {
    public static final ErrorType NONE = internal("", null, "Good Job!");
    public static final ErrorType HEADER = internal("Help", null, "Details");
    public static final ErrorType TEST = internal("Uhhh", "", "The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script");
    public static final ErrorType IO = internal("Help", "../help", "Could not load file: §c");

    public static final ErrorType UNKNOWN_VARIABLE = internal("Variables", "variables", "Unknown Variable: §e");
    public static final ErrorType UNKNOWN_VARIABLE_FLAG = internal("Variable Flags", "references/variable_flags", "Unknown Variable Flag: §e");
    public static final ErrorType UNKNOWN_THEME_FLAG = internal("Theming", "references/theming", "Unknown Theme Option or Value");
    public static final ErrorType UNKNOWN_COLOR = internal("Theming", "references/theming", "Unknown Color: §e");
    public static final ErrorType UNKNOWN_CROSSHAIR = internal("Theming", "references/theming", "Unknown Crosshair: §e");
    public static final ErrorType ILLEGAL_GLOBAL_THEME_FLAG = internal("Theming", "references/theming", "This theme option is global-only");
    public static final ErrorType INVALID_TIME_FORMAT = internal("Time Formatting", "references/real_time", "Invalid Time Format: ");
    public static final ErrorType INVALID_KEY = internal("Input for Toggle", "references/", "Invalid Key or Scan Code: ");
    public static final ErrorType UNKNOWN_STATISTIC = internal("Statistics", "references/stats", "Unknown Statistic: §e");
    public static final ErrorType UNKNOWN_ITEM_ID = internal(null, null, "Unknown Item ID: §e");
    
    public static final ErrorType UNKNOWN_SLOT = internal("Slots", "references/item_slots", "Unknown Slot: §e");
    public static final ErrorType UNAVAILABLE_SLOT = internal("Slots", "references/item_slots", "The §e" + "§r slot is not available for players");
    public static final ErrorType UNKNOWN_ITEM_PROPERTY = internal("Item Properties", "variables#items", "Unknown Item Property: §e");
    
    public static final ErrorType UNKNOWN_ICON = internal("Icons", "references/icons", "Unknown item/texture: §e");
    public static final ErrorType UNKNOWN_SETTING = internal("Settings", "references/settings", "Unknown Setting: §e");
    public static final ErrorType UNKNOWN_KEYBOARD_KEY = internal("Settings", "references/settings", "Unknown Keyboard Key: §e");
    public static final ErrorType UNKNOWN_SOUND_CATEGORY = internal("Settings", "references/settings", "Unknown Sound Category: §e");
    
    public static final ErrorType CONDITIONAL_NOT_STARTED = internal("Conditionals", "conditionals", "No =if: §ocond§r= to ");
    public static final ErrorType CONDITIONAL_NOT_ENDED = internal("Conditionals", "conditionals", "Missing =endif=");
    public static final ErrorType MALFORMED_CONDITIONAL = internal("Conditionals", "conditionals", "Malformed conditional: ");
    public static final ErrorType CONDITIONAL_UNEXPECTED_VALUE = internal("Conditionals", "conditionals", "Unexpected Value: §e");
    public static final ErrorType CONDITIONAL_WRONG_NUMBER_OF_TOKENS = internal("Conditionals", "conditionals", "Expected 4 tokens, found §e");

    public static final ErrorType NOT_A_WHOLE_NUMBER = internal(null, null, "Not a whole number: ");

    public final String message;
    public final String linkText;
    public final String link;

    public ErrorType(String linkText, String link, String msg) {
        this.message = msg;
        this.linkText = linkText == null ? null : "§b§n" + linkText;
        this.link = "https://customhud.dev/v3/" + link;
    }
    
    private static ErrorType internal(String linkText, String link, String msg) {
        return new ErrorType(linkText, "https://customhud.dev/v3/" + link, msg);
    }
}
