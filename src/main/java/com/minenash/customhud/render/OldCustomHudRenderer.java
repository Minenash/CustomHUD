//package com.minenash.customhud;
//
//import com.minenash.customhud.HudElements.ConditionalElement;
//import com.minenash.customhud.HudElements.functional.FunctionalElement;
//import com.minenash.customhud.HudElements.HudElement;
//import com.minenash.customhud.HudElements.icon.IconElement;
//import com.minenash.customhud.ducks.CustomHudTextRendererExtention;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.DrawableHelper;
//import net.minecraft.client.gui.screen.ChatScreen;
//import net.minecraft.client.render.*;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.math.Matrix4f;
//import org.apache.commons.lang3.mutable.MutableInt;
//
//import java.util.*;
//import java.util.regex.Pattern;
//
//public class CustomHudRenderer {
//
//    private static final MinecraftClient client = MinecraftClient.getInstance();
//
//    public static Identifier font;
//    private static HudTheme theme;
//
//    record IconRender(IconElement element, MutableInt x, int y) {}
//    record TextRender(String text, int length, MutableInt x, int line, int color) {}
//
//    public static void render(MatrixStack matrices) {
//
//        Profile profile = CustomHud.getActiveProfile();
//        if (profile == null)
//            return;
//
//        boolean isChatOpen = client.currentScreen instanceof ChatScreen;
//
//        matrices.push();
//        theme = profile.baseTheme;
//        font = profile.baseTheme.font;
//
//        if (theme.scale != 1.0)
//            matrices.scale(theme.scale, theme.scale, 0);
//
//        List<TextRender> textRenders = new ArrayList<>();
//        List<IconRender> iconRenders = new ArrayList<>();
//        int textRendersIndex = 0;
//        int iconRendersIndex = 0;
//
//
//
//        for (int i = 0; i < 4; i++) {
//            List<List<HudElement>> section = profile.sections[i];
//            if (section == null || (profile.hideOnChat[i] && isChatOpen))
//                continue;
//
//            int y = (i == 0 || i == 1 ? 3 : (int) (client.getWindow().getScaledHeight() * (1 / theme.scale)) - 6 - section.size() * (9 + theme.lineSpacing)) + profile.offsets[i][1];
//
//            boolean dynamicBGWidth = profile.width[i] == -1;
//
//            for (List<HudElement> elements : section) {
//
//                if (!elements.isEmpty() && elements.get(0) instanceof FunctionalElement.ChangeTheme cte) {
//                    theme = cte.theme;
//                    continue;
//                }
//
//                List<HudElement> allElements = new ArrayList<>(elements.size());
//                for (HudElement e : elements)
//                    addElement(allElements, e);
//
//                int x_offset = 0;
//                int color = theme.fgColor;
//                int y_offset = (theme.lineSpacing / 2) + 1;
//
//                int textWidth = 0;
//                StringBuilder builder = new StringBuilder();
//                for (HudElement e : allElements) {
//                    if (e instanceof FunctionalElement) {
//                        textRenders.add(new TextRender(builder.toString(), textWidth, new MutableInt(x_offset), y + y_offset, color));
//                        x_offset += textWidth;
//                        builder = new StringBuilder();
//                        textWidth = 0;
//
//                        if (e instanceof FunctionalElement.NewLine) {
//                            if (dynamicBGWidth) {
//                                int startX = ((i == 0 || i == 2) ? 5 : (int) (client.getWindow().getScaledWidth() * (1 / theme.scale)) - 3 - x_offset) + profile.offsets[i][0];
//                                addLineBackground(matrices, bgBuilder, startX - 2, y, startX + x_offset + 1, y + 9 + theme.lineSpacing, theme.bgColor);
//                            }
//                            y += 9 + theme.lineSpacing;
//                            x_offset = 0;
//                        } else if (e instanceof FunctionalElement.ChangeColor cce) {
//                            color = cce.color;
//                        } else if (e instanceof IconElement ie) {
//                            iconRenders.add(new IconRender(ie, new MutableInt(x_offset), y));
//                            x_offset += ie.getTextWidth() + 1;
//                        }
//                    } else {
//                        String str = e.getString();
//                        textWidth += client.textRenderer.getWidth(str);
//                        builder.append(str);
//                    }
//                }
//                textRenders.add(new TextRender(builder.toString(), textWidth, new MutableInt(x_offset), y + y_offset, color));
//                x_offset += textWidth;
//
//                if (x_offset == 0) {
//                    y += 9 + theme.lineSpacing;
//                    continue;
//                }
//
//
//                int startX = ((i == 0 || i == 2) ? 5 : (int) (client.getWindow().getScaledWidth() * (1 / theme.scale)) - 3 - x_offset) + profile.offsets[i][0];
//
//                if (dynamicBGWidth)
//                    addLineBackground(matrices, bgBuilder, startX - 2, y, startX + x_offset + 1, y + 9 + theme.lineSpacing, theme.bgColor);
//                for (int index = textRendersIndex; index < textRenders.size(); index++)
//                    textRenders.get(index).x.add(startX);
//                for (int index = iconRendersIndex; index < iconRenders.size(); index++)
//                    iconRenders.get(index).x.add(startX);
//
//                textRendersIndex = textRenders.size();
//                iconRendersIndex = iconRenders.size();
//
//                y += 9 + theme.lineSpacing;
//
//            }
//
//            if (!dynamicBGWidth) {
//                int width = profile.width[i];
//                int bgy = (i == 0 || i == 1 ? 3 : (int) (client.getWindow().getScaledHeight() * (1 / theme.scale)) - 6 - section.size() * (9 + theme.lineSpacing)) + profile.offsets[i][1];
//                int height = section.size() * (9 + theme.lineSpacing);
//                int bgx = ((i == 0 || i == 2) ? 5 : (int) (client.getWindow().getScaledWidth() * (1 / theme.scale)) - 3 - width) + profile.offsets[i][0] - 2;
//
//                addLineBackground(matrices, bgBuilder, bgx, bgy, width, height, theme.bgColor);
//
//            }
//
//            theme = profile.baseTheme;
//        }
//
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.setShader(GameRenderer::getPositionColorShader);
//        BufferRenderer.drawWithShader(bgBuilder.end());
//        RenderSystem.disableBlend();
//
//        ((CustomHudTextRendererExtention) client.textRenderer).customHud$beginCache();
//        for (TextRender render : textRenders)
//            if (!render.text.isEmpty())
//                drawText(matrices, render.text, render.x.getValue(), render.y, render.color);
//        ((CustomHudTextRendererExtention) client.textRenderer).customHud$submitCache();
//
//        for (IconRender render : iconRenders)
//            render.element.render(matrices, render.x.getValue(), render.y);
//
//        font = null;
//        matrices.pop();
//    }
//
//    private static void addElement(List<HudElement> allElements, HudElement element) {
//        if (element instanceof ConditionalElement ce)
//            for (HudElement e : ce.get())
//                addElement(allElements, e);
//        else
//            allElements.add(element);
//
//    }
//
//    private static void drawText(MatrixStack matrix, String line, float x, float y, int color) {
//        if (theme.textShadow)
//            client.textRenderer.drawWithShadow(matrix, line, x, y, color);
//        else
//            client.textRenderer.draw(matrix, line, x, y, color);
//    }
//
//    private static void addLineBackground(MatrixStack matrices, BufferBuilder builder, int x1, int y1, int x2, int y2, int color) {
//        Matrix4f matrix = matrices.peek().getPositionMatrix();
//        float f = (float)(color >> 24 & 255) / 255.0F;
//        float g = (float)(color >> 16 & 255) / 255.0F;
//        float h = (float)(color >> 8 & 255) / 255.0F;
//        float j = (float)(color & 255) / 255.0F;
//        builder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, j, f).next();
//        builder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, j, f).next();
//        builder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, j, f).next();
//        builder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, j, f).next();
//    }
//
//}
