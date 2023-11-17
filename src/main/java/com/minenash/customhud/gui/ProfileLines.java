package com.minenash.customhud.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class ProfileLines extends ElementListWidget<ProfileLineWidget> {
    public ProfileLines(MinecraftClient client, int width, int height, int startY, int endY, int idk) {
        super(client, width, height, startY, endY, idk);

        this.addEntry(new ProfileLineWidget("Profile 1", new KeyBinding("test1", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
        this.addEntry(new ProfileLineWidget("Profile 2", new KeyBinding("test2", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
        this.addEntry(new ProfileLineWidget("Profile 3", new KeyBinding("test3", GLFW.GLFW_KEY_UNKNOWN, "Test"), this));
    }
}
