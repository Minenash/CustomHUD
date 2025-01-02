//package com.minenash.customhud.render;
//
//import com.minenash.customhud.CustomHud;
//import com.minenash.customhud.HudElements.functional.FunctionalElement;
//import com.minenash.customhud.HudElements.icon.IconElement;
//import com.minenash.customhud.HudElements.interfaces.ExecuteElement;
//import com.minenash.customhud.HudElements.interfaces.HudElement;
//import com.minenash.customhud.HudElements.interfaces.MultiElement;
//import com.minenash.customhud.HudElements.text.TextElement;
//import com.minenash.customhud.ProfileManager;
//import com.minenash.customhud.complex.ListManager;
//import com.minenash.customhud.data.CHFormatting;
//import com.minenash.customhud.data.HudTheme;
//import com.minenash.customhud.data.Profile;
//import com.minenash.customhud.data.Section;
//import com.mojang.blaze3d.systems.RenderSystem;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.hud.DebugHud;
//import net.minecraft.client.gui.screen.ChatScreen;
//import net.minecraft.client.render.*;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class CustomHudRenderer3LocalScaleAttempt {
//
//    private static final MinecraftClient client = MinecraftClient.getInstance();
//
//    public static Identifier font;
//    public static HudTheme theme;
//
//    public static void render(DrawContext context, float tickDelta) {
//
//        Profile profile = ProfileManager.getActive();
//        if (profile == null || client.getDebugHud().shouldShowDebugHud())
//            return;
//
//        if (profile.baseTheme.getTargetGuiScale() != client.getWindow().getScaleFactor())
//            client.onResolutionChanged();
//
//        boolean isChatOpen = client.currentScreen instanceof ChatScreen;
//        float baseScale = profile.baseTheme.getScale();
//
//        List<RenderPiece> pieces = new ArrayList<>();
//        List<RenderPiece> wipPieces = new ArrayList<>();
//
//        Profilers.get().push("custom_hud");
//        Profilers.get().push("processing");
//
//        BufferBuilder bgBuffer = Tessellator.getInstance().getBuffer();
//        bgBuffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
//
//        for (Section section : profile.sections) {
//            if (section == null || isChatOpen && section.hideOnChat)
//                continue;
//
//            theme = profile.baseTheme;
//            BackgroundBuilder bgBuilder = new BackgroundBuilder(section);
//
//            float xOffset = 0;
//            int yOffset = 0;
//            int bgYOffset = 0;
//            CHFormatting formatting = theme.fgColor.copy();
//
//
//            List<HudElement> elements = new ArrayList<>(section.elements);
//            StringBuilder builder = new StringBuilder();
//
//            main:
//            for (int i = 0; i < elements.size(); i++) {
//                HudElement e = elements.get(i);
//
//                if (!(e instanceof FunctionalElement))
//                    builder.append(e.getString());
//
//                else if (e instanceof ExecuteElement ee)
//                    ee.run();
//
//                else {
//                    String str = builder.toString();
//                    if (!str.isEmpty()) {
//                        if (theme.ignoreLeadingSpace && xOffset == 0)
//                            str = leftTrim(str);
//                        str = formatting.getFormatting() + str;
//
//                        wipPieces.add(new RenderPiece(str, null, theme.font, (int) xOffset, section.hPaddingOffset(theme), yOffset, formatting.getColor(), theme, baseScale));
//                        xOffset += client.textRenderer.getWidth(str) * (theme.getScale() / baseScale);
//                        builder.setLength(0);
//                    }
//                    if (e instanceof FunctionalElement.NewLine) {
//                        if (e instanceof FunctionalElement.LineBreak) {
//                            if (!theme.convertLineBreaks)
//                                continue;
//                            if (theme.ignoreBlankLines && (i == 0 || elements.get(i-1) instanceof FunctionalElement.LineBreak) )
//                                continue;
//
//                            boolean maybeIgnore = false;
//                            UUID listUUID = null;
//                            for (int j = i - 1; j >= 0; j--) {
//                                HudElement element = elements.get(j);
//                                if (element instanceof FunctionalElement.IgnoreNewLineIfSurroundedByNewLine || (listUUID != null && FunctionalElement.isList(element))) {
//                                    maybeIgnore = true;
//                                    if (j == 0)
//                                        continue main;
//                                }
//                                else if ((element instanceof FunctionalElement.LineBreak) && maybeIgnore && listUUID == null)
//                                    continue main;
//                                else if (j == i-1 && element instanceof FunctionalElement.PopList pl)
//                                    listUUID = pl.providerID;
//                                else if (listUUID != null && element instanceof FunctionalElement.PushList pl && listUUID.equals(pl.providerID))
//                                    listUUID = null;
//                                else
//                                    break;
//                            }
//                        }
//
//                        for (int j = wipPieces.size() - 1; j >= 0; j--) {
//                            RenderPiece piece = wipPieces.get(j);
//                            if (piece.y == yOffset + theme.padding.top())
//                                piece.lineWith = (int) (xOffset * baseScale);
//                            else
//                                break;
//                        }
//                        float themeScale = theme.getScale();
//                        int height = Math.round((6 + theme.lineSpacing + theme.padding.top() + theme.padding.bottom()) * themeScale/baseScale);
//                        int bgHeight = Math.round((6 + theme.lineSpacing + theme.padding.top() + theme.padding.bottom()) * themeScale);
//                        if (xOffset > 0)
//                            bgBuilder.addLine(bgYOffset, (int) ((xOffset * baseScale) + ((theme.padding.left() + theme.padding.right()) * themeScale)), bgHeight, theme.bgColor, themeScale);
//                        yOffset += height;
//                        bgYOffset += bgHeight;
//                        xOffset = 0;
//                        formatting = theme.fgColor.copy();
//
//                    } else if (e instanceof TextElement te) {
//                        wipPieces.add(new RenderPiece(te.getText(), null, font, (int) (xOffset), section.hPaddingOffset(theme), yOffset, te.getColor(formatting.getColor()), theme, baseScale));
//                        xOffset += te.getTextWidth() * theme.getScale()/baseScale;
//
//                    } else if (e instanceof MultiElement me) {
//                        List<HudElement> expanded = me.expand();
//                        if (expanded.isEmpty()) {
//                            if (theme.convertLineBreaks && me.ignoreNewlineIfEmpty())
//                                elements.set(i, new FunctionalElement.IgnoreNewLineIfSurroundedByNewLine());
//                        } else
//                            elements.addAll(i + 1, me.expand());
//
//                    } else if (e instanceof IconElement ie) {
//                        wipPieces.add(new RenderPiece(ie, ListManager.getValue(ie.getProviderID()), null, (int) xOffset, section.hPaddingOffset(theme), yOffset, formatting.getColor(), theme, baseScale));
//                        xOffset += ie.getTextWidth() * theme.getScale()/baseScale;
//
//                    } else if (e instanceof FunctionalElement.ChangeFormatting cfe) {
//                        if (cfe.getFormatting() != null)
//                            formatting.apply(cfe.getFormatting(), theme);
//
//                    } else if (e instanceof FunctionalElement.ChangeTheme cte) {
//                        bgBuilder.onThemeChange(cte);
//                        theme = cte.theme;
//                        font = cte.theme.font;
//
//                    } else if (e instanceof FunctionalElement.ExitList el) {
//                        for (int j = i + 1; j < elements.size(); j++) {
//                            if (elements.get(j) instanceof FunctionalElement.PopList pl && pl.providerID.equals(el.providerID))
//                                break;
//                            i++;
//                        }
//                    } else if (e instanceof FunctionalElement.ContinueList cl) {
//                        for (int j = i + 1; j < elements.size(); j++) {
//                            if (elements.get(j) instanceof FunctionalElement.AdvanceList pl && pl.providerID.equals(cl.providerID))
//                                break;
//                            if (elements.get(j) instanceof FunctionalElement.PopList pl && pl.providerID.equals(cl.providerID))
//                                break;
//                            i++;
//                        }
//                    }
//
//                }
//
//            }
//
//            int height = bgBuilder.totalHeight;
//            int width = section.width > 0 ? section.width : bgBuilder.maxWidth;
//
//            int sectionXOffset = section.xOffset + switch (section.sAlign) {
//                case LEFT -> 3;
//                case RIGHT ->  (int) (client.getWindow().getScaledWidth() * (1 / baseScale))   - width - 1;
//                case CENTER -> (int) (client.getWindow().getScaledWidth() * (1 / baseScale))/2 - width/2;
//            };
//            int sectionYOffset = section.yOffset + (
//                    section instanceof Section.Top ? 1 :
//                            section instanceof Section.Bottom ? (int) (client.getWindow().getScaledHeight() * (1 / baseScale)) - height - 8:
//                                    (int) (client.getWindow().getScaledHeight() * (1 / baseScale))/2 - height/2 - 1
//            );
//
//            if (section.textAlign == Section.Align.RIGHT) {
//                if (section.width < 0)
//                    for (var piece : wipPieces)
//                        pieces.add( piece.adjust(sectionXOffset, bgBuilder.maxWidth - piece.lineWith, sectionYOffset) );
//                else
//                    for (var piece : wipPieces)
//                        pieces.add( piece.adjust(sectionXOffset, section.width - piece.lineWith, sectionYOffset) );
//            }
//            else if (section.textAlign == Section.Align.CENTER) {
//                for (var piece : wipPieces)
//                    pieces.add( piece.adjust(sectionXOffset, width/2 - piece.lineWith/2, sectionYOffset) );
//            }
//            else {
//                for (var piece : wipPieces)
//                    pieces.add( piece.adjust(sectionXOffset, 0, sectionYOffset) );
//            }
//
//            bgBuilder.finalizeBg(context, bgBuffer, (int) (sectionXOffset * baseScale), (int) (sectionYOffset * baseScale));
//            wipPieces.clear();
//
//        }
//
//        Profilers.get().pop();
//        Profilers.get().push("background_rendering");
//        context.getMatrices().push();
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
//        BufferRenderer.drawWithGlobalProgram(bgBuffer.end());
//        RenderSystem.disableBlend();
//        context.getMatrices().pop();
//        Profilers.get().pop();
//        Profilers.get().push("foreground_rendering");
//        context.getMatrices().push();
//
//        context.getMatrices().scale(baseScale, baseScale, 1);
//        float scale = baseScale;
//        for (RenderPiece piece : pieces) {
//            font = piece.font;
//            int y = piece.shiftTextUp ? piece.y-1 : piece.y;
//
//            if (piece.scale != scale) {
//                context.getMatrices().pop();
//                context.getMatrices().push();
//                context.getMatrices().scale(piece.scale, piece.scale, 1);
//            }
//
//            if (piece.element instanceof IconElement ie )
//                try { ie.render(context, piece); }
//                catch (Exception e){
//                    CustomHud.LOGGER.catching(e);
//                }
//            else if (piece.element instanceof String value && !value.isEmpty())
//                context.drawText(client.textRenderer, value, piece.x, y, piece.color, piece.shadow);
//            else if (piece.element instanceof Text text)
//                context.drawText(client.textRenderer, text, piece.x, y, piece.color, piece.shadow);
//
//        }
//
//        if (profile.charts) {
//            int right = context.getScaledWindowWidth();
//            int center = right / 2;
//
//            DebugHud hud = client.inGameHud.getDebugHud();
//            switch (profile.leftChart) {
//                case FPS -> hud.renderingChart.render(context, 0, hud.renderingChart.getWidth(center));
//                case TICK -> hud.tickChart.render(context, 0, hud.tickChart.getWidth(center));
//                case PING -> hud.pingChart.render(context, 0, hud.pingChart.getWidth(center));
//                case PACKET_SIZE -> hud.packetSizeChart.render(context, 0, hud.packetSizeChart.getWidth(center));
//            }
//            switch (profile.rightChart) {
//                case FPS -> { int w = hud.renderingChart.getWidth(center); hud.renderingChart.render(context, right - w, w); }
//                case TICK -> { int w = hud.tickChart.getWidth(center); hud.tickChart.render(context, right - w, w); }
//                case PING -> { int w = hud.pingChart.getWidth(center); hud.pingChart.render(context, right - w, w); }
//                case PACKET_SIZE -> { int w = hud.packetSizeChart.getWidth(center); hud.packetSizeChart.render(context, right - w, w); }
//            }
//        }
//
//
//        Profilers.get().pop();
//        context.getMatrices().pop();
//        Profilers.get().pop();
//
//    }
//
//    private static String leftTrim(String str) {
//        int i = 0;
//        while (i < str.length() && Character.isWhitespace(str.charAt(i)))
//            i++;
//        return str.substring(i);
//    }
//
//
//}
