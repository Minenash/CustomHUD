package com.minenash.customhud.gui.customize;

import com.minenash.customhud.ConfigManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ToggleListWidget extends ElementListWidget<ToggleListWidget.TEntry> implements CSTabWidget {
    public static final Identifier MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/menu_list_background.png");
    public static final Identifier INWORLD_MENU_LIST_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/inworld_menu_list_background.png");

    private final CustomizeScreen parent;

    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height - 36 + 4 - 30 - 3;

    }
    public void updateChildren() {
        for (var e : children())
            e.update();
    }

    public ToggleListWidget(CustomizeScreen parent, int width, int height) {
        super(CLIENT, width, height - 36 + 4 - 30 - 3, 30, /*TogglesScreen.this.height - 36 + 4,*/ 18);
        this.parent = parent;
        Profile profile = parent.profile;

        boolean noEntries = profile == null || profile.toggles.values().isEmpty();

        this.addEntry( new ToggleEntryHeader(noEntries) );

        if (noEntries)
            return;

        for (var e : profile.toggles.entrySet())
            if (e.getValue().inProfile)
                this.addEntry(new ToggleEntry(e.getValue(), e.getKey()));

        this.addEntry(new BlankSeparator());
        this.addEntry(new ToggleEntrySeparator());
        this.addEntry(new BlankSeparator());
        for (var e : profile.toggles.entrySet())
            if (!e.getValue().inProfile)
                this.addEntry(new ToggleEntry(e.getValue(), e.getKey()));

        int index = children().size()-2;
        if (children().get(index) instanceof ToggleEntrySeparator) {
            children().remove(index+1);
            children().remove(index);
            children().remove(index-1);
        }
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

    public abstract class TEntry extends ElementListWidget.Entry<TEntry> {
        public void update() {}
        @Override public List<? extends Selectable> selectableChildren() { return Collections.emptyList(); }
        @Override public List<? extends Element> children() { return Collections.emptyList(); }
    }

    public class ToggleEntryHeader extends TEntry {
        private static final Text LINE = Text.literal("Line").formatted(Formatting.UNDERLINE);
        private static final Text NAME = Text.literal("Name").formatted(Formatting.UNDERLINE);
        private static final Text MODIFIER = Text.literal("Modifier").formatted(Formatting.UNDERLINE);
        private static final Text KEYBIND = Text.literal("Key").formatted(Formatting.UNDERLINE);
        private static final Text NO_TOGGLES = Text.literal("This profiles has no toggles").formatted(Formatting.UNDERLINE);
        private final boolean noEntries;

        public ToggleEntryHeader(boolean noEntries) { this.noEntries = noEntries; }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(client.textRenderer, LINE, x+0, y+2, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, NAME, x+0+24, y+2, 0xFFFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, MODIFIER, x+entryWidth-40-80-4+15, y+2, 0xFFFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, KEYBIND, x+entryWidth-40+15, y+2, 0xFFFFFFFF);
            if (noEntries)
                context.drawCenteredTextWithShadow(client.textRenderer, NO_TOGGLES, x + (entryWidth/2), y+2+12, 0xFFFFFFFF);
        }
    }

    public class ToggleEntrySeparator extends TEntry {
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            context.drawCenteredTextWithShadow(client.textRenderer, "§nPrior Bound Toggles from this Profile", x + entryWidth/2, y+4, 0xFFFFFFFF);
        }
    }
    public class BlankSeparator extends TEntry {
        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {}
    }

    @Environment(EnvType.CLIENT)
    public class ToggleEntry extends TEntry {
        final Toggle toggle;
        final ButtonWidget modifier;
        final ButtonWidget key;
        final ButtonWidget remove;
        final String keyName;

        public ToggleEntry(Toggle toggle, String keyName) {
            this.toggle = toggle;
            this.keyName = keyName;
            this.modifier = ButtonWidget.builder(toggle.modifier.getBoundKeyTranslationKey().equals("key.keyboard.unknown") ?
                Text.literal("None") : toggle.modifier.getBoundKeyLocalizedText(), b -> {
                parent.selectedKeybind = toggle.modifier;
                update();
            }).size(80, 16).build();
            this.key = ButtonWidget.builder(toggle.key.getBoundKeyLocalizedText(), b -> {
                parent.selectedKeybind = toggle.key;
                update();
            }).size(80, 16).build();
            this.remove = ButtonWidget.builder(Text.literal("§c-"), b -> {
                parent.profile.toggles.remove(keyName);
                parent.refresh();
                ConfigManager.save();
            }).size(16, 16).build();
            this.remove.setTooltip(Tooltip.of(Text.literal("§cRemove")));
            this.key.active = !toggle.direct;
            this.modifier.active = !toggle.direct;
        }

        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
            Text lines = getLines();
            int linesLength = client.textRenderer.getWidth(lines);
            boolean isHovered = mY >= y && mY <= y+eHeight && mX >= x-(linesLength/2)-3 && mX <= x+eWidth+16;
            if (isHovered)
                context.fill(x-(linesLength/2)-3, y-1, x+eWidth+16, y+eHeight+3, 0x11ECECEC);

            context.drawTextWithShadow(client.textRenderer, toggle.getDisplayName(), x+0+24, y+4, 0xFFFFFFFF);

            if (!toggle.inProfile) {
                remove.setY(y);
                remove.setX(x + 2 - 10);
                remove.render(context, mX, mY, delta);
            }
            else
                context.drawCenteredTextWithShadow(client.textRenderer, lines, x+0, y+4, 0xFFFFFFFF);

            if (toggle.lines.size() > 2 && isHovered && mX <= x+(linesLength/2)+3)
                parent.setTooltip(Text.literal(StringUtils.join(toggle.lines, ", ")));



            modifier.setY(y);
            modifier.setX(x+eWidth-80-80-4+15);
            modifier.render(context, mX, mY, delta);

            key.setY(y);
            key.setX(x+eWidth-80+15);
            key.render(context, mX, mY, delta);
        }

        private Text getLines() {
            if (toggle.lines.size() == 1)
                return Text.literal(String.valueOf(toggle.lines.get(0)));
            else if (toggle.lines.size() == 2)
                return Text.literal(toggle.lines.get(0) + "," + toggle.lines.get(1));
            return Text.literal(toggle.lines.get(0) + "…");
        }

        @Override
        public void update() {
            modifier.setMessage(toggle.modifier.getBoundKeyTranslationKey().equals("key.keyboard.unknown") ? Text.literal("None") : toggle.modifier.getBoundKeyLocalizedText());
            if (parent.selectedKeybind == toggle.modifier)
                modifier.setMessage(Text.literal("> ")
                    .append(modifier.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));
            key.setMessage(toggle.key.getBoundKeyLocalizedText());
            if (parent.selectedKeybind == toggle.key)
                key.setMessage(Text.literal("> ")
                    .append(key.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));
        }

        @Override public List<? extends Selectable> selectableChildren() { return toggle.inProfile ? List.of(modifier, key) : List.of(remove, modifier, key); }
        @Override public List<? extends Element> children() { return toggle.inProfile ? List.of(modifier, key) : List.of(remove, modifier, key); }
    }
}