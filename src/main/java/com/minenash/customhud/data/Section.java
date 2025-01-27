package com.minenash.customhud.data;

import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.conditionals.Operation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class Section {
    public enum Align {LEFT, CENTER, RIGHT}

    public Operation xOffset = new Operation.Literal(0);
    public Operation yOffset = new Operation.Literal(0);
    public int width = -1;
    public Align textAlign;
    public Align sAlign;
    public boolean hideOnChat = false;

    public List<HudElement> elements = new ArrayList<>();

    public Section(Align align) { this.sAlign = this.textAlign = align; }

    public int hPaddingOffset(HudTheme theme) {
        return textAlign == Align.LEFT ? theme.padding.left() : textAlign == Align.RIGHT ? -theme.padding.right() : (theme.padding.left() - theme.padding.right())/2;
    }

    public static class Top extends Section {
        public Top(Align sAlign) { super(sAlign); }
    }

    public static class Center extends Section {
        public Center(Align sAlign) { super(sAlign); }
    }

    public static class Bottom extends Section {
        public Bottom(Align sAlign) { super(sAlign); }
    }

}
