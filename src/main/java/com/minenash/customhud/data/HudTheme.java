package com.minenash.customhud.data;

import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HudTheme {
    public int bgColor = 0x44000000;
    public CHFormatting fgColor = new CHFormatting().color(0xffffffff,0xffffffff);
    public int lineSpacing = 2;
    public float scale = 1;
    public Identifier font = null;
    public boolean textShadow = true;

    public Vec3i rotation = new Vec3i(0,0,0);
    public Vec3i translation = new Vec3i(0,0,0);

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
    private static final Pattern COLOR_FLAG_PATTERN_STR = Pattern.compile("(back|fore)groundcolou?r: ?(.*)");
    private static final Pattern FONT_FLAG_PATTERN = Pattern.compile("font: ?(\\w*:?\\w+)");
    private static final Pattern TEXT_SHADOW_FLAG_PATTERN = Pattern.compile("textshadow: ?(true|false)");
    private static final Pattern ROTATION_FLAG_PATTERN = Pattern.compile("rotate: ?(-?\\d+), ?(-?\\d+), ?(-?\\d+)");
    private static final Pattern TRANSLATE_FLAG_PATTERN = Pattern.compile("translate: ?(-?\\d+), ?(-?\\d+), ?(-?\\d+)");

    public boolean parse(boolean global, String line, int profileID, int lineNum) {
        line = line.toLowerCase();
        Matcher matcher = COLOR_FLAG_PATTERN.matcher(line);
        if (matcher.matches())
            if (matcher.group(1).equals("fore"))
                fgColor = parseHexNumber(matcher.group(3), false);
            else
                bgColor = parseHexNumber(matcher.group(3), true);

        else if (( matcher = COLOR_FLAG_PATTERN_STR.matcher(line) ).matches()) {
            Integer color = parseColorName(matcher.group(2).trim());
            if (color == null) {
                Errors.addError(profileID, lineNum, line, ErrorType.UNKNOWN_THEME_FLAG, matcher.group(2));
                return true; //Not Really, but I add the error here
            }
            if (matcher.group(1).equals("fore"))
                fgColor = 0xFF000000 + color;
            else
                bgColor = 0x44000000 + color;
        }

        else if (( matcher = SPACING_FLAG_PATTERN.matcher(line) ).matches())
            lineSpacing = Integer.parseInt(matcher.group(1));

        else if (global && (  matcher = SCALE_FLAG_PATTERN.matcher(line) ).matches())
            scale = Float.parseFloat(matcher.group(1));

        else if (global && (  matcher = ROTATION_FLAG_PATTERN.matcher(line) ).matches())
            rotation = new Vec3i(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)));
        else if (global && (  matcher = TRANSLATE_FLAG_PATTERN.matcher(line) ).matches())
            translation = new Vec3i(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)));

        else if (( matcher = FONT_FLAG_PATTERN.matcher(line) ).matches())
            font = new Identifier(matcher.group(1));

        else if (( matcher = TEXT_SHADOW_FLAG_PATTERN.matcher(line) ).matches())
            textShadow = Boolean.parseBoolean(matcher.group(1));

        else
            return false;

        return true;
    }

    public static int parseHexNumber(String str, boolean forBg) {
        if (str.equals("none"))
            return 0;

        str = switch (str.length()) {
            case 3 -> (forBg? "44" : "ff") + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2);
            case 4 -> "" + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2) + str.charAt(3) + str.charAt(3);
            case 5 -> "" + str.charAt(0) + str.charAt(0) + str.substring(1);
            case 6 -> (forBg? "44" : "ff") + str;
            default -> str;
        };

        long color = Long.parseLong(str,16);
        return (int) (color >= 0x100000000L ? color - 0x100000000L : color);
    }

    public static CHFormatting parseColorName(String str) {
        int color = switch (str) {
            case "black"       -> 0x000000;
            case "dark_blue"   -> 0x0000AA;
            case "dark_green"  -> 0x00AA00;
            case "dark_aqua"   -> 0x00AAAA;
            case "dark_red"    -> 0xAA0000;
            case "dark_purple" -> 0xAA00AA;
            case "gold", "orange" -> 0xFFAA00;
            case "gray"        -> 0xAAAAAA;
            case "dark_gray"   -> 0x555555;
            case "blue"        -> 0x5555FF;
            case "green"       -> 0x55FF55;
            case "aqua"        -> 0x55FFFF;
            case "red"         -> 0xFF5555;
            case "light_purple", "purple" -> 0xFF55FF;
            case "yellow"      -> 0xFFFF55;
            case "white"       -> 0xFFFFFF;
            //Bedrock:
            case "minecoin_gold",      "minecoin"  -> 0xDDD605;
            case "material_quartz",    "quartz"    -> 0xE3D4D1;
            case "material_iron",      "iron"      -> 0xCECACA;
            case "material_netherite", "netherite" -> 0x443A3B;
            case "material_redstone",  "redstone"  -> 0x971607;
            case "material_copper",    "copper"    -> 0xB4684D;
            case "material_gold",      "mgold"     -> 0xDEB12D;
            case "material_emerald",   "emerald"   -> 0x47A036;
            case "material_diamond",   "diamond"   -> 0x2CBAA8;
            case "material_lapis",     "lapis"     -> 0x21497B;
            case "material_amethyst",  "amethyst"  -> 0x9A5CC6;
            default -> 0xFF000000;
        };
    }

    public static Integer parseColorCode(String str) {
        return switch (str) {
            case "0" -> 0x000000;
            case "1" -> 0x0000AA;
            case "2" -> 0x00AA00;
            case "3" -> 0x00AAAA;
            case "4" -> 0xAA0000;
            case "5" -> 0xAA00AA;
            case "6" -> 0xFFAA00;
            case "7" -> 0xAAAAAA;
            case "8" -> 0x555555;
            case "9" -> 0x5555FF;
            case "a" -> 0x55FF55;
            case "b" -> 0x55FFFF;
            case "c" -> 0xFF5555;
            case "d" -> 0xFF55FF;
            case "e" -> 0xFFFF55;
            case "f" -> 0xFFFFFF;
            //Bedrock:
            case "g" -> 0xDDD605;
            case "h" -> 0xE3D4D1;
            case "i" -> 0xCECACA;
            case "j" -> 0x443A3B;
            case "zm" -> 0x971607;
            case "zn" -> 0xB4684D;
            case "p" -> 0xDEB12D;
            case "q" -> 0x47A036;
            case "s" -> 0x2CBAA8;
            case "t" -> 0x21497B;
            case "u" -> 0x9A5CC6;
            default -> null;
        };
    }


}
