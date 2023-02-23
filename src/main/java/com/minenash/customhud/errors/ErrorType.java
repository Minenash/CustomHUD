package com.minenash.customhud.errors;

import java.util.function.Function;

public enum ErrorType {
    IO (context -> "Could not load file: " + context),
    UNKNOWN_VARIABLE (context -> "Unknown variable"),
    UNKNOWN_VARIABLE_FLAG (context -> "Unknown variable flag: " + context),
    MALFORMED_CONDITIONAL (context -> "Malformed conditional: " + context),

    INVALID_TIME_FORMAT (context -> ""),
    UNKNOWN_STATISTIC (context -> ""),
    UNKNOWN_ITEM_ID (context -> ""),

    UNKNOWN_SLOT (context -> ""),
    UNAVAILABLE_SLOT (context -> ""),
    UNKNOWN_ITEM_PROPERTY (context -> ""),

    UNKNOWN_SETTING (context -> ""),
    UNKNOWN_KEYBOARD_KEY (context -> ""),
    UNKNOWN_SOUND_CATEGORY (context -> ""),

    CONDITIONAL_UNEXPECTED_VALUE (context -> ""),
    CONDITIONAL_WRONG_NUMBER_OF_TOKENS (context -> "");

    Function<String,String> message;

    ErrorType() {}

    ErrorType(Function<String,String> msg) {
        message = msg;
    }
}
