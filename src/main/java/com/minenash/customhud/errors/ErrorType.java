package com.minenash.customhud.errors;

import java.util.function.Function;

public enum ErrorType {
    IO (true, context -> "Could not load file: " + context),
    UNKNOWN_VARIABLE (false, context -> "Unknown variable"),
    UNKNOWN_VARIABLE_FLAG (false, context -> "Unknown variable flag: §e" + context),
    MALFORMED_CONDITIONAL (true, context -> "Malformed conditional: " + context),

    INVALID_TIME_FORMAT (false, context -> "Invalid Time Format: " + context),
    UNKNOWN_STATISTIC (false, context -> "Unknown Statistic: §e" + context),
    UNKNOWN_ITEM_ID (false, context -> "Unknown Item ID: §e" + context),

    UNKNOWN_SLOT (false, context -> "Unknown Slot: §e" + context),
    UNAVAILABLE_SLOT (false, context -> "The §e" + context + "§r slot is not available for players"),
    UNKNOWN_ITEM_PROPERTY (false, context -> "Unknown Item Property: §e" + context),

    UNKNOWN_SETTING (false, context -> "Unknown Setting: §e" + context),
    UNKNOWN_KEYBOARD_KEY (false, context -> "Unknown Keyboard Key: §e" + context),
    UNKNOWN_SOUND_CATEGORY (false, context -> "Unknown Sound Category: §e" + context),

    CONDITIONAL_UNEXPECTED_VALUE (true, context -> "Unexpected Value: §e" + context),
    CONDITIONAL_WRONG_NUMBER_OF_TOKENS (true, context -> "Found §e" + context + "§r tokens instead of 3");

    final Function<String,String> message;
    final boolean multiLine;

    ErrorType(boolean multi, Function<String,String> msg) {
        message = msg;
        multiLine = multi;
    }
}
