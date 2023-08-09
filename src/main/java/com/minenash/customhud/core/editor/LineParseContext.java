package com.minenash.customhud.core.editor;

public enum LineParseContext {
    NORMAL,
    VARIABLE_INNER,
    VARIABLE_FLAGS,
    CONDITIONAL_PHRASE,
    CONDITIONAL_SYNTAX,
    CONDITIONAL_INNER,

    THEME_PREFIX,
    THEME_KEY,
    THEME_VALUE,
    THEME_POSTFIX,

    ERROR,
    COMMENT,
    COLOR;

}
