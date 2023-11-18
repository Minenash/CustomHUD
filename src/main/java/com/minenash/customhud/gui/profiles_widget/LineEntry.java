package com.minenash.customhud.gui.profiles_widget;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.List;

public abstract class LineEntry extends ElementListWidget.Entry<LineEntry> {
    public void update() {}

    protected void posAndRender(DrawContext context, int mouseX, int mouseY, float delta, int x, int y, int width, ButtonWidget widget, int xOffset) {
        widget.setX(x + (xOffset < 0 ? width + xOffset : xOffset + 16));
        widget.setY(y);
        widget.render(context, mouseX, mouseY, delta);
    }

    protected static ButtonWidget button(String text, int width, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(text), action).dimensions(0, 0, width, 16).build();
    }
    protected static ButtonWidget button(String text, String tooltip, int width, ButtonWidget.PressAction action) {
        return ButtonWidget.builder(Text.literal(text), action).dimensions(0, 0, width, 16)
                .tooltip(Tooltip.of(Text.literal(tooltip))).build();
    }

    public static class NewProfile extends LineEntry {

        private final ProfileLinesWidget parent;
        private final ButtonWidget newProfileButton;
        private final ButtonWidget deleteProfileButton;
        private final ButtonWidget deleteDoneButton;

        public NewProfile(ProfileLinesWidget parent) {
            this.parent = parent;
            this.newProfileButton = button("§a+§f New", 48, b -> parent.newProfile());
            this.deleteProfileButton = button("§c-§f Delete", 64, b -> parent.deleteMode = true);
            this.deleteDoneButton = button("§a✔§f Done", 56, b -> parent.deleteMode = false);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
            if (parent.deleteMode)
                posAndRender(context, mX, mY, delta, x, y, eWidth, deleteDoneButton, 2);
            else {
                posAndRender(context, mX, mY, delta, x, y, eWidth, newProfileButton, 2);
                posAndRender(context, mX, mY, delta, x, y, eWidth, deleteProfileButton, 2 + 50);
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            if (parent.deleteMode)
                return List.of(deleteDoneButton);
            return List.of(newProfileButton, deleteProfileButton);
        }
        @Override
        public List<? extends Element> children() {
            if (parent.deleteMode)
                return List.of(deleteDoneButton);
            return List.of(newProfileButton, deleteProfileButton);
        }
    }


}
