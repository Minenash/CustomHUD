package com.minenash.customhud.data;

import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flags {

    public enum TextCase {UPPER, LOWER, TITLE}

    public TextCase textCase = null;
    public boolean smallCaps = false;
    public int numSize = 0;
    public boolean noDelimiters = false;

    public int precision = -1;
    public double scale = 1;
    public boolean hex = false;

    public boolean formatted = false;

    public int iconWidth = -1;
    public int iconShiftX = 0;
    public int iconShiftY = 0;
    public float rotation = 0;
    public boolean iconReferenceCorner = false;
    public boolean iconShowCount = false;
    public boolean iconShowDur = false;
    public boolean iconShowCooldown = false;

    public boolean anyTextUsed() {
        return textCase != null || smallCaps || numSize != 0 || noDelimiters;
    }

    private static final Pattern PRECISION_PATTERN = Pattern.compile("-(?:p|precision)(\\d+)");
    private static final Pattern SCALE_PATTERN = Pattern.compile("-(?:s|scale)((\\d+)/(\\d+)|\\d+(\\.\\d+)?)");
    private static final Pattern WIDTH_PATTERN = Pattern.compile("-(?:w|width)(\\d+)");
    private static final Pattern SHIFT_PATTERN = Pattern.compile("-(?:sh|shift)(-?\\d+)(?:,(-?\\d+))?");
    private static final Pattern ROTATE_PATTERN = Pattern.compile("-(?:r|rotation)(-?\\d+)");

    public static Flags parse(int profile, int line, String[] parts) {
        Flags flags = new Flags();

        if (parts.length <= 1)
            return flags;

        for (int i = 1; i < parts.length; i++) {
            switch (parts[i]) {
                // Text
                case "-uc", "-uppercase" -> flags.textCase = Flags.TextCase.UPPER;
                case "-lc", "-lowercase" -> flags.textCase = Flags.TextCase.LOWER;
                case "-tc", "-titlecase" -> flags.textCase = Flags.TextCase.TITLE;
                case "-sc", "-smallcaps" -> flags.smallCaps = true;
                case "-sub", "-subnums" -> flags.numSize = 1;
                case "-sup", "-supnums" -> flags.numSize = 2;
                case "-hex" -> flags.hex = true;
                case "-nd", "-nodashes" -> flags.noDelimiters = true;
                // Stat
                case "-f", "-formatted" -> flags.formatted = true;
                // Icons
                case "-dvc" -> flags.iconReferenceCorner = true;
                // Slot Icons
                case "-rich" -> flags.iconShowCount = flags.iconShowDur = flags.iconShowCooldown = true;
                case "-count" -> flags.iconShowCount = true;
                case "-dur" -> flags.iconShowDur = true;
                case "-cooldown" -> flags.iconShowCooldown = true;
                default -> {
                    //Decimals
                    Matcher matcher = PRECISION_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.precision = Integer.parseInt(matcher.group(1));
                        continue;
                    }
                    matcher = SCALE_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        if (parts[i].contains("/"))
                            flags.scale = Integer.parseInt(matcher.group(2)) / (double) Integer.parseInt(matcher.group(3));
                        else
                            flags.scale = Double.parseDouble(matcher.group(1));
                        continue;
                    }
                    //Icons
                    matcher = WIDTH_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.iconWidth = Integer.parseInt(matcher.group(1));
                        continue;
                    }
                    matcher = SHIFT_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.iconShiftX = Integer.parseInt(matcher.group(1));
                        if (matcher.group(2) != null)
                            flags.iconShiftY = Integer.parseInt(matcher.group(2));
                        continue;
                    }
                    matcher = ROTATE_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.rotation = Integer.parseInt(matcher.group(1)) * 0.017453292F;
                        continue;
                    }

                    Errors.addError(profile, line, parts[i], ErrorType.UNKNOWN_VARIABLE_FLAG, null);
                }
            }
        }
        return flags;
    }

    public static String smallCaps(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case 'a' -> chars[i] = 'ᴀ';
                case 'b' -> chars[i] = 'ʙ';
                case 'c' -> chars[i] = 'ᴄ';
                case 'd' -> chars[i] = 'ᴅ';
                case 'e' -> chars[i] = 'ᴇ';
                case 'f' -> chars[i] = 'ꜰ';
                case 'g' -> chars[i] = 'ɢ';
                case 'h' -> chars[i] = 'ʜ';
                case 'i' -> chars[i] = 'ɪ';
                case 'j' -> chars[i] = 'ᴊ';
                case 'k' -> chars[i] = 'ᴋ';
                case 'l' -> chars[i] = 'ʟ';
                case 'm' -> chars[i] = 'ᴍ';
                case 'n' -> chars[i] = 'ɴ';
                case 'o' -> chars[i] = 'ᴏ';
                case 'p' -> chars[i] = 'ᴘ';
                case 'q' -> chars[i] = '\uA7AF';
                case 'r' -> chars[i] = 'ʀ';
                case 's' -> chars[i] = 'ꜱ';
                case 't' -> chars[i] = 'ᴛ';
                case 'u' -> chars[i] = 'ᴜ';
                case 'v' -> chars[i] = 'ᴠ';
                case 'w' -> chars[i] = 'ᴡ';
                case 'y' -> chars[i] = 'ʏ';
                case 'z' -> chars[i] = 'ᴢ';
            }
        }
        return new String(chars);
    }

    public static String subNums(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '0' -> chars[i] = '₀';
                case '1' -> chars[i] = '₁';
                case '2' -> chars[i] = '₂';
                case '3' -> chars[i] = '₃';
                case '4' -> chars[i] = '₄';
                case '5' -> chars[i] = '₅';
                case '6' -> chars[i] = '₆';
                case '7' -> chars[i] = '₇';
                case '8' -> chars[i] = '₈';
                case '9' -> chars[i] = '₉';
            }
        }
        return new String(chars);
    }

    public static String supNums(String text) {
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '0' -> chars[i] = '⁰';
                case '1' -> chars[i] = '¹';
                case '2' -> chars[i] = '²';
                case '3' -> chars[i] = '³';
                case '4' -> chars[i] = '⁴';
                case '5' -> chars[i] = '⁵';
                case '6' -> chars[i] = '⁶';
                case '7' -> chars[i] = '⁷';
                case '8' -> chars[i] = '⁸';
                case '9' -> chars[i] = '⁹';
            }
        }
        return new String(chars);
    }

}
