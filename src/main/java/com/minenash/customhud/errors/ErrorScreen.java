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
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.List;

public class ErrorScreen extends Screen {

    private ErrorListWidget listWidget;
    private ButtonWidget[] profiles = new ButtonWidget[3];
    private final Screen parent;
    private int profile;
    public int y_offset = 0;
    private static final int lineColumnX = 15;
    public int sourceSectionWidth = 120;

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

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        profiles[0].active = Errors.hasErrors(1);
        profiles[1].active = Errors.hasErrors(2);
        profiles[2].active = Errors.hasErrors(3);

        y_offset = 0;
        this.listWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);

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

        @Environment(EnvType.CLIENT)
        public class ErrorEntry extends AlwaysSelectedEntryListWidget.Entry<ErrorEntry> {
            final Errors.Error error;
            final String source;
            final List<OrderedText> lines;
            final int msgX;

            public ErrorEntry(Errors.Error error) {
                this.error = error;
                msgX = 32 + sourceSectionWidth + 15;
                lines = textRenderer.wrapLines(StringVisitable.plain( error.type().message.apply(error.context()) ), width - (msgX));

                String source = error.source().replace('§', '&');
                if (textRenderer.getWidth(source) > sourceSectionWidth) {
                    String suffix = source.startsWith("{{") ? "…}}" : source.startsWith("{") ? "…}" : "…";
                    int endWidth = textRenderer.getWidth("suffix");
                    while (textRenderer.getWidth(source) > sourceSectionWidth - endWidth)
                        source = source.substring(0, source.length() - 1);
                    this.source = source + suffix;
                }
                else
                    this.source = source;

            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                drawCentered(matrices, y + y_offset, lineColumnX, "" + error.line());

                client.textRenderer.drawWithShadow(matrices,source, 32, y + y_offset, 0xFFFFFFFF);

                client.textRenderer.drawWithShadow(matrices, lines.get(0), msgX, y + y_offset, 0xFFFFFFFF);

                for (int i = 1; i < lines.size(); i++) {
                    y_offset += 18;
                    client.textRenderer.drawWithShadow(matrices, lines.get(0), msgX, y + y_offset, 0xFFFFFFFF);
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
