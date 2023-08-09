package com.minenash.customhud.core.editor;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;

import java.awt.*;

public class CToken {

    public static final int NORMAL = 1;
    public static final int COMMENT = 2;
    public static final int THEME_KEY = 3;
    public static final int THEME_VALUE = 4;
    public static final int CONDITIONAL = 5;
    public static final int VARIABLE = 6;
    public static final int FLAG = 7;
    public static final int COLOR = 8;

    public static int SYNTAX = 22;

    public static final SyntaxScheme style = new SyntaxScheme(false);
    static {
        style.setStyle(NORMAL,      new Style());
        style.setStyle(COMMENT,     new Style(new Color(127,127,127), null, RSyntaxTextArea.getDefaultFont().deriveFont(Font.ITALIC)));
        style.setStyle(THEME_KEY,   new Style(new Color(0,95,0)));
        style.setStyle(THEME_VALUE, new Style(new Color(0,127,0)));
        style.setStyle(CONDITIONAL, new Style(new Color(0,0,191)));
        style.setStyle(VARIABLE,    new Style(new Color(127, 0, 127)));
        style.setStyle(FLAG,        new Style(new Color(159,0,159)));
        style.setStyle(COLOR,       new Style(new Color(191,63,0)));
        style.setStyle(SYNTAX,      new Style(new Color(127,127,127)));
    }
}
