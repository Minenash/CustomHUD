package com.minenash.customhud.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ProfileLinesWidget extends ElementListWidget<ProfileLineEntry> {

    public final NewConfigScreen screen;

    public ProfileLinesWidget(NewConfigScreen screen, int startY, int endY) {
        super(MinecraftClient.getInstance(), screen.width, endY - startY, startY, endY, 20);

        this.screen = screen;

        this.addEntry(new ProfileLineEntry("Profile 1", new KeyBinding("test1", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
        this.addEntry(new ProfileLineEntry("Profile 2", new KeyBinding("test2", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
        this.addEntry(new ProfileLineEntry("Profile 3", new KeyBinding("test3", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
    }

    public void update() {
        for (ProfileLineEntry widget : children())
            widget.update();
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 64;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }


}
