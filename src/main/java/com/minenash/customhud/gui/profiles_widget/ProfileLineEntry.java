package com.minenash.customhud.gui.profiles_widget;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.gui.customize.CustomizeScreen;
import com.minenash.customhud.gui.ErrorsScreen;
import com.minenash.customhud.gui.NewConfigScreen.Mode;
//import com.minenash.customhud.gui.editor.EditorWindow;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ProfileLineEntry extends LineEntry {

    public final ButtonWidget selected, cycled, customize;
    private final ButtonWidget keybind, edit, error;
    private final ButtonWidget delete, up, down;
    public final TextFieldWidget editName;
    private final ProfileLinesWidget widget;

    public final Profile profile;
    private String displayName;

    public ProfileLineEntry(Profile profile, ProfileLinesWidget widget) {
        this.profile = profile;
        this.widget = widget;
        this.displayName = profile.name;

        this.selected = button(ProfileManager.getActive() == profile ? "☑" : "☐", "Swap to this profile", 16, (b) -> {
            ProfileManager.setActive(profile);
            b.setTooltip(Tooltip.of(Text.literal(ProfileManager.getActive() == profile ? "Turn off this profile" : "Swap to this profile")));
        });

//        String editText = "Will open in your text editor\n\n Not opening? Shift-click to edit in game";

        this.edit = button("Edit", ProfileManager.openTooltipStr, 40, (b) -> {
            ProfileManager.open(profile);
        });
        this.cycled = button(profile.cycle ? "☑" : "☐", "Include this profile in the profile cycle", 16, (b) -> {
            profile.cycle = !profile.cycle;
            b.setMessage(Text.literal(profile.cycle ? "☑" : "☐"));
        });

        this.keybind = button(profile.keyBinding.getBoundKeyLocalizedText().getString(), "Keybind to switch to this profile", 80, (b) -> {
            widget.screen.selectedKeybind = profile.keyBinding;
            widget.update();
        });
        int errors = Errors.getErrors(profile.name).size();
        int toggles = profile.toggles.size();
        int options = profile.options.size();

        String customizeTooltip = options == 0 ? "" : options + (options == 1 ? " Option" : " Options");
        if (toggles > 0 && options > 0)
            customizeTooltip += "\n";
        customizeTooltip += toggles == 0 ? "" : toggles + (toggles == 1 ? " Toggle" : " Toggles");

        this.error = button("§c!", "§c" + (errors == 1 ? "1 Error Found" : errors + " Errors Found") , 16, (b) -> CLIENT.setScreen(new ErrorsScreen(widget.screen, profile)));
        this.customize = button("\uD83D\uDD8C", customizeTooltip, 32, (b) -> CLIENT.setScreen(new CustomizeScreen(widget.screen, profile, null)));
        this.delete = button("§cDelete", "§cThis Can't Be §nUndone!!!!", 48, (b) -> widget.deleteProfile(this));
        this.up = button("§a↑", 16, b -> widget.move(this, -1));
        this.down = button("§c↓", 16, b -> widget.move(this, 1));

        this.editName = new TextFieldWidget(CLIENT.textRenderer, 0, 0, 200, 16, Text.literal("Edit Name"));
        this.editName.setText(profile.name);
        this.editName.setTooltip(Tooltip.of(Text.literal("Click to edit name")));
        this.editName.setFocusUnlocked(true);
        this.editName.setTextPredicate( str -> {
            for (int i = 0; i < str.length(); i++)
                if (invalidCharacters.contains(str.charAt(i)))
                    return false;
            return true;
        });
        this.editName.setChangedListener((n) -> widget.screen.editing = this);
    }

    private static final List<Character> invalidCharacters = List.of('\\', '/', ':', '*', '?', '"', '<', '>', '|');

    public void update() {
        keybind.setMessage(this.profile.keyBinding.getBoundKeyLocalizedText());
        if (widget.screen.selectedKeybind == profile.keyBinding)
//            keyButton.setMessage(keyButton.getMessage().copy().formatted(Formatting.YELLOW, Formatting.UNDERLINE));
            keybind.setMessage(Text.literal("> ")
                    .append(keybind.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));

        editName.setFocused(false);
        if (!editName.getText().equals(profile.name)) {
            ProfileManager.rename(profile, editName.getText());
        }
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
        customize.active = !profile.toggles.isEmpty() || !profile.options.isEmpty();
        editName.setX(x + 16 + 20);
        editName.setY(y);
        editName.setWidth(eWidth - 16 - 20 - 16 - 42 - 82 - 18 - (customize.active ? 34 : 0) - 3);

        if (editName.isSelected() || editName.isMouseOver(mX, mY))
            editName.render(context, mX, mY, delta);
        else {
            if (!editName.getText().equals(profile.name)) {
                ProfileManager.rename(profile, editName.getText());
                displayName = profile.name;
            }
            context.drawTextWithShadow(CLIENT.textRenderer, truncateName(x, eWidth), x + 16 + 20 + 4, y + 4, 0xFFFFFF);
        }

        selected.setMessage(Text.literal(ProfileManager.getActive() == profile ? "☑" : "☐"));
        posAndRender(context, mX, mY, delta, x, y, eWidth, selected, 2);

        if (widget.screen.mode == Mode.DELETE) {
            posAndRender(context, mX, mY, delta, x, y, eWidth, delete, -16-42);
            return;
        }
        if (widget.screen.mode == Mode.REORDER) {
            down.active = widget.children().get(widget.children().size()-2) != this;
            down.setMessage(Text.literal(down.active ? "§c↓" : "§4↓"));
            posAndRender(context, mX, mY, delta, x, y, eWidth, down, -16-18);

            up.active = widget.children().get(0) != this;
            up.setMessage(Text.literal(up.active ? "§a↑" : "§2↑"));
            posAndRender(context, mX, mY, delta, x, y, eWidth, up, -16-18-18);
            return;
        }

        if (Errors.hasErrors(profile.name))
            posAndRender(context, mX, mY, delta, x, y, eWidth, error, -16);
        posAndRender(context, mX, mY, delta, x, y, eWidth, edit, -16-42);
        posAndRender(context, mX, mY, delta, x, y, eWidth, keybind, -16-42-82);
        posAndRender(context, mX, mY, delta, x, y, eWidth, cycled, -16-42-82-18);
        if (customize.active)
            posAndRender(context, mX, mY, delta, x, y, eWidth, customize, -16-42-82-18-34);

    }

    private String truncateName(int x, int eWidth) {
        String name = displayName;
        int width = CLIENT.textRenderer.getWidth(name);
        int maxWidth = x + eWidth + switch (widget.screen.mode) {
            case NORMAL ->  -16-42-82-18-(profile.toggles.isEmpty() ? 0 : 50);
            case REORDER -> -16-18-18;
            case DELETE -> -16-42;
        } - 2 - (x + 16 + 20 + 4);
        if (maxWidth > width)
            return name;

        maxWidth -= CLIENT.textRenderer.getWidth("…") + 2;

        while(width > maxWidth) {
            name = name.substring(0, name.length() - 1);
            width = CLIENT.textRenderer.getWidth(name);
        }
        return name + "…";
    }

    @Override public List<? extends Selectable> selectableChildren() { return widgets(); }
    @Override public List<? extends Element> children() { return widgets(); }
    public List<ClickableWidget> widgets() {
        List<ClickableWidget> widgets = new ArrayList<>(6);
        widgets.add(selected);
        widgets.add(editName);
        if (widget.screen.mode == Mode.DELETE)
            widgets.add(delete);
        else if (widget.screen.mode == Mode.REORDER) {
            widgets.add(up);
            widgets.add(down);
        }
        else {
//            if (!profile.toggles.isEmpty())
                widgets.add(customize);
            widgets.add(cycled);
            widgets.add(keybind);
            widgets.add(edit);
            if (Errors.hasErrors(profile.name))
                widgets.add(error);
        }
        return widgets;
    }

}
