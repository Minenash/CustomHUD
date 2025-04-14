package com.minenash.customhud.data;

import com.minenash.customhud.HudElements.FormattedElement;
import com.minenash.customhud.HudElements.FrequencyElement;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.HudElements.RomanNumeralElement;
import com.minenash.customhud.HudElements.icon.IconElement;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.v5.NumberDefaultOptions;
import net.minecraft.stat.StatFormatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flags {

    public enum TextCase {UPPER, LOWER, TITLE}
    public enum IdPart {NAMESPACE, PATH, FULL}

    public TextCase textCase = null;
    public boolean smallCaps = false;
    public int numSize = 0;
    public boolean noDelimiters = false;
    public IdPart idPart = IdPart.FULL;
    public String listPrefix = "";
    public boolean reverseList = false;

    public int precision = -1;
    public double scale = 1;
    public int zerofill = 0;
    public int base = 10;
    public int frequency = -1;

    public boolean formatted = false;
    public boolean romanNumerals = false;

    public int iconWidth = -1;
    public int iconShiftX = 0;
    public int iconShiftY = 0;
    public float rotation = 0;
    public boolean iconReferenceCorner = false;
    public boolean iconShowCount = false;
    public boolean iconShowDur = false;
    public boolean iconShowCooldown = false;

    public StatFormatter formatter = null;

    public boolean anyTextUsed() {
        return textCase != null || smallCaps || numSize != 0 || noDelimiters;
    }

    public Flags applyNumDefaults(NumberDefaultOptions numDefaults) {
        if (precision == -1)
            precision = numDefaults.precision();
        formatter = numDefaults.formatter();
        return this;
    }

    private static final Pattern PRECISION_PATTERN = Pattern.compile("-(?:p|precision)(\\d+)");
    private static final Pattern ZEROFILL_PATTERN = Pattern.compile("-(?:zf|zero-?fill)(\\d+)");
    private static final Pattern SCALE_PATTERN = Pattern.compile("-(?:s|scale)((\\d+)/(\\d+)|\\d+(\\.\\d+)?)");
    private static final Pattern FREQUENCY_PATTERN = Pattern.compile("-(?:v|freq|frequency)((\\d+)/(\\d+)|\\d+(\\.\\d+)?)");
    private static final Pattern WIDTH_PATTERN = Pattern.compile("-(?:w|width)(\\d+)");
    public static final Pattern SHIFT_PATTERN = Pattern.compile("-(?:sh|shift)(-?\\d+)(?:[,|](-?\\d+))?");
    private static final Pattern ROTATE_PATTERN = Pattern.compile("-(?:r|rotation)(-?\\d+)");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("-(?:pre|prefix):(-?\\w+)");
    private static final Pattern BASE_PATTERN = Pattern.compile("-base(\\d+)");

    public static Flags parse(String profileName, int line, String[] parts) {
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
                case "-hex" -> flags.base = 16;
                case "-oct" -> flags.base = 8;
                case "-bin" -> flags.base = 2;
                case "-nd", "-nodashes" -> flags.noDelimiters = true;
                // ID
                case "-ns", "-namespace" -> flags.idPart = IdPart.NAMESPACE;
                case "-path" -> flags.idPart = IdPart.PATH;
                // Stat
                case "-f", "-formatted" -> flags.formatted = true;
                //Numbers
                case "-rn", "-roman" -> flags.romanNumerals = true;
                // Icons
                case "-dvc" -> flags.iconReferenceCorner = true;
                // Slot Icons
                case "-rich" -> flags.iconShowCount = flags.iconShowDur = flags.iconShowCooldown = true;
                case "-count" -> flags.iconShowCount = true;
                case "-dur" -> flags.iconShowDur = true;
                case "-cooldown" -> flags.iconShowCooldown = true;
                case "-r", "-reverse" -> flags.reverseList = true;
                default -> {
                    //Decimals
                    Matcher matcher = PRECISION_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.precision = Integer.parseInt(matcher.group(1));
                        continue;
                    }
                    matcher = ZEROFILL_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.zerofill = Integer.parseInt(matcher.group(1));
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
                    matcher = BASE_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        int base = Integer.parseInt(matcher.group(1));
                        if (base > 36)
                            Errors.addError(profileName, line, parts[i], ErrorType.RADIX_TOO_BIG, null);
                        else if (base < 2)
                            Errors.addError(profileName, line, parts[i], ErrorType.RADIX_TOO_SMALL, null);
                        else
                            flags.base = base;
                        continue;
                    }
                    //Frequency / Update
                    matcher = FREQUENCY_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        if (parts[i].contains("/"))
                            flags.frequency = (int)(1000 * Integer.parseInt(matcher.group(2)) / (double) Integer.parseInt(matcher.group(3)));
                        else
                            flags.frequency = (int)(1000 * Double.parseDouble(matcher.group(1)));
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
                    matcher = PREFIX_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.listPrefix = matcher.group(1);
                        continue;
                    }

                    Errors.addError(profileName, line, parts[i], ErrorType.UNKNOWN_VARIABLE_FLAG, null);
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

    public static HudElement wrap(HudElement element, Flags flags) {
        if (element instanceof IconElement)
            return element;
        if (flags.romanNumerals)
            element = new RomanNumeralElement(element);
        if (flags.anyTextUsed() && !flags.romanNumerals)
            element = new FormattedElement(element, flags);
        if (flags.frequency > 0)
            element = new FrequencyElement(element, flags.frequency);
        return element;
    }

}
