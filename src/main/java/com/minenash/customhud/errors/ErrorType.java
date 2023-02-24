package com.minenash.customhud.errors;

import java.util.function.Function;

public enum ErrorType {
    TEST (context -> "The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script The bee movie script"),
    IO (context -> "Could not load file: " + context),
    UNKNOWN_VARIABLE ( context -> "Unknown variable"),
    UNKNOWN_VARIABLE_FLAG ( context -> "Unknown variable flag: §e" + context),
    MALFORMED_CONDITIONAL (context -> "Malformed conditional: " + context),

    INVALID_TIME_FORMAT ( context -> "Invalid Time Format: " + context),
    UNKNOWN_STATISTIC ( context -> "Unknown Statistic: §e" + context),
    UNKNOWN_ITEM_ID ( context -> "Unknown Item ID: §e" + context),

    UNKNOWN_SLOT ( context -> "Unknown Slot: §e" + context),
    UNAVAILABLE_SLOT ( context -> "The §e" + context + "§r slot is not available for players"),
    UNKNOWN_ITEM_PROPERTY ( context -> "Unknown Item Property: §e" + context),

    UNKNOWN_SETTING ( context -> "Unknown Setting: §e" + context),
    UNKNOWN_KEYBOARD_KEY ( context -> "Unknown Keyboard Key: §e" + context),
    UNKNOWN_SOUND_CATEGORY ( context -> "Unknown Sound Category: §e" + context),

    CONDITIONAL_UNEXPECTED_VALUE (context -> "Unexpected Value: §e" + context),
    CONDITIONAL_WRONG_NUMBER_OF_TOKENS (context -> "Found §e" + context + "§r tokens instead of 3");

    final Function<String,String> message;

    ErrorType(Function<String,String> msg) {
        message = msg;
    }
}
