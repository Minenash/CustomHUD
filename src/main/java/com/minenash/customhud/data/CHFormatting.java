package com.minenash.customhud.data;

import java.util.HashMap;
import java.util.Map;

public class CHFormatting {
    public static final byte       BOLD = 0b10000;
    public static final byte     ITALIC = 0b01000;
    public static final byte  UNDERLINE = 0b00100;
    public static final byte     STRIKE = 0b10010;
    public static final byte OBFUSCATED = 0b10001;
    public static final byte      RESET = 0b00000;
    private static final byte    INVERT = 0b11111;

    private int color = 0x000000;
    private int colorBitmask = 0x000000;

    private byte formatting = 0b00000;
    private byte formattingBitmask = 0b00000;

    public CHFormatting color(int color, int bitmask) {
        this.color = color;
        this.colorBitmask = bitmask;
        return this;
    }
    public CHFormatting format(byte format) {
        this.formatting |= format;
        this.formattingBitmask |= format == RESET ? INVERT : format;
        return this;
    }
    public CHFormatting reset() {
        this.formatting |= 0b00000;
        this.formattingBitmask |= 0b11111;
        return this;
    }

    public CHFormatting apply(CHFormatting f) {
        this.color &= f.colorBitmask ^ INVERT;
        this.color |= f.color & f.colorBitmask;
        this.colorBitmask |= f.colorBitmask;
        this.formatting &= f.formattingBitmask ^ INVERT;
        this.formatting |= f.formatting & f.formattingBitmask;
        this.formattingBitmask |= f.formattingBitmask;
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
