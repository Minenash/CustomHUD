package com.minenash.customhud.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ProfileLineEntry extends ElementListWidget.Entry<ProfileLineEntry> {

    private final KeyBinding binding;
    private final Text name;

    private final ButtonWidget selected, cycled, keybind, edit;

    private final ProfileLinesWidget widget;
    private boolean inCycle = false;

    public ProfileLineEntry(String name, KeyBinding keyBinding, ProfileLinesWidget widget) {
        this.binding = keyBinding;
        this.name = Text.literal(name);
        this.widget = widget;

        this.selected = button("☐", 16, (b) -> {widget.screen.active = this;});
        this.edit = button("Edit", 32, (b) -> {});
        this.cycled = button("☐", 16, (b) -> {inCycle = !inCycle; b.setMessage(Text.literal(inCycle ? "☑" : "☐"));});
        this.keybind = button(I18n.translate(binding.getBoundKeyTranslationKey()), 80, (b) -> {
            widget.screen.selectedKeybind = binding;
            widget.update();
        });
    }

    void update() {
        keybind.setMessage(this.binding.getBoundKeyLocalizedText());
        if (widget.screen.selectedKeybind == binding)
//            keyButton.setMessage(keyButton.getMessage().copy().formatted(Formatting.YELLOW, Formatting.UNDERLINE));
            keybind.setMessage(Text.literal("> ")
                    .append(keybind.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));

    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
        context.drawTextWithShadow(CLIENT.textRenderer, name, x+20, y+4, 0xFFFFFF);
        selected.setMessage(Text.literal(widget.screen.active == this ? "☑" : "☐"));

        setPosAndRender(context, mX, mY, delta, x, y, eWidth, selected, 2);
        setPosAndRender(context, mX, mY, delta, x, y, eWidth, edit, -34);
        setPosAndRender(context, mX, mY, delta, x, y, eWidth, keybind, -34-82);
        setPosAndRender(context, mX, mY, delta, x, y, eWidth, cycled, -34-82-18);
    }

    @Override public List<? extends Selectable> selectableChildren() { return List.of(selected, cycled, keybind, edit); }
    @Override public List<? extends Element> children() { return List.of(selected, cycled, keybind, edit); }

    private static ButtonWidget button(String text, int width, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(text), action).dimensions(0, 0, width, 16).build();
    }

    private void setPosAndRender(DrawContext context, int mouseX, int mouseY, float delta, int x, int y, int width, ButtonWidget widget, int xOffset) {
        widget.setX(x + (xOffset < 0 ? width + xOffset : xOffset));
        widget.setY(y);
        widget.render(context, mouseX, mouseY, delta);
    }
}
