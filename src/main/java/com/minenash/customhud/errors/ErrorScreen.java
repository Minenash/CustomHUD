package com.minenash.customhud.errors;

import com.minenash.customhud.CustomHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.List;

public class ErrorScreen extends Screen {

    private ErrorListWidget listWidget;
    private ButtonWidget[] profiles = new ButtonWidget[3];
    private final Screen parent;
    private int profile;

    public ErrorScreen(Screen parent) {
        super(Text.literal("Profile Errors"));
        this.parent = parent;
        this.profile = CustomHud.activeProfile;
    }

    public void init() {
        this.listWidget = new ErrorListWidget(this.client, profile);
        this.addSelectableChild( listWidget );

        profiles[0] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40 - 90, 24, 80, 20, Text.literal("Profile 1"), button -> {
            listWidget = new ErrorListWidget(this.client, 1);
            profile = 1;
        }));
        profiles[1] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40, 24, 80, 20, Text.literal("Profile 2"), button -> {
            listWidget = new ErrorListWidget(this.client, 2);
            profile = 2;
        }));
        profiles[2] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40 + 90, 24, 80, 20, Text.literal("Profile 3"), button -> {
            listWidget = new ErrorListWidget(this.client, 3);
            profile = 3;
        }));

        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 26, 150, 20, Text.literal("Open Profile"), button -> {
            new Thread(() -> Util.getOperatingSystem().open(CustomHud.getProfilePath(profile).toFile())).start();
        }));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 26, 150, 20, ScreenTexts.DONE, (button) -> {
            this.client.setScreen(parent);
        }));
        super.init();
    }

    public int y_offset = 0;
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        profiles[0].active = Errors.hasErrors(1);
        profiles[1].active = Errors.hasErrors(2);
        profiles[2].active = Errors.hasErrors(3);

        y_offset = 0;
        this.listWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);

        DrawableHelper.fill(matrices, 30, 52, 31, height - 36 + 4, 0x44FFFFFF);
        DrawableHelper.fill(matrices, 150, 52, 151, height - 36 + 4, 0x44FFFFFF);

        super.render(matrices, mouseX, mouseY, delta);
    }

    class ErrorListWidget extends AlwaysSelectedEntryListWidget<ErrorListWidget.ErrorEntry> {

        public ErrorListWidget(MinecraftClient client, int profile) {
            super(client, ErrorScreen.this.width, ErrorScreen.this.height, 52, ErrorScreen.this.height - 36 + 4, 18);

            for (var e : Errors.getErrors(profile))
                this.addEntry(new ErrorEntry(e));

            if (this.getSelectedOrNull() != null)
                this.centerScrollOn(getEntry(0));

        }

        protected int getScrollbarPositionX() {
            return super.getScrollbarPositionX() + 20;
        }

        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        protected void renderBackground(MatrixStack matrices) {
            ErrorScreen.this.renderBackground(matrices);
        }

        protected boolean isFocused() {
            return ErrorScreen.this.getFocused() == this;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        private static final int lineColumnX = 15;
        private static final int sourceColumnX = 90;
        private static final int messageColumnX = 150;

        @Environment(EnvType.CLIENT)
        public class ErrorEntry extends AlwaysSelectedEntryListWidget.Entry<ErrorEntry> {
            final Errors.Error error;

            public ErrorEntry(Errors.Error error) {
                this.error = error;
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

                if (!error.type().multiLine) {
                    drawCentered(matrices, y + y_offset, lineColumnX, "" + error.line());
                    drawCentered(matrices, y + y_offset, sourceColumnX, error.source());
                    client.textRenderer.drawWithShadow(matrices, error.type().message.apply(error.context()), messageColumnX, y, 0xFFFFFFFF);

                }
                else {
                    drawCentered(matrices, y + y_offset, lineColumnX, "" + error.line());
                    client.textRenderer.drawWithShadow(matrices, error.source(), sourceColumnX, y, 0xFFFFFFFF);


                    for (OrderedText text : client.textRenderer.wrapLines(Text.literal(error.type().message.apply(error.context())), width - 20)) {
                        y_offset += 18;
                        client.textRenderer.drawWithShadow(matrices, text, 32, y + y_offset, 0xFFFFFFFF);
                    }
                    y_offset += 18;
                }


            }

            private void drawCentered(MatrixStack matrices, int y, int x, String text) {
                float xx = (float)(x - client.textRenderer.getWidth(text) / 2);
                client.textRenderer.drawWithShadow(matrices, text, xx, y, 0xFFFFFFFF);
            }

            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return false;
            }

            public Text getNarration() {
                return Text.literal("contact developer");
            }
        }
    }
}
