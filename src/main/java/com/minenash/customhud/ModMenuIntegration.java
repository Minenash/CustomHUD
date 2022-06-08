package com.minenash.customhud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
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
            super(MutableText.of(new TranslatableTextContent("sml.config.screen.title")));
            this.parent = parent;
            this.font = MinecraftClient.getInstance().textRenderer;
        }

        @Override
        protected void init() {

            int buttonWidth = 75;
            int buttonX = this.width - buttonWidth - 10;
            int mid = this.width/2;

            this.addDrawableChild(new ButtonWidget(buttonX,OPTION_START,buttonWidth,20,
                    MutableText.of(new TranslatableTextContent(getEnableDisableLabel(CustomHud.enabled))), (button) -> {
                CustomHud.enabled = !CustomHud.enabled;
                button.setMessage(MutableText.of(new TranslatableTextContent(getEnableDisableLabel(CustomHud.enabled))));
            }));

            this.addDrawableChild(new ButtonWidget(buttonX,OPTION_START + OPTION_BUFFER,buttonWidth,20,
                    MutableText.of(new LiteralTextContent(Integer.toString(CustomHud.activeProfile))), (button) -> {
                CustomHud.activeProfile =CustomHud. activeProfile == 3 ? 1 : CustomHud.activeProfile + 1;
                button.setMessage(MutableText.of(new LiteralTextContent(Integer.toString(CustomHud.activeProfile))));
            }));

            for (int i = 1; i <= 3; i++) {
                int ii = i;
                this.addDrawableChild(new ButtonWidget(mid - 75,OPTION_START + OPTION_BUFFER*(i+1) + 5,150,20,
                        MutableText.of(new TranslatableTextContent("config.custom_hud.open_profile",i)), (button) -> new Thread(() -> Util.getOperatingSystem().open(CustomHud.getProfilePath(ii).toFile())).start()));

            }

            this.addDrawableChild(new ButtonWidget(mid - 100,this.height - 28,200,20,
                    MutableText.of(new TranslatableTextContent("config.custom_hud.done")), (button) -> close()));

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
        public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
            this.renderBackground(matrix);
            if (MinecraftClient.getInstance().cameraEntity == null)
                DrawableHelper.fill(matrix,0,36,this.width,this.height - 30-9, 0x88000000);

            DrawableHelper.drawCenteredText(matrix, font, MutableText.of(new TranslatableTextContent("config.custom_hud.title")), this.width / 2, 13, 0xFFFFFF);
            font.draw(matrix, MutableText.of(new TranslatableTextContent("config.custom_hud.enable.label")), 20, OPTION_START + 5, 0xFFFFFF);
            font.draw(matrix, MutableText.of(new TranslatableTextContent("config.custom_hud.active_profile.label")), 20, OPTION_START + OPTION_BUFFER + 5, 0xFFFFFF);

            super.render(matrix,mouseX, mouseY, delta);
        }

    }
}
