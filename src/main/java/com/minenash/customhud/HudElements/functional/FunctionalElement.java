package com.minenash.customhud.HudElements.functional;

import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.list.Attributers;
import com.minenash.customhud.HudElements.list.ListProvider;
import com.minenash.customhud.data.CHFormatting;
import com.minenash.customhud.data.HudTheme;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalElement implements HudElement {

    @Override public String getString() { return null; }
    @Override public Number getNumber() { return null; }
    @Override public boolean getBoolean() { return false; }

    public static class ChangeTheme extends FunctionalElement {
        public final HudTheme theme;
        public ChangeTheme(HudTheme theme) { this.theme = theme; }
    }

    public static class ChangeFormatting extends FunctionalElement {
        public final CHFormatting formatting;
        public ChangeFormatting(CHFormatting formatting) { this.formatting = formatting; }
        public ChangeFormatting(int color) {
            formatting = new CHFormatting().color(color,
                    ((color & 0xFF000000) != 0 ? 0xFF000000 : 0x00000000)
                    | ((color & 0x00FFFFFF) != 0 ? 0x00FFFFFF : 0x00000000));
        }
        public CHFormatting getFormatting() {return formatting;}
    }
    public static class ChangeFormattingFromElement extends ChangeFormatting {
        public final HudElement element;
        public ChangeFormattingFromElement(HudElement element) {super(null); this.element = element; }
        public CHFormatting getFormatting() {
            int color = element.getNumber().intValue();
            int bitmask = (color & 0xFF000000) != 0 ? 0xFFFFFFFF : 0x00FFFFFF;
            return new CHFormatting().color(color, bitmask);
        }
    }

    public static class NewLine extends FunctionalElement {}
    public static class IgnoreNewLineIfSurroundedByNewLine extends FunctionalElement {}

    public static class AdvanceList extends FunctionalElement {}
    public static class PopList extends FunctionalElement {}
    public static class PushList extends FunctionalElement {
        public final List<?> values;
        public PushList(List<?> values) { this.values = values; }
    }
    public static class CreateListElement extends FunctionalElement {
        public final ListProvider provider;
        public final Attributers.Attributer attributer;
        public CreateListElement(Supplier<?> supplier, Function<?,List<?>> function, Attributers.Attributer attributer) {
            this.provider = new ListProvider.ListFunctioner(supplier,function);
            this.attributer = attributer;
            Attributers.ATTRIBUTER_MAP.put(provider, attributer);
        }
    }

}
