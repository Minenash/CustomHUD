package com.minenash.customhud.gui.profiles_widget;

import com.minenash.customhud.gui.NewConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ProfileLinesWidget extends ElementListWidget<LineEntry> {

    public final NewConfigScreen screen;
    public int index = 4;
    public boolean deleteMode = false;

    public ProfileLinesWidget(NewConfigScreen screen, int startY, int endY) {
        super(MinecraftClient.getInstance(), screen.width, endY - startY, startY, endY, 20);

        this.screen = screen;

        this.addEntry(new ProfileLineEntry("Profile 1", new KeyBinding("test1", GLFW.GLFW_KEY_UNKNOWN, "Test"), this, true, false));
        this.addEntry(new ProfileLineEntry("Profile 2", new KeyBinding("test2", GLFW.GLFW_KEY_UNKNOWN, "Test"), this, false, true));
        this.addEntry(new ProfileLineEntry("Profile 3", new KeyBinding("test3", GLFW.GLFW_KEY_UNKNOWN, "Test"), this, false, false));
        this.addEntry(new LineEntry.NewProfile(this));
    }

    public void update() {
        for (LineEntry widget : children())
            widget.update();
    }

    public void newProfile() {
        children().add(children().size()-1, new ProfileLineEntry("Profile " + index, new KeyBinding("test" + index++, GLFW.GLFW_KEY_UNKNOWN, "Test"), this, false, false));
    }

    public void deleteProfile(LineEntry entry) {
        children().remove(entry);
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 96 + 32;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 48 + 16;
    }


}
