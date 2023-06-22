package com.minenash.customhud.mc1_20;

import com.minenash.customhud.mc1_20.errors.ErrorScreen;
import com.minenash.customhud.core.errors.Errors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }

    private static class ConfigScreen extends Screen {

        private final Screen parent;
        private final TextRenderer font;
        private static final int OPTION_START = 32+13, OPTION_BUFFER = 30;

        protected ConfigScreen(Screen parent) {
            super(Text.translatable("sml.config.screen.title"));
            this.parent = parent;
            this.font = MinecraftClient.getInstance().textRenderer;
        }

        @Override
        protected void init() {

            int buttonWidth = 75;
            int buttonX = this.width - buttonWidth - 10;
            int mid = this.width/2;

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.custom_hud.done"), button -> close())
                    .dimensions(mid - 100, this.height - 28, 200, 20).build());

            this.addDrawableChild(ButtonWidget.builder(Text.translatable(getEnableDisableLabel(CustomHud.enabled)), button -> {
                CustomHud.enabled = !CustomHud.enabled;
                button.setMessage(Text.translatable(getEnableDisableLabel(CustomHud.enabled)));
            }).dimensions(buttonX, OPTION_START, buttonWidth, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.literal(Integer.toString(CustomHud.activeProfile)), (button) -> {
                CustomHud.activeProfile = CustomHud.activeProfile == 3 ? 1 : CustomHud.activeProfile + 1;
                button.setMessage(Text.literal(Integer.toString(CustomHud.activeProfile)));
            }).dimensions(buttonX, OPTION_START + OPTION_BUFFER, buttonWidth, 20).build());

            for (int i = 1; i <= 3; i++) {
                int ii = i;
                this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.custom_hud.open_profile", i), (button) ->
                        new Thread(() -> Util.getOperatingSystem().open(CustomHud.getProfilePath(ii).toFile())).start()
                ).dimensions(mid - 75, OPTION_START + OPTION_BUFFER * (i + 1) + 5, 150, 20).build());
            }

            for (int i = 1; i <= 3; i++) {
                int ii = i;
                if (Errors.hasErrors(i))
                    this.addDrawableChild(ButtonWidget.builder(Text.literal("!").formatted(Formatting.RED), (button) ->
                            client.setScreen(new ErrorScreen(client.currentScreen, ii)))
                        .dimensions(mid + 85, OPTION_START + OPTION_BUFFER * (i + 1) + 5, 20, 20)
                        .tooltip(Tooltip.of(Text.literal(Errors.getErrors(ii).size() + " Errors Found").formatted(Formatting.RED)))
                        .build());
            }

            this.addDrawableChild(ButtonWidget.builder(Text.translatable("config.custom_hud.done"), (button) -> close()).dimensions(mid - 100, this.height - 28, 200, 20).build());
        }

        private String getEnableDisableLabel(boolean value) {
            return value ? "config.custom_hud.enabled" : "config.custom_hud.disabled";
        }

        @Override
        public void close() {
            CustomHud.justSaved = true;
            CustomHud.saveConfig();
            MinecraftClient.getInstance().setScreen(parent);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context);
            if (MinecraftClient.getInstance().cameraEntity == null)
                context.fill(0,36,this.width,this.height - 30-9, 0x88000000);

            context.drawCenteredTextWithShadow(font, Text.translatable("config.custom_hud.title"), this.width / 2, 13, 0xFFFFFF);
            context.drawTextWithShadow(font, Text.translatable("config.custom_hud.enable.label"), 20, OPTION_START + 5, 0xFFFFFF);
            context.drawTextWithShadow(font,  Text.translatable("config.custom_hud.active_profile.label"), 20, OPTION_START + OPTION_BUFFER + 5, 0xFFFFFF);

            super.render(context,mouseX, mouseY, delta);
        }

    }
}
