package com.minenash.customhud.data;

import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.util.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.minenash.customhud.CustomHud.CLIENT;

public class HudTheme {
    public enum ScaleMethod {DIRECT, GUI, RELATIVE_GUI}
    public record Padding(int top, int bottom, int left, int right) {}

    public int bgColor = 0x44000000;
    public CHFormatting fgColor = new CHFormatting().color(0xffffffff,0xffffffff);
    public int lineSpacing = 2;
    public StyleSpriteSource font = null;
    public boolean textShadow = true;
    public boolean persistentFormatting = false;

    private float scale = 1;
    private ScaleMethod scaleMethod = ScaleMethod.DIRECT;
    public Integer hudScale = null;
    public boolean hudScaleRelative = false;
    public Padding padding = new Padding(2, 1, 2, 2);

    private boolean lineSpaced = false;
    private boolean padded = false;

    public boolean convertLineBreaks = true;
    public boolean ignoreBlankLines = false;
    public boolean ignoreLeadingSpace = false;
    public boolean fitItemIconsToLine = true;

    private HudTheme(){}

    public static HudTheme defaults() {
        return new HudTheme();
    }

    public HudTheme copy() {
        HudTheme newTheme = new HudTheme();
        newTheme.bgColor = bgColor;
        newTheme.fgColor = fgColor;
        newTheme.lineSpacing = lineSpacing;
        newTheme.font = font;
        newTheme.textShadow = textShadow;
        newTheme.scale = scale;
        newTheme.scaleMethod = scaleMethod;
        newTheme.hudScale = hudScale;
        newTheme.hudScaleRelative = hudScaleRelative;
        newTheme.padding = padding;

        newTheme.lineSpaced = lineSpaced;
        newTheme.padded = padded;

        newTheme.convertLineBreaks = convertLineBreaks;
        newTheme.ignoreBlankLines = ignoreBlankLines;
        newTheme.ignoreLeadingSpace = ignoreLeadingSpace;
        newTheme.persistentFormatting = persistentFormatting;
        newTheme.fitItemIconsToLine = fitItemIconsToLine;

        return newTheme;
    }


    private static final Pattern SPACING_FLAG_PATTERN = Pattern.compile("linespacing: ?([-+]?\\d+)");
    private static final Pattern SCALE_FLAG_PATTERN = Pattern.compile("scale: ?(\\d+.?\\d*|.?\\d+)");
    private static final Pattern NICESCALE_FLAG_PATTERN = Pattern.compile("nicescale: ?(\\+)? ?(-?\\d+)");
    private static final Pattern HUDSCALE_FLAG_PATTERN = Pattern.compile("hudscale: ?(\\+)? ?(-?\\d+)");
    private static final Pattern COLOR_FLAG_PATTERN = Pattern.compile("(back|fore)groundcolou?r: ?(0x|#)?([0-9a-fA-F]+|none)");
    private static final Pattern COLOR_FLAG_PATTERN_STR = Pattern.compile("(back|fore)groundcolou?r: ?(.*)");
    private static final Pattern PERSISTEMT_FORMATTING_FLAG_PATTERN = Pattern.compile("persistentformatting: ?(true|false)");
    private static final Pattern FONT_FLAG_PATTERN = Pattern.compile("font: ?(\\w*:?\\w+)");
    private static final Pattern TEXT_SHADOW_FLAG_PATTERN = Pattern.compile("textshadow: ?(true|false)");
    private static final Pattern ROTATION_FLAG_PATTERN = Pattern.compile("rotate: ?(-?\\d+), ?(-?\\d+), ?(-?\\d+)");
    private static final Pattern TRANSLATE_FLAG_PATTERN = Pattern.compile("translate: ?(-?\\d+), ?(-?\\d+), ?(-?\\d+)");
    private static final Pattern PADDING_FLAG_PATTERN = Pattern.compile("padding: ?(-?\\d+)?(, ?(-?\\d+)?)?(, ?(-?\\d+)?)?(, ?(-?\\d+)?)?");
    private static final Pattern ITEM_ICON_SIZE = Pattern.compile("itemiconsize?: ?(line|full)");

    private static final Pattern CONVERT_LINE_BREAKS_PATTERN = Pattern.compile("convertlinebreaks?: ?(true|false)");
    private static final Pattern IGNORE_BLANK_LINES_PATTERN = Pattern.compile("ignoreblanklines?: ?(true|false)");
    private static final Pattern IGNORE_LEADING_SPACES = Pattern.compile("ignoreleadingspaces?: ?(true|false)");

    public boolean parse(boolean global, String line, String profileName, int lineNum) {
        line = line.toLowerCase();
        Matcher matcher = COLOR_FLAG_PATTERN.matcher(line);
        if (matcher.matches() && matcher.group(1).length() <= 8)
            if (matcher.group(1).equals("fore"))
                fgColor.apply(parseHexNumber(matcher.group(3)), this);
            else
                bgColor = new CHFormatting().color(bgColor, 0xFFFFFFFF).apply(parseHexNumber(matcher.group(3)), this).getColor();

        else if (( matcher = COLOR_FLAG_PATTERN_STR.matcher(line) ).matches()) {
            Integer color = parseColorName(matcher.group(2).trim());
            if (color == null) {
                Errors.addError(profileName, lineNum, line, ErrorType.UNKNOWN_THEME_FLAG, matcher.group(2));
                return true; //Not Really, but I add the error here
            }
            if (matcher.group(1).equals("fore"))
                fgColor.apply(color, 0x00FFFFFF);
            else
                bgColor = 0x44000000 + color;
        }

        else if (( matcher = SPACING_FLAG_PATTERN.matcher(line) ).matches()) {
            if (padded) {
                Errors.addError(profileName, lineNum, line, ErrorType.LINE_SPACING_AND_PADDING, "");
                return true; //Not Really, but I add the error here
            }
            lineSpacing = Integer.parseInt(matcher.group(1));
            lineSpaced = true;
        }

        else if (global && (  matcher = SCALE_FLAG_PATTERN.matcher(line) ).matches()) {
            float scale = Float.parseFloat(matcher.group(1));
            if (scale == 0) {
                Errors.addError(profileName, lineNum, line, ErrorType.ZERO_SCALE, "");
                return true; //Not Really, but I add the error here
            }
            this.scale = scale;
            scaleMethod = ScaleMethod.DIRECT;
        }

        else if (global && (  matcher = NICESCALE_FLAG_PATTERN.matcher(line) ).matches()) {
            float scale = Integer.parseInt(matcher.group(2));
            if (matcher.group(1) != null || scale < 0) {
                scaleMethod = ScaleMethod.RELATIVE_GUI;
                this.scale = scale;
            }
            else if (scale == 0) {
                Errors.addError(profileName, lineNum, line, ErrorType.ZERO_SCALE, "");
                return true; //Not Really, but I add the error here
            }
            else {
                scaleMethod = ScaleMethod.GUI;
                this.scale = scale;
            }
        }

        else if (global && (  matcher = HUDSCALE_FLAG_PATTERN.matcher(line) ).matches()) {
            hudScale = Integer.parseInt(matcher.group(2));
            hudScaleRelative = matcher.group(1) != null || scale < 0;
        }

        else if (( matcher = FONT_FLAG_PATTERN.matcher(line) ).matches())
            font = new StyleSpriteSource.Font(Identifier.of(matcher.group(1)));

        else if (( matcher = TEXT_SHADOW_FLAG_PATTERN.matcher(line) ).matches())
            textShadow = Boolean.parseBoolean(matcher.group(1));

        else if (( matcher = PADDING_FLAG_PATTERN.matcher(line) ).matches()) {
            if (lineSpaced) {
                Errors.addError(profileName, lineNum, line, ErrorType.LINE_SPACING_AND_PADDING, "");
                return true; //Not Really, but I add the error here
            }
            padded = true;

            if (!empty(matcher,4)) { //Top, Bottom, Left, Right
                padding = new Padding(
                        empty(matcher,1) ? padding.top : Integer.parseInt(matcher.group(1)),
                        empty(matcher,3) ? padding.bottom : Integer.parseInt(matcher.group(3)),
                        empty(matcher,5) ? padding.left : Integer.parseInt(matcher.group(5)),
                        empty(matcher,7) ? padding.right : Integer.parseInt(matcher.group(7))
                );
            }
            else if (!empty(matcher,2)) { //Vertical, Horizontal
                padding = new Padding(
                        empty(matcher, 1) ? padding.top : Integer.parseInt(matcher.group(1)),
                        empty(matcher, 1) ? padding.bottom : Integer.parseInt(matcher.group(1)),
                        empty(matcher, 3) ? padding.left : Integer.parseInt(matcher.group(3)),
                        empty(matcher, 3) ? padding.right : Integer.parseInt(matcher.group(3))
                );
            }
            else if (!empty(matcher,1)) { // All
                int p = Integer.parseInt(matcher.group(1));
                padding = new Padding(p, p, p, p);
            }
        }

        else if (( matcher = CONVERT_LINE_BREAKS_PATTERN.matcher(line) ).matches() )
            convertLineBreaks = Boolean.parseBoolean(matcher.group(1));

        else if (( matcher = IGNORE_BLANK_LINES_PATTERN.matcher(line) ).matches() )
            ignoreBlankLines = Boolean.parseBoolean(matcher.group(1));

        else if (( matcher = IGNORE_LEADING_SPACES.matcher(line) ).matches() )
            ignoreLeadingSpace = Boolean.parseBoolean(matcher.group(1));

        else if (( matcher = PERSISTEMT_FORMATTING_FLAG_PATTERN.matcher(line) ).matches() )
            persistentFormatting = Boolean.parseBoolean(matcher.group(1));

        else if (( matcher = ITEM_ICON_SIZE.matcher(line) ).matches() )
            fitItemIconsToLine = matcher.group(1).equals("line");

        else
            return false;

        return true;
    }
    private static boolean empty(Matcher matcher, int group) {
        return matcher.group(group) == null || matcher.group(group).isEmpty();
    }

    public float getScale() {
        if (scaleMethod == ScaleMethod.DIRECT)
            return scale;
        float gui = (float) CLIENT.getWindow().getScaleFactor();
        float target = scaleMethod == ScaleMethod.GUI ? scale : gui + scale;
        return target / gui;
    }
    public int getTargetGuiScale() {
        if (hudScale == null)
            return CLIENT.getWindow().getScaleFactor();
        int gS = hudScaleRelative ? CLIENT.options.getGuiScale().getValue() + hudScale : hudScale;
        return CLIENT.forcesUnicodeFont() && gS % 2 != 0 ? gS+1 : gS;
    }

    public static CHFormatting parseHexNumber(String str) {
        if (str.equals("none"))
            return new CHFormatting().color(0x00000000, 0xFF000000);
        int length = str.length();

        str = switch (length) {
            case 1 -> "" + str.charAt(0) + str.charAt(0) + "000000";
            case 2 -> "" + str.charAt(0) + str.charAt(1) + "000000";
            case 3 -> "00" + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2);
            case 4 -> "" + str.charAt(0) + str.charAt(0) + str.charAt(1) + str.charAt(1) + str.charAt(2) + str.charAt(2) + str.charAt(3) + str.charAt(3);
            case 5 -> "" + str.charAt(0) + str.charAt(1) + str.charAt(2) + str.charAt(2) + str.charAt(3) + str.charAt(3) + str.charAt(4) + str.charAt(4);
            case 6 -> "00" + str;
            case 7 -> "" + str.charAt(0) + str.charAt(0) + str.substring(1);
            default -> str;
        };

        long colorL = Long.parseLong(str,16);
        int color = (int) (colorL >= 0x100000000L ? colorL - 0x100000000L : colorL);

        int bitmask = switch (length) {
            case 1, 2 -> 0xFF000000;
            case 3, 6 -> 0x00FFFFFF;
            default-> 0xFFFFFFFF;
        };

        return new CHFormatting().color(color, bitmask);


    }

    public static Integer parseColorName(String str) {
        return switch (str) {
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
            default -> null;
        };
    }

    public static CHFormatting parseFormattingName(String str) {
        byte formatting = switch (str) {
            case "bold"       -> CHFormatting.BOLD;
            case "italic"     -> CHFormatting.ITALIC;
            case "underline"  -> CHFormatting.UNDERLINE;
            case "strikethrough", "strike" -> CHFormatting.STRIKE;
            case "obfuscated" -> CHFormatting.OBFUSCATED;
            case "reset"      -> CHFormatting.FULL_RESET;
            default -> 0b11111;
        };
        if (formatting == 0b11111)
            return null;
        return new CHFormatting().format(formatting);
    }

    public static CHFormatting parseColorCode(String str) {
        Integer color = switch (str) {
            case "§0" -> 0x000000;
            case "§1" -> 0x0000AA;
            case "§2" -> 0x00AA00;
            case "§3" -> 0x00AAAA;
            case "§4" -> 0xAA0000;
            case "§5" -> 0xAA00AA;
            case "§6" -> 0xFFAA00;
            case "§7" -> 0xAAAAAA;
            case "§8" -> 0x555555;
            case "§9" -> 0x5555FF;
            case "§a" -> 0x55FF55;
            case "§b" -> 0x55FFFF;
            case "§c" -> 0xFF5555;
            case "§d" -> 0xFF55FF;
            case "§e" -> 0xFFFF55;
            case "§f" -> 0xFFFFFF;
            //Bedrock:
            case "§g" -> 0xDDD605;
            case "§h" -> 0xE3D4D1;
            case "§i" -> 0xCECACA;
            case "§j" -> 0x443A3B;
            case "§zm" -> 0x971607;
            case "§zn" -> 0xB4684D;
            case "§p" -> 0xDEB12D;
            case "§q" -> 0x47A036;
            case "§s" -> 0x2CBAA8;
            case "§t" -> 0x21497B;
            case "§u" -> 0x9A5CC6;
            default -> null;
        };
        if (color != null)
            return new CHFormatting().color(color, 0x00FFFFFF);

        Byte formatting = switch (str) {
            case "§k" -> CHFormatting.OBFUSCATED;
            case "§l" -> CHFormatting.BOLD;
            case "§m" -> CHFormatting.STRIKE;
            case "§n" -> CHFormatting.UNDERLINE;
            case "§o" -> CHFormatting.ITALIC;
            case "§r" -> CHFormatting.FULL_RESET;
            default -> null;
        };
        return formatting == null ? null : new CHFormatting().format(formatting);
    }


}
