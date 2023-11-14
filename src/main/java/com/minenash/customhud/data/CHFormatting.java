package com.minenash.customhud.data;

import com.mojang.blaze3d.systems.RenderSystem;

import java.util.HashMap;
import java.util.Map;

public class CHFormatting {
    public static final byte       NONE = 0b0000000;
    public static final byte FULL_RESET = 0b1000000;
    public static final byte      RESET = 0b0100000;
    public static final byte       BOLD = 0b0010000;
    public static final byte     ITALIC = 0b0001000;
    public static final byte  UNDERLINE = 0b0000100;
    public static final byte     STRIKE = 0b0000010;
    public static final byte OBFUSCATED = 0b0000001;
    private static final int    INVERT = 0xFFFFFFFF;

    private int color = 0x00000000;
    private int colorBitmask = 0x00000000;

    private byte formatting = NONE;

    public CHFormatting color(int color, int bitmask) {
        this.color = color;
        this.colorBitmask = bitmask;
        this.formatting = RESET;

        return this;
    }
    public CHFormatting format(byte format) {
        this.formatting |= format;
        return this;
    }

    public CHFormatting apply(CHFormatting f, HudTheme theme) {
        if ((f.formatting & FULL_RESET) != 0) {
            this.color = theme.fgColor.color;
            this.colorBitmask = theme.fgColor.colorBitmask;
            this.formatting = NONE;
            return this;
        }
        this.color &= f.colorBitmask ^ INVERT;
        this.color |= f.color & f.colorBitmask;
        this.colorBitmask |= f.colorBitmask;
        if ( (f.formatting & RESET) != 0)
            this.formatting = NONE;
        else
            this.formatting |= f.formatting;
        return this;

    }

    public CHFormatting apply(int color, int bitmask) {
        this.color &= bitmask ^ INVERT;
        this.color |= color & bitmask;
        this.colorBitmask |= bitmask;
        return this;
    }

    public int getColor() {
        return color;
    }

    public String getFormatting() {
        return FORMAT_MAP.get(formatting);
    }

    private static final Map<Byte,String> FORMAT_MAP = new HashMap<>(32);
    static {
        for (byte b = 0; b <= 0b11111; b++) {
            String format = "";
            if ((b & BOLD) != 0)       format += "§l";
            if ((b & ITALIC) != 0)     format += "§o";
            if ((b & UNDERLINE) != 0)  format += "§n";
            if ((b & STRIKE) != 0)     format += "§m";
            if ((b & OBFUSCATED) != 0) format += "§k";
            FORMAT_MAP.put(b, format);
        }
    }


}
