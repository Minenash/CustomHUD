package com.minenash.customhud.render;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.MultiElement;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.icon.IconElement;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.data.CHFormatting;
import com.minenash.customhud.data.HudTheme;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Section;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class CustomHudRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static Identifier font;

    public static void render(DrawContext context, float _tickDelta) {

        Profile profile = ProfileManager.getActive();
        if (profile == null || client.getDebugHud().shouldShowDebugHud())
            return;

        if (profile.baseTheme.getTargetGuiScale() != client.getWindow().getScaleFactor())
            client.onResolutionChanged();

        boolean isChatOpen = client.currentScreen instanceof ChatScreen;

        List<RenderPiece> pieces = new ArrayList<>();

        context.getMatrices().push();

        context.getMatrices().scale(profile.baseTheme.getScale(), profile.baseTheme.getScale(), 1);
        BufferBuilder bgBuilder = Tessellator.getInstance().getBuffer();
        bgBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (Section section : profile.sections) {
            HudTheme theme = profile.baseTheme;

            if (section == null || isChatOpen && section.hideOnChat)
                continue;

            CHFormatting formatting = theme.fgColor.copy();
            int right = (int) (client.getWindow().getScaledWidth() * (1 / theme.getScale())) - 3 + section.xOffset;
            boolean dynamicWidth = section.width == -1;
            boolean maxWidth = section.width == -2;
            int piecesOffset = pieces.size();
            int maxLineWidth = 0;
            List<MaxLineRenderPiece> maxLineRenderPieces = maxWidth ? new ArrayList<>() : null;


            int lineCount = 0;
            List<HudElement> elements = new ArrayList<>();
            for (HudElement e : section.elements)
                lineCount += addElement(elements, e);
            for (int j = 1; j < elements.size()-1; j++) {
                if (elements.get(j) instanceof FunctionalElement.IgnoreNewLineIfSurroundedByNewLine
                 && elements.get(j-1) instanceof FunctionalElement.NewLine
                 && elements.get(j+1) instanceof FunctionalElement.NewLine)
                    lineCount--;
            }


            boolean removeExtraNewLines = false;
            for (int i = elements.size() - 1; i >= 0; i--) {
                if (!(elements.get(i) instanceof FunctionalElement.NewLine))
                    break;
                if (removeExtraNewLines)
                    elements.remove(i);
                removeExtraNewLines = true;
            }

            StringBuilder builder = new StringBuilder();
            int y = section.getStartY(theme, lineCount);
            int xOffset = 0;

            int staticWidthY = y;

            for (int ei = 0; ei < elements.size(); ei++) {
                HudElement e = elements.get(ei);
                if (e instanceof FunctionalElement) {
                    String str = builder.toString();
                    if (!str.isEmpty()) {
                        str = formatting.getFormatting() + str;
                        pieces.add(new RenderPiece(str, theme.font, xOffset, y, formatting.getColor(), theme.textShadow));
                        xOffset += client.textRenderer.getWidth(str);
                        builder.setLength(0);
                    }
                    if (e instanceof FunctionalElement.NewLine) {
                        int x1 = section.getStartX(right, xOffset);
                        for (int i = piecesOffset; i < pieces.size(); i++)
                            pieces.get(i).x += x1;
                        piecesOffset = pieces.size();

                        if (dynamicWidth && xOffset != 0)
                            addLineBg(context, bgBuilder, x1-2, y - 2, x1 + xOffset + 2, y + 9 + theme.lineSpacing - 2, theme.bgColor);
                        else if (maxWidth)
                            maxLineWidth = Math.max(maxLineWidth, xOffset);

                        y += 9 + theme.lineSpacing;
                        xOffset = 0;
                        formatting = theme.fgColor.copy();
                    } else if (e instanceof FunctionalElement.IgnoreNewLineIfSurroundedByNewLine) {
                        if ( (ei-1 < 0 || elements.get(ei-1) instanceof FunctionalElement.NewLine)
                        && (ei+1 >= elements.size() || elements.get(ei+1) instanceof FunctionalElement.NewLine) ) {
                            ei++;
                        }

                    } else if (e instanceof FunctionalElement.ChangeFormatting cfe && cfe.getFormatting() != null) {
                        formatting.apply(cfe.getFormatting(), theme);
                    } else if (e instanceof FunctionalElement.ChangeTheme cte) {
                        if ((maxWidth || !dynamicWidth) && theme.bgColor != cte.theme.bgColor) {
                            int x1 = section.getStartX(right + 3, section.width) - 2;
                            if (maxWidth)
                                maxLineRenderPieces.add(new MaxLineRenderPiece(theme.bgColor, staticWidthY - 2, y-2));
                            else
                                addLineBg(context, bgBuilder, x1, staticWidthY - 2, x1 + section.width, y - 2, theme.bgColor);
                            staticWidthY = y;
                        }
                        theme = cte.theme;
                    } else if (e instanceof IconElement ie) {
                        pieces.add( new RenderPiece(ie, null, xOffset, y, 0, false) );
                        xOffset += ie.getTextWidth();
                    }
                    else if (e instanceof FunctionalElement.AdvanceList)    ListManager.advance();
                    else if (e instanceof FunctionalElement.PushList push)  ListManager.push(push.values, formatting.copy());
                    else if (e instanceof FunctionalElement.PopList)        formatting = ListManager.pop();

                } else {
                    builder.append(e.getString());
                }
            }

            if (maxLineRenderPieces != null) {
                int x1 = section.getStartX(right + 3, maxLineWidth+4) - 2;
                for (MaxLineRenderPiece piece : maxLineRenderPieces)
                    addLineBg(context, bgBuilder, x1, piece.y1, x1+maxLineWidth+4, piece.y2, piece.color);
                addLineBg(context, bgBuilder, x1, staticWidthY - 2, x1 + maxLineWidth+4, y - 2, theme.bgColor);
            }
            else if (!dynamicWidth) {
                int x1 = section.getStartX(right + 3, section.width) - 2;
                addLineBg(context, bgBuilder, x1, staticWidthY - 2, x1 + section.width, y - 2, theme.bgColor);
            }


        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        RenderSystem.disableBlend();

        for (RenderPiece piece : pieces) {
            if (piece.element instanceof IconElement ie )
                try { ie.render(context, piece.x, piece.y); }
                catch (Exception ignored){}
            else if (piece.element instanceof String value && !value.isEmpty()) {
                font = piece.font;
                context.drawText(client.textRenderer, value, piece.x, piece.y, piece.color, piece.shadow);
            }

        }

        context.getMatrices().pop();
        font = null;

    }


    private static int addElement(List<HudElement> allElements, HudElement element) {
        if (element instanceof MultiElement me) {
            int nl = 0;
            List<HudElement> elements = me.expand();
            if (elements.isEmpty()) {
                allElements.add(new FunctionalElement.IgnoreNewLineIfSurroundedByNewLine());
                return nl;
            }
            for (HudElement e : me.expand())
                nl += addElement(allElements, e);
            return nl;
        }
        else {
            if (element instanceof FunctionalElement.AdvanceList) ListManager.advance();
            else if (element instanceof FunctionalElement.PushList push) ListManager.push(push.values, null);
            else if (element instanceof FunctionalElement.PopList) ListManager.pop();
            allElements.add(element);
            return element instanceof FunctionalElement.NewLine ? 1 : 0;
        }


    }

    private static void addLineBg(DrawContext context, BufferBuilder builder, int x1, int y1, int x2, int y2, int color) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float f = (float)(color >> 24 & 255) / 255.0F;
        float g = (float)(color >> 16 & 255) / 255.0F;
        float h = (float)(color >> 8 & 255) / 255.0F;
        float j = (float)(color & 255) / 255.0F;
        builder.vertex(matrix, (float)x1, (float)y2, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)x2, (float)y2, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)x2, (float)y1, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)x1, (float)y1, 0.0F).color(g, h, j, f).next();
    }


}
