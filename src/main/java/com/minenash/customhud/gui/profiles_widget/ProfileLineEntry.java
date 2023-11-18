package com.minenash.customhud.gui.profiles_widget;

import com.minenash.customhud.gui.ErrorScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ProfileLineEntry extends LineEntry {

    private final KeyBinding binding;
    private Text name;

    private final ButtonWidget selected, cycled, keybind, edit, error, keys, delete;
    private TextFieldWidget editName;
    private boolean startedEditing = false;

    private final ProfileLinesWidget widget;
    private boolean inCycle = false;
    private final boolean hasToggles;
    private final boolean hasErrors;

    public ProfileLineEntry(String name, KeyBinding keyBinding, ProfileLinesWidget widget, boolean hasToggles, boolean hasErrors) {
        this.binding = keyBinding;
        this.name = Text.literal(name);
        this.widget = widget;
        this.hasToggles = hasToggles;
        this.hasErrors = hasErrors;

        this.selected = button("☐", "Swap to this profile", 16, (b) -> {
            widget.screen.active = widget.screen.active == this ? null : this;

            b.setTooltip(Tooltip.of(Text.literal(widget.screen.active == this ? "Turn off this profile" : "Swap to this profile")));
        });
        this.edit = button("Edit", "Will open in your text editor", 40, (b) -> {});
        this.cycled = button("☐", "Include this profile in the profile cycle", 16, (b) -> {
            inCycle = !inCycle;
            b.setMessage(Text.literal(inCycle ? "☑" : "☐"));
        });
        this.keybind = button(I18n.translate(binding.getBoundKeyTranslationKey()), "Keybind to switch to this profile", 80, (b) -> {
            widget.screen.selectedKeybind = binding;
            widget.update();
        });
        this.error = button("§c!", "§c5 Errors Found", 16, (b) -> CLIENT.setScreen(new ErrorScreen(widget.screen)));
        this.keys = button("Toggles", "3 Toggles in the profile",48, (b) -> CLIENT.setScreen(new ErrorScreen(widget.screen)));
        this.delete = button("§cDelete", "&cThis Can't Be §nUndone!!!!", 48, (b) -> widget.deleteProfile(this));

        this.editName = new TextFieldWidget(CLIENT.textRenderer, 0, 0, 200, 16, Text.literal("Edit Name"));
        this.editName.setText(name);
        this.editName.setTooltip(Tooltip.of(Text.literal("Click to edit name")));
        this.editName.setFocusUnlocked(true);
        this.editName.setChangedListener((n) -> {
            this.name = Text.literal(n);
            this.startedEditing = true;
            widget.screen.editing = this;
        });
    }

    public void update() {
        keybind.setMessage(this.binding.getBoundKeyLocalizedText());
        if (widget.screen.selectedKeybind == binding)
//            keyButton.setMessage(keyButton.getMessage().copy().formatted(Formatting.YELLOW, Formatting.UNDERLINE));
            keybind.setMessage(Text.literal("> ")
                    .append(keybind.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));

        editName.setFocused(false);
//        if (widget.screen.editing != this || !startedEditing)
//            editName.setFocused(false);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
        editName.setX(x + 16 + 20);
        editName.setY(y);
        editName.setWidth(eWidth - 16 - 20 - 16 - 42 - 82 - 18 - (hasToggles ? 50 : 0) - 3);

        if (editName.isSelected() || editName.isMouseOver(mX, mY))
            editName.render(context, mX, mY, delta);
        else
            context.drawTextWithShadow(CLIENT.textRenderer, name, x+16+20+4, y+4, 0xFFFFFF);

        selected.setMessage(Text.literal(widget.screen.active == this ? "☑" : "☐"));
        posAndRender(context, mX, mY, delta, x, y, eWidth, selected, 2);

        if (widget.deleteMode) {
            posAndRender(context, mX, mY, delta, x, y, eWidth, delete, -16-42);
            return;
        }

        if (hasErrors)
            posAndRender(context, mX, mY, delta, x, y, eWidth, error, -16);
        posAndRender(context, mX, mY, delta, x, y, eWidth, edit, -16-42);
        posAndRender(context, mX, mY, delta, x, y, eWidth, keybind, -16-42-82);
        posAndRender(context, mX, mY, delta, x, y, eWidth, cycled, -16-42-82-18);
        if (hasToggles)
            posAndRender(context, mX, mY, delta, x, y, eWidth, keys, -16-42-82-18-50);

    }

    @Override public List<? extends Selectable> selectableChildren() { return widgets(); }
    @Override public List<? extends Element> children() { return widgets(); }
    public List<ClickableWidget> widgets() {
        List<ClickableWidget> widgets = new ArrayList<>(6);
        widgets.add(selected);
        widgets.add(editName);
        if (widget.deleteMode)
            widgets.add(delete);
        else {
            if (hasToggles)
                widgets.add(keys);
            widgets.add(cycled);
            widgets.add(keybind);
            widgets.add(edit);
            if (hasErrors)
                widgets.add(error);
        }
        return widgets;
    }


}
