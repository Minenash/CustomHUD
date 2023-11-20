package com.minenash.customhud.gui.profiles_widget;

import com.minenash.customhud.gui.NewConfigScreen.Mode;
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
        private final ButtonWidget newProfile;
        private final ButtonWidget reorderProfiles;
        private final ButtonWidget deleteProfiles;
        private final ButtonWidget deleteDone;

        public NewProfile(ProfileLinesWidget parent) {
            this.parent = parent;
            this.newProfile = button("§a+§f New", 48, b -> parent.newProfile());
            this.reorderProfiles = button("§6⇵§f Reorder", 72, b -> parent.screen.mode = Mode.REORDER);
            this.deleteProfiles = button("§c-§f Delete", 64, b -> parent.screen.mode = Mode.DELETE);
            this.deleteDone = button("§a✔§f Done", 56, b -> {
                if (parent.screen.mode == Mode.REORDER)
                    parent.doneMoving();
                parent.screen.mode = Mode.NORMAL;
            });
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int eWidth, int eHeight, int mX, int mY, boolean hovered, float delta) {
            if (parent.screen.mode != Mode.NORMAL)
                posAndRender(context, mX, mY, delta, x, y, eWidth, deleteDone, 2);
            else {
                posAndRender(context, mX, mY, delta, x, y, eWidth, newProfile, 2);
                posAndRender(context, mX, mY, delta, x, y, eWidth, reorderProfiles, 2 + 50);
                posAndRender(context, mX, mY, delta, x, y, eWidth, deleteProfiles, 2 + 50 + 74);
            }
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            if (parent.screen.mode != Mode.NORMAL)
                return List.of(deleteDone);
            return List.of(newProfile, reorderProfiles, deleteProfiles);
        }
        @Override
        public List<? extends Element> children() {
            if (parent.screen.mode != Mode.NORMAL)
                return List.of(deleteDone);
            return List.of(newProfile, reorderProfiles, deleteProfiles);
        }
    }


}
