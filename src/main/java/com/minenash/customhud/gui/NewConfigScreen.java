package com.minenash.customhud.gui;

import com.minenash.customhud.ConfigManager;
import com.minenash.customhud.gui.profiles_widget.ProfileLineEntry;
import com.minenash.customhud.gui.profiles_widget.ProfileLinesWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import static com.minenash.customhud.CustomHud.CLIENT;

public class NewConfigScreen extends Screen {

    private final Screen parent;
    private final TextRenderer font;

    private ProfileLinesWidget profiles;
    public KeyBinding selectedKeybind;
    public ProfileLineEntry editing;

    public enum Mode {NORMAL, REORDER, DELETE}
    public Mode mode = Mode.NORMAL;

    public NewConfigScreen(Screen parent) {
        super(Text.translatable("sml.config.screen.title"));
        this.parent = parent;
        this.font = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    protected void init() {
        profiles = new ProfileLinesWidget(this,30, height-20);
        addSelectableChild(profiles);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(font, Text.translatable("config.custom_hud.title"), this.width / 2, 11, 0xFFFFFF);
        profiles.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
            selectedKeybind = null;
            profiles.update();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(keyCode == 256 ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode));
            selectedKeybind = null;
            profiles.update();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        CLIENT.setScreen(parent);
        profiles.update();
        ConfigManager.save();
    }
}
