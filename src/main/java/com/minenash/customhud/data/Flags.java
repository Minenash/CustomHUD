package com.minenash.customhud.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flags {

    public enum TextCase {UPPER, LOWER, TITLE}

    public TextCase textCase = null;
    public boolean smallCaps = false;
    public boolean noDelimiters = false;

    public int precision = -1;
    public double scale = 1;

    public boolean formatted = false;

    public int iconWidth = -1;
    public int iconShiftX = 0;
    public int iconShiftY = 0;
    public boolean iconReferenceCorner = false;
    public boolean iconShowCount = false;
    public boolean iconShowDur = false;
    public boolean iconShowCooldown = false;

    public boolean anyTextUsed() {
        return textCase != null || smallCaps || noDelimiters;
    }

    private static final Pattern PRECISION_PATTERN = Pattern.compile("-(?:p|precision)(\\d+)");
    private static final Pattern SCALE_PATTERN = Pattern.compile("-(?:s|scale)((\\d+)/(\\d+)|\\d+(\\.\\d+)?)");
    private static final Pattern WIDTH_PATTERN = Pattern.compile("-(?:w|width)(\\d+)");
    private static final Pattern SHIFT_PATTERN = Pattern.compile("-(?:sh|shift)(-?\\d+)(?:,(-?\\d+))?");
    public static Flags parse(String[] parts) {
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
                    }
                    matcher = SHIFT_PATTERN.matcher(parts[i]);
                    if (matcher.matches()) {
                        flags.iconShiftX = Integer.parseInt(matcher.group(1));
                        if (matcher.group(2) != null)
                            flags.iconShiftY = Integer.parseInt(matcher.group(2));
                    }
                }
            }
        }
        return flags;
    }

}
