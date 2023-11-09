package com.minenash.customhud.HudElements;

import com.minenash.customhud.data.Flags;
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
            text = Flags.smallCaps(text);
        if (flags.numSize == 1)
            text = Flags.subNums(text);
        if (flags.numSize == 2)
            text = Flags.supNums(text);

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

}
