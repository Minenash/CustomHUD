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
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ErrorScreen extends Screen {

    private ErrorListWidget listWidget = null;
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

    public void changeProfile(int profile) {
        this.profile = profile;
//        if (listWidget != null)
//            this.remove(listWidget);
//        this.listWidget = new ErrorListWidget(this.client, profile);
//        this.addSelectableChild( listWidget );
        init();
    }

    protected void init() {
        children().clear();
        this.listWidget = new ErrorListWidget(this.client, profile);
        this.addSelectableChild(listWidget);

        profiles[0] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40 - 90, 24, 80, 20, Text.literal("Profile 1"), button -> {
            changeProfile(1);
        }));
        profiles[1] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40, 24, 80, 20, Text.literal("Profile 2"), button -> {
            changeProfile(2);
        }));
        profiles[2] = this.addDrawableChild(new ButtonWidget(this.width / 2 - 40 + 90, 24, 80, 20, Text.literal("Profile 3"), button -> {
            changeProfile(3);
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

        int x = this.width / 2 + (profile == 1 ? -90 : profile == 2 ? 0 : 90);
        DrawableHelper.fill(matrices, x - 30, 47, x + 30, 48, 0xFFFFFFFF);
    }

    class ErrorListWidget extends EntryListWidget<ErrorListWidget.ErrorEntry> {

        public ErrorListWidget(MinecraftClient client, int profile) {
            super(client, ErrorScreen.this.width, ErrorScreen.this.height, 52, ErrorScreen.this.height - 36 + 4, 18);

            this.addEntry( new ErrorEntryHeader() );
            this.addEntry( new ErrorEntry(new Errors.Error("100", "{aaaaaaaaaaaaaaahhhhhhhhhhhaaaaaaaaaaaaaaahhhhhhhhhhhaaaaaaaaaaaaaaahhhhhhhhhhh}", ErrorType.TEST, "")) );

            for (var e : Errors.getErrors(profile))
                this.addEntry(new ErrorEntry(e));

            if (this.getSelectedOrNull() != null)
                this.centerScrollOn(getEntry(0));

        }

        @Override
        protected void drawSelectionHighlight(MatrixStack matrices, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {}

        @Override
        protected int getScrollbarPositionX() {
            return width - 8;
        }

        @Override
        protected ErrorEntry getEntryAtPosition(double x, double y) {
            int m = MathHelper.floor(y - (double)this.top) - this.headerHeight + (int)this.getScrollAmount() - 4;
            int n = m / this.itemHeight;

            ErrorEntry entry = getSelectedOrNull();
            if (entry != null ) {
                int index = children().indexOf( entry );
                if (n >= index && n <= index + entry.expandedMsg.size())
                    n = index;
                else if (n == index + entry.expandedMsg.size() + 1)
                    n = 0;
                else if (n > index)
                    n -= entry.expandedMsg.size() + 1;
            }
            return x < this.getScrollbarPositionX() && n >= 0 && m >= 0 && n < this.getEntryCount() ? this.children().get(n) : null;
        }

        @Override
        protected void renderBackground(MatrixStack matrices) {
            ErrorScreen.this.renderBackground(matrices);
        }

        @Override
        protected boolean isFocused() {
            return ErrorScreen.this.getFocused() == this;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}

        public class ErrorEntryHeader extends ErrorEntry {

            public ErrorEntryHeader() {
                super(new Errors.Error("Line", " Source", ErrorType.HEADER, ""));
            }

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                drawCentered(matrices, y + y_offset, lineColumnX, error.line());
                client.textRenderer.drawWithShadow(matrices, collapsedSource, 36, y + y_offset, 0xFFFFFFFF);
                client.textRenderer.drawWithShadow(matrices, collapsedMsg, msgX, y + y_offset, 0xFFFFFFFF);

                DrawableHelper.fill(matrices, 0, y + 12, width, y+16, 0x88000000);
            }
        }

        @Environment(EnvType.CLIENT)
        public class ErrorEntry extends EntryListWidget.Entry<ErrorEntry> {
            final Errors.Error error;
            final String collapsedSource;
            final String collapsedMsg;
            final List<OrderedText> expandedMsg;
            final String expandedSource;
            final int msgX;
            boolean expands = false;

            public ErrorEntry(Errors.Error error) {
                this.error = error;
                msgX = 36 + sourceSectionWidth + 15;
                expandedMsg = textRenderer.wrapLines(StringVisitable.plain( error.type().message.apply(error.context()) ), width - 36 - 16);
                collapsedMsg = ensureLength(error.type().message.apply(error.context()), width - msgX - 16, "…");
                collapsedSource = ensureLength(error.source().replace('§', '&'), sourceSectionWidth,
                        error.source().startsWith("{{") ? "…}}" : error.source().startsWith("{") ? "…}" : "…");
                expandedSource = ensureLength(error.source().replace('§', '&'), width - 36 - 16,
                        error.source().startsWith("{{") ? "…}}" : error.source().startsWith("{") ? "…}" : "…");
            }

            private String ensureLength(String str, int width, String suffix) {
                if (textRenderer.getWidth(str) <= width)
                    return str;
                else {
                    expands = true;
                    int endWidth = textRenderer.getWidth("suffix");
                    while (textRenderer.getWidth(str) > width - endWidth)
                        str = str.substring(0, str.length() - 1);
                    return str + suffix;
                }
            }

            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (hovered) {
                    int extendedHeight = ErrorListWidget.this.getSelectedOrNull() == this ? (18 * expandedMsg.size()) : 0;
                    DrawableHelper.fill(matrices, 0, y + y_offset, width, y + y_offset + 18 + extendedHeight, 0x22FFFFFF);
                }

                y += 6;

                if (ErrorListWidget.this.getSelectedOrNull() != this) {
                    drawCentered(matrices, y + y_offset, lineColumnX, "" + error.line());
                    if (expands)
                        drawCentered(matrices, y + y_offset, width-16, "C");
                    client.textRenderer.drawWithShadow(matrices, collapsedSource, 36, y + y_offset, 0xFFFFFFFF);
                    client.textRenderer.drawWithShadow(matrices, collapsedMsg, msgX, y + y_offset, 0xFFFFFFFF);


                }
                else {
                    drawCentered(matrices, y + y_offset, lineColumnX, "" + error.line());
                    if (expands)
                        drawCentered(matrices, y + y_offset, width-16, "E");
                    client.textRenderer.drawWithShadow(matrices, expandedSource, 36, y + y_offset, 0xFFFFFFFF);
                    for (OrderedText msgLine : expandedMsg) {
                        y_offset += 18;
                        drawCentered(matrices, y + y_offset, lineColumnX, "→");
                        client.textRenderer.drawWithShadow(matrices, msgLine, 36, y + y_offset, 0xFFFFFFFF);
                    }
                    y_offset += 18;

                }

            }

            protected void drawCentered(MatrixStack matrices, int y, int x, String text) {
                float xx = (float)(x - client.textRenderer.getWidth(text) / 2);
                client.textRenderer.drawWithShadow(matrices, text, xx, y, 0xFFFFFFFF);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (ErrorListWidget.this.getSelectedOrNull() == this)
                    ErrorListWidget.this.setSelected(null);
                else if (true)
                    ErrorListWidget.this.setSelected(this);
                return false;
            }

        }
    }
}
