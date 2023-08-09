package com.minenash.customhud.core.editor;

import javax.swing.text.*;
import java.util.Arrays;

import static com.minenash.customhud.core.editor.LineParseContext.*;

public enum CompletionContext {
    NONE,
    START,
    NORMAL,
    COLOR,
    VARIABLE,
    FLAGS,
    CONDITIONAL,
    LOCAL_THEME_KEY,
    GLOBAL_THEME_KEY,
    THEME_VALUE;

    public static CompletionContext getContextAtCursor(JTextComponent comp, Segment seg) {
        Document doc = comp.getDocument();
        int dot = comp.getCaretPosition();
        Element root = doc.getDefaultRootElement();
        int start = root.getElement(root.getElementIndex(dot)).getStartOffset();

        char[] chars;
        try {
            doc.getText(start, dot-start, seg);
            chars = Arrays.copyOfRange(seg.array, start, dot);
        } catch (BadLocationException ble) {
            ble.printStackTrace();
            return NONE;
        }

        boolean global = false;

        LineParseContext context;

        if (chars.length == 0) {
            System.out.println("Parse Context: ANY");
            return START;
        }
        else if (chars.length >= 2 && chars[0] == '/' && chars[1] == '/')
            context = LineParseContext.COMMENT;
        else if (chars[0] == '=') {
            context = THEME_PREFIX;
            for (int i = 1; i < chars.length; i++) {

                if (context == THEME_PREFIX) {
                    if (chars[i] != '=') context = THEME_KEY;
                    else global = true;
                }
                if ((context == THEME_KEY || context == LineParseContext.THEME_VALUE || context == CONDITIONAL_PHRASE) && chars[i] == '=') {
                    context = THEME_POSTFIX;
                }
                if (context == THEME_KEY && chars[i] == ':') {
                    String left = new String(Arrays.copyOfRange(chars, 1, i));
                    context = left.equalsIgnoreCase("if") || left.equalsIgnoreCase("elseif") ? CONDITIONAL_PHRASE : LineParseContext.THEME_VALUE;
                }
            }

        }
        else {
            context = LineParseContext.NORMAL;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '&' && i+1 == chars.length )
                    context = LineParseContext.COLOR;

                if (chars[i] == '{' && !(i+1 < chars.length && chars[i+1] == '{'))
                    context = VARIABLE_INNER;

                if (context == VARIABLE_INNER && chars[i] == ' ')
                    context = VARIABLE_FLAGS;

                if (chars[i] == '{' && i+1 < chars.length && chars[i+1] == '{') {
                    i++;
                    context = CONDITIONAL_PHRASE;
                }

                if (context == CONDITIONAL_PHRASE && chars[i] == ',')
                    context = CONDITIONAL_SYNTAX;

                if (context == CONDITIONAL_INNER && chars[i] == '"')
                    context = CONDITIONAL_PHRASE;

                if (context == CONDITIONAL_SYNTAX && chars[i] == '"')
                    context = CONDITIONAL_INNER;

                if (chars[i] == '}') {
                    if (i+1 < chars.length && chars[i+1] == '}')
                        i++;
                    if (context != CONDITIONAL_SYNTAX)
                        context = LineParseContext.NORMAL;
                }
            }

        }
        System.out.println("Context: " + context);

        CompletionContext context1 = switch (context) {
            case NORMAL -> NORMAL;
            case COLOR -> COLOR;
            case VARIABLE_INNER, CONDITIONAL_INNER -> VARIABLE;
            case VARIABLE_FLAGS -> FLAGS;
            case CONDITIONAL_PHRASE -> CONDITIONAL;
            case THEME_PREFIX, THEME_KEY -> global ? GLOBAL_THEME_KEY : LOCAL_THEME_KEY;
            case THEME_VALUE -> THEME_VALUE;
            default -> NONE;
        };
        System.out.println("Parse Context: " + context1);

        return context1;

    }
}
