package com.minenash.customhud.data;

import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HudTheme {
    public int bgColor = 0x44000000;
    public int fgColor = 0xffffffff;
    public int lineSpacing = 2;
    public float scale = 1;
    public Identifier font = null;
    public boolean textShadow = true;

    private HudTheme(){}

    public static HudTheme defaults() {
        return new HudTheme();
    }

    public HudTheme copy() {
        HudTheme newTheme = new HudTheme();
        newTheme.bgColor = bgColor;
        newTheme.fgColor = fgColor;
        newTheme.lineSpacing = lineSpacing;
        newTheme.scale = scale;
        newTheme.font = font;
        newTheme.textShadow = textShadow;
        return newTheme;
    }


    private static final Pattern SPACING_FLAG_PATTERN = Pattern.compile("linespacing: ?([-+]?\\d+)");
    private static final Pattern SCALE_FLAG_PATTERN = Pattern.compile("scale: ?(\\d+.?\\d*|.?\\d+)");
    private static final Pattern COLOR_FLAG_PATTERN = Pattern.compile("(back|fore)groundcolou?r: ?(0x|#)?([0-9a-fA-F]+|none)");
    private static final Pattern FONT_FLAG_PATTERN = Pattern.compile("font: ?(\\w*:?\\w+)");
    private static final Pattern TEXT_SHADOW_FLAG_PATTERN = Pattern.compile("textshadow: ?(true|false)");

    public boolean parse(boolean global, String line) {
        line = line.toLowerCase();
        Matcher matcher = COLOR_FLAG_PATTERN.matcher(line);
        if (matcher.matches())
            if (matcher.group(1).equals("fore"))
                fgColor = parseHexNumber(matcher.group(3));
            else
                bgColor = parseHexNumber(matcher.group(3));

        else if (( matcher = SPACING_FLAG_PATTERN.matcher(line) ).matches())
            lineSpacing = Integer.parseInt(matcher.group(1));

        else if (global && (  matcher = SCALE_FLAG_PATTERN.matcher(line) ).matches())
            scale = Float.parseFloat(matcher.group(1));

        else if (( matcher = FONT_FLAG_PATTERN.matcher(line) ).matches())
            font = new Identifier(matcher.group(1));

        else if (( matcher = TEXT_SHADOW_FLAG_PATTERN.matcher(line) ).matches())
            textShadow = Boolean.parseBoolean(matcher.group(1));

        else
            return false;

        return true;
    }

    public static int parseHexNumber(String str) {
        if (str.equals("none"))
            return 0;

        str = switch (str.length()) {
            case 3 -> "FF" + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2);
            case 4 -> "" + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2) + str.charAt(3) + str.charAt(3);
            case 5 -> "" + str.charAt(0) + str.charAt(0) + str.substring(1);
            case 6 -> "FF" + str;
            default -> str;
        };

        long color = Long.parseLong(str,16);
        return (int) (color >= 0x100000000L ? color - 0x100000000L : color);
    }


}
