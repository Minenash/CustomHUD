package com.minenash.customhud.core.elements;

import com.minenash.customhud.core.data.Flags;
import org.apache.commons.lang3.text.WordUtils;

public class FormattedElement implements HudElement {

    final static char[] delimiters = { ' ', '_', '-', ':' };

    private final HudElement base;
    private final Flags flags;

    public FormattedElement(HudElement base, Flags flags) {
        this.base = base;
        this.flags = flags;
    }

    @Override
    public String getString() {
        String text = base.getString();

        if (flags.noDelimiters)
            text = text.replace('-', ' ').replace('_', ' ');


        if (flags.textCase != null)
            switch (flags.textCase) {
                case UPPER -> text = text.toUpperCase();
                case LOWER -> text = text.toLowerCase();
                case TITLE -> text = WordUtils.capitalizeFully(text, delimiters);
            }

        if (flags.smallCaps)
            text = smallcaps(text);

        return text;
    }

    @Override
    public Number getNumber() {
        return base.getNumber();
    }

    @Override
    public boolean getBoolean() {
        return base.getBoolean();
    }

    public static String smallcaps(String text) {
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

}
