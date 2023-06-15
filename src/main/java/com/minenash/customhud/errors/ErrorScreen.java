package com.minenash.customhud.errors;

import com.minenash.customhud.CustomHud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ErrorScreen extends Screen {

    private ErrorListWidget listWidget = null;
    private final ButtonWidget[] profiles = new ButtonWidget[3];
    private final Screen parent;
    private int profile;
    public int y_offset = 0;
    private static final int lineColumnX = 15;
    public int sourceSectionWidth = 120;

    public ErrorScreen(Screen parent) {
        this(parent, CustomHud.activeProfile);
    }

    public ErrorScreen(Screen parent, int profile) {
        super(Text.literal("Profile Errors"));
        this.parent = parent;
        this.profile = profile;
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



        profiles[0] = this.addDrawableChild( ButtonWidget.builder(Text.literal("Profile 1"), button -> changeProfile(1))
                .position(this.width / 2 - 40 - 90, 24).size(80, 20).build() );

        profiles[1] = this.addDrawableChild( ButtonWidget.builder(Text.literal("Profile 2"), button -> changeProfile(2))
                .position(this.width / 2 - 40, 24).size(80, 20).build() );

        profiles[2] = this.addDrawableChild( ButtonWidget.builder(Text.literal("Profile 3"), button -> changeProfile(3))
                .position(this.width / 2 - 40 + 90, 24).size(80, 20).build() );

        this.addDrawableChild( ButtonWidget.builder(Text.literal("Open Profile"),
                button -> new Thread(() -> Util.getOperatingSystem().open(CustomHud.getProfilePath(profile).toFile())).start())
                .position(this.width / 2 - 155, this.height - 26).size(150, 20).build() );

        this.addDrawableChild( ButtonWidget.builder(ScreenTexts.DONE, button -> this.client.setScreen(parent))
                .position(this.width / 2 - 155 + 160, this.height - 26).size(150, 20).build() );

        super.init();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        profiles[0].active = Errors.hasErrors(1);
        profiles[1].active = Errors.hasErrors(2);
        profiles[2].active = Errors.hasErrors(3);

        y_offset = 0;
        this.listWidget.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);

        super.render(context, mouseX, mouseY, delta);

        int x = this.width / 2 + (profile == 1 ? -90 : profile == 2 ? 0 : 90);
        context.fill(x - 30, 47, x + 30, 48, 0xFFFFFFFF);
    }

    class ErrorListWidget extends EntryListWidget<ErrorListWidget.ErrorEntry> {

        public ErrorListWidget(MinecraftClient client, int profile) {
            super(client, ErrorScreen.this.width, ErrorScreen.this.height, 52, ErrorScreen.this.height - 36 + 4, 18);

            this.addEntry( new ErrorEntryHeader() );
            if (!Errors.hasErrors(profile))
                this.addEntry( new ErrorEntry( new Errors.Error("0", "No Errors", ErrorType.NONE, "") ) );

            for (var e : Errors.getErrors(profile))
                this.addEntry(new ErrorEntry(e));

            if (this.getSelectedOrNull() != null)
                this.centerScrollOn(getEntry(0));

        }

        @Override
        protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {}

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
        protected void renderBackground(DrawContext context) {
            ErrorScreen.this.renderBackground(context);
        }

        @Override
        public boolean isFocused() {
            return ErrorScreen.this.getFocused() == this;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {}

        public class ErrorEntryHeader extends ErrorEntry {

            public ErrorEntryHeader() {
                super(new Errors.Error("§nLine", "Source", ErrorType.HEADER, ""));
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                context.drawCenteredTextWithShadow(textRenderer, error.line().formatted(Formatting.UNDERLINE), lineColumnX, y + y_offset, 0xFFFFFFFF);
                context.drawTextWithShadow(textRenderer, Text.literal(collapsedSource).formatted(Formatting.UNDERLINE), 36, y + y_offset, 0xFFFFFFFF);
                context.drawTextWithShadow(textRenderer, Text.literal(collapsedMsg).formatted(Formatting.UNDERLINE), msgX, y + y_offset, 0xFFFFFFFF);
                context.drawTextWithShadow(textRenderer, error.type().linkText.formatted(Formatting.WHITE), refX, y + y_offset, 0xFFFFFFFF);
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
            final int refX, refLength;
            boolean expands = false;

            public ErrorEntry(Errors.Error error) {
                this.error = error;
                msgX = 36 + sourceSectionWidth + 15;

                refLength = error.type().linkText == null ? 0 : textRenderer.getWidth(error.type().linkText);
                refX = error.type().linkText == null ? 0 : width - 28 - refLength;

                expandedMsg = textRenderer.wrapLines(StringVisitable.plain( error.type().message + error.context() ), width - 36 - 16);
                collapsedMsg = ensureLength(error.type().message + error.context(), width - msgX - 16 - refLength, "…");
                collapsedSource = ensureLength(error.source().replace('§', '&'), sourceSectionWidth,
                        error.source().startsWith("{{") ? "…}}" : error.source().startsWith("{") ? "…}" : "…");
                expandedSource = ensureLength(error.source().replace('§', '&'), width - 36 - 16 - refLength,
                        error.source().startsWith("{{") ? "…}}" : error.source().startsWith("{") ? "…}" : "…");
            }

            private String ensureLength(String str, int width, String suffix) {
                if (textRenderer.getWidth(str) <= width)
                    return str;
                else {
                    str = str.substring(0, str.length() - suffix.length() + 1);
                    expands = true;
                    int endWidth = textRenderer.getWidth("suffix");
                    while (textRenderer.getWidth(str) > width - endWidth)
                        str = str.substring(0, str.length() - 1);
                    return str + suffix;
                }
            }

            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                if (hovered) {
                    int extendedHeight = ErrorListWidget.this.getSelectedOrNull() == this ? (18 * expandedMsg.size()) : 0;
                    context.fill(0, y + y_offset, width, y + y_offset + 18 + extendedHeight, 0x22FFFFFF);
                    if (mouseX >= refX && mouseX <= refX + refLength)
                        context.drawTooltip(textRenderer, Text.literal("§eClick to open the " + error.type().linkText.getString() + " page"), mouseX, mouseY);
                }

                y += 6;
                int ceX = getMaxScroll() > 0 ? width-16 : width-12;

                context.drawCenteredTextWithShadow(textRenderer, error.line(), lineColumnX, y + y_offset, 0xFFFFFFFF);
                if (refX > 0)
                    context.drawTextWithShadow(textRenderer, error.type().linkText, refX, y + y_offset, 0xFFFFFFFF);
                if (ErrorListWidget.this.getSelectedOrNull() != this) {
                    if (expands)
                        context.drawTextWithShadow(textRenderer, "▶", ceX, y + y_offset, 0xFFFFFFFF);
                    context.drawTextWithShadow(textRenderer, collapsedSource, 36, y + y_offset, 0xFFFFFFFF);
                    context.drawTextWithShadow(textRenderer, collapsedMsg, msgX, y + y_offset, 0xFFFFFFFF);
                }
                else {
                    if (expands)
                        context.drawTextWithShadow(textRenderer, "▼", ceX, y + y_offset, 0xFFFFFFFF);
                    context.drawTextWithShadow(textRenderer, expandedSource, 36, y + y_offset, 0xFFFFFFFF);
                    for (OrderedText msgLine : expandedMsg) {
                        y_offset += 18;
                        context.drawCenteredTextWithShadow(textRenderer, "→", lineColumnX, y + y_offset, 0xFFFFFFFF);
                        context.drawTextWithShadow(textRenderer, msgLine, 36, y + y_offset, 0xFFFFFFFF);
                    }
                    y_offset += 18;

                }

            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (mouseX >= refX && mouseX <= refX + refLength)
                    Util.getOperatingSystem().open(error.type().link);
                else if (expands && ErrorListWidget.this.getSelectedOrNull() != this)
                    ErrorListWidget.this.setSelected(this);
                else
                    ErrorListWidget.this.setSelected(null);
                return false;
            }

        }
    }
}