package com.minenash.customhud.gui.customize;

import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.ProfileOption;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.util.Collections;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class OptionListWidget extends ElementListWidget<OptionListWidget.OEntry> implements CSTabWidget {
    private static final Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_list_background.png");
    private static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");

    private final CustomizeScreen parent;

    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height - 36 + 4 - 30 - 3;

    }
    public void updateChildren() {
        for (var e : children())
            e.update();
    }

    public OptionListWidget(CustomizeScreen parent, int width, int height) {
        super(CLIENT, width, height - 36 + 4 - 30 - 3, 30, /*TogglesScreen.this.height - 36 + 4,*/ 18);
        this.parent = parent;
        Profile profile = parent.profile;

        boolean noEntries = profile == null || profile.options.isEmpty();

        if (noEntries)
            return;

        for (var e : profile.options.entrySet())
            this.addEntry(new OptionEntry(e.getValue(), e.getKey()));

        if (!profile.optionHeaders.isEmpty()) {
            for (var e : profile.optionHeaders)
                children().add(e.getLeft(), new OptionCategoryHeader(e.getRight()));

            for (int i = 1; i < children().size(); i++)
                if (children().get(i) instanceof OptionCategoryHeader)
                    children().add(i++, new BlankSeparator());
        }

//        this.addEntry(new BlankSeparator());
//        this.addEntry(new OptionCategorySeparator());
//        this.addEntry(new BlankSeparator());
//        for (var e : profile.toggles.entrySet())
//            if (!e.getValue().inProfile)
//                this.addEntry(new OptionEntry(e.getValue(), e.getKey()));
//
//        int index = children().size()-2;
//        if (children().get(index) instanceof OptionCategorySeparator) {
//            children().remove(index+1);
//            children().remove(index);
//            children().remove(index-1);
//        }
    }

    @Override
    protected void drawHeaderAndFooterSeparators(DrawContext context) {}

    @Override
    protected void drawMenuListBackground(DrawContext context) {
        RenderSystem.enableBlend();
        Identifier identifier = this.client.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
        context.drawTexture(identifier, this.getX(),
            this.getY() - 6,
            (float)this.getRight(),
            (float)(this.getBottom() + (int)this.getScrollAmount()),
            this.getWidth(),
            this.getHeight() + 6,
            32,
            32
        );
        RenderSystem.disableBlend();
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 64;
    }

    @Override
    protected int getScrollbarX() {
        return super.getScrollbarX() + 32;
    }

    public abstract static class OEntry extends Entry<OEntry> {
        public void update() {}
        @Override public List<? extends Selectable> selectableChildren() { return Collections.emptyList(); }
        @Override public List<? extends Element> children() { return Collections.emptyList(); }
    }

    public class OptionCategoryHeader extends OEntry {
        public final Text categoryName;

        OptionCategoryHeader(String categoryName) {
            this.categoryName = Text.literal(categoryName).setStyle(Style.EMPTY.withUnderline(true));
        }

        private int entries(int eHeight) {
            float entries = 0.5f;
            var children = OptionListWidget.this.children();
            for (int i = children.indexOf(this); i < children.size(); i++)
                if (children.get(i) instanceof BlankSeparator)
                    break;
                else
                    entries++;
            return (int) (eHeight * entries);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            if (hovered) {
                context.fill(x - 3, y - 1, x + eWidth + 1, y + entries(eHeight+4), 0x11ECECEC);
            }

            context.drawCenteredTextWithShadow(CLIENT.textRenderer, categoryName, x + eWidth/2, y+4, 0xFFFFFFFF);
        }
    }
    public static class BlankSeparator extends OEntry {
        @Override
        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawHorizontalLine(x, x+eWidth, y + (eHeight/2), 0x11ECECEC);
        }
    }

    public class OptionEntry extends OEntry {
        final ProfileOption option;
        final String keyName;

        final ButtonWidget button;
        final TextFieldWidget textField;
        final List<ClickableWidget> children;
        final int buttonOffset;
        final Text textFieldHover;

        public OptionEntry(ProfileOption option, String keyName) {
            this.option = option;
            this.keyName = keyName;
            this.buttonOffset = option.value instanceof Color ? 18 : 0;

            if (option.value instanceof Boolean bool) {
                button = ButtonWidget.builder(bool ? Text.literal("True") : Text.literal("False"), b -> {
                    boolean v = !option.<Boolean>get();
                    option.value = v;
                    b.setMessage(v ? Text.literal("True") : Text.literal("False"));
                }).size(80, 16).build();
                textField = null;
                textFieldHover = null;
                children = List.of(button);
            }
            else if (option.value instanceof ProfileOption.OnOff) {
                button = ButtonWidget.builder(option.value == ProfileOption.OnOff.ON ? Text.literal("On") : Text.literal("Off"), b -> {
                    option.value = ProfileOption.OnOff.toggle(option.value);
                    b.setMessage(option.value == ProfileOption.OnOff.ON ? Text.literal("On") : Text.literal("Off"));
                }).size(80, 16).build();
                textField = null;
                textFieldHover = null;
                children = List.of(button);
            }
            else if (option.value instanceof KeyBinding kb) {
                button = ButtonWidget.builder(kb.getBoundKeyLocalizedText(), b -> {
                    parent.selectedKeybind = kb;
                    update();
                }).size(80, 16).build();
                textField = null;
                textFieldHover = null;
                children = List.of(button);
            }
            else {
                textField = new TextFieldWidget(client.textRenderer, 80, 16, Text.literal("lolz"));
                textField.setText(option.value.toString());
                children = List.of(textField);

                if (option.value instanceof Integer) {
                    textField.setChangedListener(str -> option.value = NumberUtils.toInt(str, 0));
                    textField.setTextPredicate(str -> {
                        for (int i = 0; i < str.length(); i++) {
                            char c = str.charAt(i);
                            if (i == 0 && c == '-')
                                continue;
                            if (c < '0' || c > '9')
                                return false;
                        }
                        return true;
                    });
                    textFieldHover = Text.literal("Allowed characters:\n• 0-9\n• A dash (-) at the beginning");
                    button = null;
                }
                else if (option.value instanceof Double) {
                    textField.setChangedListener(str -> option.value = NumberUtils.toDouble(str, 0));
                    textField.setTextPredicate(str -> {
                        boolean hasDot = false;
                        for (int i = 0; i < str.length(); i++) {
                            char c = str.charAt(i);
                            if (i == 0 && c == '-')
                                continue;
                            if (c == '.') {
                                if (hasDot)
                                    return false;
                                else
                                    hasDot = true;
                            }
                            else if (c < '0' || c > '9')
                                return false;
                        }
                        return true;
                    });
                    textFieldHover = Text.literal("Allowed characters:\n• 0-9\n• A single period (.)\n• A dash (-) at the beginning");
                    button = null;
                }
                else if (option.value instanceof Color co) {
                    int color = co.getRGB();
                    button = ButtonWidget.builder(Text.literal("■").styled(s -> s.withColor(color)), b -> {})
                        .size(16, 16).build();

                    textField.setText( Integer.toHexString(color).toUpperCase());
                    textField.setChangedListener(str -> {
                        long cLong = str.isEmpty() ? -1 : Long.parseLong(str.toLowerCase(),16);
                        int c = (int) (cLong >= 0x100000000L ? cLong - 0x100000000L : cLong);
                        option.value = new Color(c, true);
                        button.setMessage(Text.literal("■").styled(s -> s.withColor(c)));
                    });
                    textField.setTextPredicate(str -> {
                        if (str.length() > 8)
                            return false;
                        for (int i = 0; i < str.length(); i++) {
                            char c = str.charAt(i);
                            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F')))
                                return false;
                        }
                        return true;
                    });
                    textFieldHover = Text.literal("Allowed characters: 0-9, A-F, a-f");

                }
                else if (option.value instanceof String) {
                    textField.setChangedListener(str -> option.value = str);
                    textFieldHover = Text.literal("Allowed characters: Any");
                    button = null;
                }
                else { // Never should happen
                    button = null;
                    textFieldHover = null;
                }

            }
        }

        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
            if (hovered)
                context.fill(x-3, y-1, x+eWidth+1, y+eHeight+3, 0x11ECECEC);
            context.drawTextWithShadow(client.textRenderer, option.name, x, y+4, 0xFFFFFFFF);

            if (hovered && option.tooltip != null)
                parent.setTooltip(option.tooltip);

            if (button != null) {
                button.setY(y);
                button.setX(x+eWidth-80-buttonOffset);
                button.render(context, mX, mY, delta);
            }
            if (textField != null) {
                textField.setY(y);
                textField.setX(x+eWidth-80);
                textField.render(context, mX, mY, delta);
                if (textField.isHovered())
                    parent.setTooltip(textFieldHover);
            }

        }

        @Override
        public void update() {
            if (option.value instanceof KeyBinding kb) {
                button.setMessage(kb.getBoundKeyLocalizedText());
                if (parent.selectedKeybind == kb)
                    button.setMessage(Text.literal("> ")
                        .append(button.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                        .append(" <").formatted(Formatting.YELLOW));
            }
        }

        @Override public List<? extends Selectable> selectableChildren() { return children; }
        @Override public List<? extends Element> children() { return children; }
    }
}