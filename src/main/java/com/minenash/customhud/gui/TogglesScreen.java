package com.minenash.customhud.gui;

import com.minenash.customhud.ConfigManager;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class TogglesScreen extends Screen {

    private ToggleListWidget listWidget = null;
    private final Screen parent;
    private Profile profile;
    public KeyBinding selectedKeybind;

    public TogglesScreen(Screen parent, Profile profile) {
        super(Text.literal("§nProfile Toggles"));
        this.parent = parent;
        this.profile = profile;
    }


    public void changeProfile(Profile profile) {
        this.profile = profile;
        init();
    }

    public void init() {
        children().clear();
        this.listWidget = new ToggleListWidget(profile);
        this.addSelectableChild(listWidget);

        this.addDrawableChild( ButtonWidget.builder(Text.literal("Open Profile"), button -> ProfileManager.open(profile))
                .position(this.width / 2 - 155, this.height - 26).size(150, 20).build() );

        this.addDrawableChild( ButtonWidget.builder(ScreenTexts.DONE, button -> CLIENT.setScreen(parent))
                .position(this.width / 2 - 155 + 160, this.height - 26).size(150, 20).build() );

        super.init();
    }

    @Override
    public void close() {
        CLIENT.setScreen(parent);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        this.listWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, profile.name, this.width / 2, 24, 16777215);

//        int x = this.width / 2;
//        context.fill(x - 30, 47, x + 30, 48, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
            selectedKeybind = null;
            for (ToggleListWidget.TEntry e : listWidget.children())
                e.update();
            ConfigManager.save();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(keyCode == 256 ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode));
            selectedKeybind = null;
            for (ToggleListWidget.TEntry e : listWidget.children())
                e.update();
            ConfigManager.save();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    class ToggleListWidget extends ElementListWidget<ToggleListWidget.TEntry> {

        public ToggleListWidget(Profile profile) {
            super(CLIENT, TogglesScreen.this.width, TogglesScreen.this.height - 36 + 4 - 40, 40, TogglesScreen.this.height - 36 + 4, 18);

            boolean noEntries = profile == null || profile.toggles.values().isEmpty();

            this.addEntry( new ToggleEntryHeader(noEntries) );

            if (noEntries)
                return;

            for (var e :profile.toggles.entrySet())
                if (e.getValue().inProfile)
                    this.addEntry(new ToggleEntry(e.getValue(), e.getKey()));
            this.addEntry(new ToggleEntrySeparator());
            for (var e :profile.toggles.entrySet())
                if (!e.getValue().inProfile)
                    this.addEntry(new ToggleEntry(e.getValue(), e.getKey()));

            if (children().get(children().size()-1) instanceof ToggleEntrySeparator)
                children().remove(children().size()-1);

        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 64;
        }

        @Override
        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 32;
        }

        @Override public void appendNarrations(NarrationMessageBuilder builder) {}

        public abstract class TEntry extends ElementListWidget.Entry<TEntry> {
            public void update() {}
            @Override public List<? extends Selectable> selectableChildren() { return Collections.emptyList(); }
            @Override public List<? extends Element> children() { return Collections.emptyList(); }
        }

        public class ToggleEntryHeader extends TEntry {
            private static final Text LINE = Text.literal("Line").formatted(Formatting.UNDERLINE);
            private static final Text NAME = Text.literal("Name").formatted(Formatting.UNDERLINE);
            private static final Text KEYBIND = Text.literal("Key").formatted(Formatting.UNDERLINE);
            private static final Text NO_TOGGLES = Text.literal("This profiles has no toggles").formatted(Formatting.UNDERLINE);
            private final boolean noEntries;

            public ToggleEntryHeader(boolean noEntries) { this.noEntries = noEntries; }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawCenteredTextWithShadow(textRenderer, LINE, x + 15, y+2, 0xFFFFFFFF);
                context.drawTextWithShadow(textRenderer, NAME, x+15+24, y+2, 0xFFFFFFFF);
                context.drawCenteredTextWithShadow(textRenderer, KEYBIND, x+entryWidth-40, y+2, 0xFFFFFFFF);
                if (noEntries)
                    context.drawCenteredTextWithShadow(textRenderer, NO_TOGGLES, x + (entryWidth/2), y+2+12, 0xFFFFFFFF);
            }
        }

        public class ToggleEntrySeparator extends TEntry {
            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawCenteredTextWithShadow(textRenderer, "§nPrior Bound Toggles from this Profile", x + entryWidth/2, y+4, 0xFFFFFFFF);
            }
        }

        @Environment(EnvType.CLIENT)
        public class ToggleEntry extends TEntry {
            final Toggle toggle;
            final ButtonWidget keybind;
            final ButtonWidget remove;
            final String keyName;

            public ToggleEntry(Toggle toggle, String keyName) {
                this.toggle = toggle;
                this.keyName = keyName;
                this.keybind = ButtonWidget.builder(toggle.keyBinding.getBoundKeyLocalizedText(), b -> {
                    selectedKeybind = toggle.keyBinding;
                    update();
                }).size(80, 16).build();
                this.remove = ButtonWidget.builder(Text.literal("§c-"), b -> {
                    profile.toggles.remove(keyName);
                    init();
                    ConfigManager.save();
                }).size(16, 16).build();
                this.remove.setTooltip(Tooltip.of(Text.literal("§cRemove")));
                this.keybind.active = !toggle.direct;
            }

            public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
                context.drawTextWithShadow(textRenderer, toggle.getDisplayName(), x+15+24, y+4, 0xFFFFFFFF);

                if (!toggle.inProfile) {
                    remove.setY(y);
                    remove.setX(x + 7);
                    remove.render(context, mX, mY, delta);
                }
                else
                    context.drawCenteredTextWithShadow(textRenderer, getLines(), x+15, y+4, 0xFFFFFFFF);

                if (toggle.lines.size() > 2 && hovered && mX > x && mX < x+30)
                    setTooltip(Text.literal(StringUtils.join(toggle.lines, ", ")));

                keybind.setY(y);
                keybind.setX(x+eWidth-80);
                keybind.render(context, mX, mY, delta);
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
                keybind.setMessage(toggle.keyBinding.getBoundKeyLocalizedText());
                if (selectedKeybind == toggle.keyBinding)
                    keybind.setMessage(Text.literal("> ")
                            .append(keybind.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                            .append(" <").formatted(Formatting.YELLOW));
            }

            @Override public List<? extends Selectable> selectableChildren() { return toggle.inProfile ? List.of(keybind) : List.of(remove, keybind); }
            @Override public List<? extends Element> children() { return toggle.inProfile ? List.of(keybind) : List.of(remove, keybind); }
        }
    }
}