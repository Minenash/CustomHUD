package com.minenash.customhud.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ProfileLineWidget extends ElementListWidget.Entry<ProfileLineWidget> {

    private final KeyBinding binding;
    private final String name;

    private final ButtonWidget selected, cycled, keybind, edit;

    private int screenWidth = 0;
    private boolean profileActive = false;
    private boolean inCycle = false;
    private boolean keybindSelected = false;

    public ProfileLineWidget(String name, KeyBinding keyBinding, ElementListWidget screen) {
        this.binding = keyBinding;
        this.name = name;
        this.screenWidth = screen.getRowWidth();

        this.selected = button("☐", 16, (b) -> {profileActive = !profileActive; b.setMessage(Text.literal(profileActive ? "☑" : "☐"));});
        this.edit = button("edit", 32, (b) -> {});
        this.cycled = button("☐", 16, (b) -> {inCycle = !inCycle; b.setMessage(Text.literal(inCycle ? "☑" : "☐"));});
        this.keybind = button(I18n.translate(binding.getBoundKeyTranslationKey()), 80, (b) -> {
            keybindSelected = true;
            update();
        });
    }

    private void update() {
        keybind.setMessage(this.binding.getBoundKeyLocalizedText());
        if (keybindSelected)
//            keyButton.setMessage(keyButton.getMessage().copy().formatted(Formatting.YELLOW, Formatting.UNDERLINE));
            keybind.setMessage(Text.literal("> ")
                    .append(keybind.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));

    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        context.drawTextWithShadow(CLIENT.textRenderer, Text.literal("Profile 1"), x+20, y+5, 0xFFFFFF);

        setPosAndRender(context, mouseX, mouseY, tickDelta, y, selected, 2);
        setPosAndRender(context, mouseX, mouseY, tickDelta, y, cycled, -34);
        setPosAndRender(context, mouseX, mouseY, tickDelta, y, keybind, -34-82);
        setPosAndRender(context, mouseX, mouseY, tickDelta, y, edit, -34-82-18);

    }

    @Override public List<? extends Selectable> selectableChildren() { return List.of(selected, cycled, keybind, edit); }
    @Override public List<? extends Element> children() { return List.of(selected, cycled, keybind, edit); }

    private static ButtonWidget button(String text, int width, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(text), action).dimensions(0, 0, width, 16).build();
    }

    private void setPosAndRender(DrawContext context, int mouseX, int mouseY, float delta, int y, ButtonWidget widget, int x) {
        widget.setX(x < 0 ? screenWidth - x - 50 : x + 50);
        widget.setY(y);
        widget.render(context, mouseX, mouseY, delta);
    }
}
