package com.minenash.customhud;

import com.minenash.customhud.HudElements.ConditionalElement;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.icon.IconElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.regex.Pattern;

public class CustomHudRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("([^{}&]*)(&\\{(#|0x)?([0-9a-fA-F]*)})?");
    private static final Pattern CONTAINS_HEX_COLOR_PATTERN = Pattern.compile(".*&\\{(#|0x)?[0-9a-fA-F]*}.*");

    public static Identifier font;
    public static boolean batch = false;
    private static HudTheme theme;

    record IconRender(IconElement element, MutableInt x, int y) {}
    record TextRender(String text, int length, MutableInt x, int y, int color) {}

    public static void render(MatrixStack matrix) {

        Profile profile = CustomHud.getActiveProfile();
        if (profile == null)
            return;

        boolean isChatOpen = client.currentScreen instanceof ChatScreen;

        matrix.push();
        theme = profile.baseTheme;
        font = profile.baseTheme.font;

        if (theme.scale != 1.0)
            matrix.scale(theme.scale,theme.scale,0);

        List<TextRender> textRenders = new ArrayList<>();
        List<IconRender> iconRenders = new ArrayList<>();
        int textRendersIndex = 0;
        int iconRendersIndex = 0;

        for (int i = 0; i < 4; i++) {
            List<List<HudElement>> section = profile.sections[i];
            if (section == null || (profile.hideOnChat[i] && isChatOpen))
                continue;

            int y = (i == 0 || i == 1 ? 3 : (int)(client.getWindow().getScaledHeight()*(1/theme.scale)) - 6 - section.size()*(9 + theme.lineSpacing)) + profile.offsets[i][1];

            for(List<HudElement> elements : section) {

                if (!elements.isEmpty() && elements.get(0) instanceof FunctionalElement.ChangeTheme cte) {
                    theme = cte.theme;
                    continue;
                }

                List<HudElement> allElements = new ArrayList<>(elements.size());
                for (HudElement e : elements)
                    addElement(allElements, e);


                int x_offset = 0;
                int color = theme.fgColor;
                int y_offset = (theme.lineSpacing/2) + 1;

                int textWidth = 0;
                StringBuilder builder = new StringBuilder();
                for (HudElement e : allElements) {
                    if (e instanceof FunctionalElement) {
                        textRenders.add(new TextRender(builder.toString(), textWidth, new MutableInt(x_offset), y + y_offset, color));
                        x_offset += textWidth;
                        builder = new StringBuilder();
                        textWidth = 0;

                        if (e instanceof FunctionalElement.NewLine) {
                            y += 9 + theme.lineSpacing;
                            x_offset = 0;
                        } else if (e instanceof FunctionalElement.ChangeColor cce) {
                            color = cce.color;
                        } else if (e instanceof IconElement ie) {
                            iconRenders.add(new IconRender(ie, new MutableInt(x_offset), y));
                            x_offset += ie.getTextWidth() + 1;
                        }
                    }
                    else {
                        String str = e.getString();
                        textWidth += client.textRenderer.getWidth(str);
                        builder.append(str);
                    }
                }
                textRenders.add(new TextRender(builder.toString(), textWidth, new MutableInt(x_offset), y + y_offset, color));
                x_offset += textWidth;

                if (x_offset == 0) {
                    y += 9 + theme.lineSpacing;
                    continue;
                }

                int startX = ((i == 0 || i == 2) ? 5 : (int)(client.getWindow().getScaledWidth()*(1/theme.scale)) - 3 - x_offset) + profile.offsets[i][0];

                DrawableHelper.fill(matrix, startX - 2, y, startX + x_offset + 1, y + 9 + theme.lineSpacing, theme.bgColor);

                for (int index = textRendersIndex; index < textRenders.size(); index++)
                    textRenders.get(index).x.add(startX);
                for (int index = iconRendersIndex; index < iconRenders.size(); index++)
                    iconRenders.get(index).x.add(startX);

                textRendersIndex = textRenders.size();
                iconRendersIndex = iconRenders.size();

                y += 9 + theme.lineSpacing;

            }
            theme = profile.baseTheme;
        }

//        batch = true;
        for (TextRender render : textRenders) {
            if (!render.text.isEmpty())
                drawText(matrix, render.text, render.x.getValue(), render.y, render.color);
        }
//        batch = false;
//        VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer()).draw();

        for (IconRender render : iconRenders) {
            render.element.render(matrix, render.x.getValue(), render.y);
        }

        font = null;
        matrix.pop();
    }

    private static void addElement(List<HudElement> allElements, HudElement element) {
        if (element instanceof ConditionalElement ce)
            for (HudElement e : ce.get())
                addElement(allElements, e);
        else
            allElements.add(element);

    }

    private static void drawText(MatrixStack matrix, String line, float x, float y, int color) {
        //TODO: batch immediate.draw(); in TextRenderer
        if (theme.textShadow)
            client.textRenderer.drawWithShadow(matrix, line, x, y, color);
        else
            client.textRenderer.draw(matrix, line, x, y, color);
    }


}
