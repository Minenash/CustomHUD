package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.HudElements.interfaces.MultiElement;
import com.minenash.customhud.HudElements.interfaces.NumElement;
import com.minenash.customhud.data.HudTheme;
import com.minenash.customhud.render.CustomHudRenderer3;
import net.minecraft.util.Identifier;

import java.util.List;

public class RomanNumeralElement extends FunctionalElement implements HudElement, MultiElement {

    private static final Identifier ROMAN_NUMERAL_FONT = Identifier.of("custom_hud", "roman");
    private final HudElement base;
    private final boolean precision;

    public RomanNumeralElement(HudElement base) {
        this.base = base;
        this.precision = base instanceof NumElement ne && ne.getPrecision() > 0;
    }

    @Override
    public String getString() {
        return base.getString();
    }

    @Override
    public Number getNumber() {
        return base.getNumber();
    }

    @Override
    public boolean getBoolean() {
        return base.getBoolean();
    }

    @Override
    public List<HudElement> expand() {
        HudTheme copy = CustomHudRenderer3.theme.copy();
        copy.font = ROMAN_NUMERAL_FONT;

        return List.of(
                new FunctionalElement.ChangeTheme(copy),
                new StringElement( precision ? convert(base.getNumber().intValue()) : convert(base.getNumber().doubleValue()) ),
                new FunctionalElement.ChangeTheme(CustomHudRenderer3.theme.copy())
        );
    }

    @Override
    public boolean ignoreNewlineIfEmpty() {
        return true;
    }

    public static String convert(int num) {
        return num == 0 ? "N" : num < 0 ? "-" + _convert(-1 * num) : _convert(num);
    }

    private static String _convert(int num) {
        if (num < 4_000)
            return convertSection(num, true);
        if (num < 4_000_000)
            return convertSection(num / 1_000, false) + convertSection(num % 1_000, true);
        return "|\uE001" + convertSection(num / 1_000_000, false) + "\uE001:" +
                (num % 1_000_000 / 1_000 > 0 ? convertSection(num % 1_000_000 / 1_000, false) : "") +
                convertSection(num % 1_000, true);


    }

    private static final int[]     values = {1000,  900, 500,  400, 100,   90,  50,   40,  10,    9,   5,    4,   1};
    private static final String[] least = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
    private static final String[] most = { "m\uE001", "c\uE001m\uE001", "d\uE001", "c\uE001d\uE001", "c\uE001",
            "x\uE001c\uE001", "l\uE001", "x\uE001l\uE001", "x\uE001", "i\uE001x\uE001", "v\uE001", "i\uE001v\uE001", "i\uE001"};

    private static String convertSection(int num, boolean rightmost) {
        String[] letters = rightmost ? least : most;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                result.append(letters[i]);
                num -= values[i];
            }
        }
        return rightmost ? result.toString() : result.substring(0, result.length()-1);

    }

    public static String convert(double num) {
        if (num == 0) return "N";

        boolean neg = num < 0;
        if (neg)
            num *= -1;

        int twelfths = (int) Math.round((num % 1) * 12);
        if (twelfths == 12)
            num++;

        return (neg ? "-" : "") + (num < 1 ? "" : _convert( (int) num) )
                + switch ( twelfths ) {
            case 1 -> "1";
            case 2 -> "2";
            case 3 -> "3";
            case 4 -> "4";
            case 5 -> "5";
            case 6 -> "S";
            case 7 -> "S1";
            case 8 -> "S2";
            case 9 -> "S3";
            case 10 -> "S4";
            case 11 -> "S5";
            default -> "";
        };


    }
}
