package com.minenash.customhud.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class NewConfigScreen extends Screen {

    private static final int OPTION_START = 32+13, OPTION_BUFFER = 30;
    private final Screen parent;
    private final TextRenderer font;

    private final KeyBinding binding = new KeyBinding("test", GLFW.GLFW_KEY_UNKNOWN, "Test Cat");

    private ButtonWidget keyButton;
    private boolean profileActive = false;
    private boolean inCycle = false;
    private boolean keybindSelected = false;

    public NewConfigScreen(Screen parent) {
        super(Text.translatable("sml.config.screen.title"));
        this.parent = parent;
        this.font = MinecraftClient.getInstance().textRenderer;
    }

    @Override
    protected void init() {
        example("○", 2+50, OPTION_START, 16, (button) -> {
            profileActive = !profileActive; button.setMessage(Text.literal(profileActive ? "☑" : "☐"));});
        example("Edit", width-34-50, OPTION_START, 32, (b) -> {});
        example("☐", width-34-82-18-50, OPTION_START, 16, (button) -> {
            inCycle = !inCycle; button.setMessage(Text.literal(inCycle ? "☑" : "☐"));});


        keyButton = example(I18n.translate(binding.getBoundKeyTranslationKey()), width-34-82-50, OPTION_START, 80, (b) -> {
            keybindSelected = true;
            update();
        });
    }

    private void update() {
        keyButton.setMessage(this.binding.getBoundKeyLocalizedText());
        if (keybindSelected)
//            keyButton.setMessage(keyButton.getMessage().copy().formatted(Formatting.YELLOW, Formatting.UNDERLINE));
            keyButton.setMessage(Text.literal("> ")
                    .append(keyButton.getMessage().copy().formatted(Formatting.WHITE, Formatting.UNDERLINE))
                    .append(" <").formatted(Formatting.YELLOW));

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (keybindSelected) {
            binding.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
            keybindSelected = false;
            update();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keybindSelected) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE)
                binding.setBoundKey(InputUtil.UNKNOWN_KEY);
            else
                binding.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode));
            keybindSelected = false;
            update();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private ButtonWidget example(String text, int x, int y, int width, ButtonWidget.PressAction action) {
        return addDrawableChild(ButtonWidget.builder(Text.literal(text), action)
                .dimensions(x, y, width, 16)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(font, Text.translatable("config.custom_hud.title"), this.width / 2, 13, 0xFFFFFF);

//        context.drawTextWithShadow(font, Text.literal("○"), 2, OPTION_START + 5, 0xFFFFFF);
        context.drawTextWithShadow(font, Text.literal("Profile 1"), 20+50, OPTION_START + 5, 0xFFFFFF);
//        context.drawTextWithShadow(font, Text.literal("☐"), width-42-42-22, OPTION_START + 5, 0xFFFFFF);

        super.render(context, mouseX, mouseY, delta);
    }
}
