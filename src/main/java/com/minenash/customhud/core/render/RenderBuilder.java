package com.minenash.customhud.core.render;

import com.minenash.customhud.core.data.HudTheme;
import com.minenash.customhud.core.data.Profile;
import com.minenash.customhud.core.data.Section;
import com.minenash.customhud.core.elements.ConditionalElement;
import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.HudElement;
import com.minenash.customhud.mc1_20.elements.icon.ItemRenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class RenderBuilder {

    public static List<RenderPiece> build(Profile profile, boolean isChatOpen, int width, Function<String,Integer> getWidth) {
        List<RenderPiece> pieces = new ArrayList<>();

        for (Section section : profile.sections) {
            HudTheme theme = profile.baseTheme;

            if (section == null || isChatOpen && section.hideOnChat)
                continue;

            int color = theme.fgColor;
            int right = (int) (width * (1 / theme.scale)) - 3 + section.xOffset;
            boolean dynamicWidth = section.width == -1;
            int piecesOffset = pieces.size();


            int lineCount = 0;
            List<HudElement> elements = new ArrayList<>();
            for (HudElement e : section.elements)
                lineCount += addElement(elements, e);

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

            for (HudElement e : elements) {
                if (e instanceof FunctionalElement) {
                    String str = builder.toString();
                    pieces.add( new RenderPiece.Foreground(str, xOffset, y, color, theme.font, theme.textShadow) );
                    xOffset += getWidth.apply(str);
                    builder.setLength(0);

                    if (e instanceof FunctionalElement.NewLine) {
                        int x1 = section.getStartX(right, xOffset);
                        for (int i = piecesOffset; i < pieces.size(); i++)
                            if (pieces.get(i) instanceof RenderPiece.Foreground piece)
                                piece.x += x1;
                        piecesOffset = pieces.size();

                        if (dynamicWidth && xOffset != 0)
                            pieces.add( new RenderPiece.Background(x1-2, y - 2, x1 + xOffset + 2, y + 9 + theme.lineSpacing - 2, theme.bgColor) );

                        y += 9 + theme.lineSpacing;
                        xOffset = 0;
                        color = theme.fgColor;
                    } else if (e instanceof FunctionalElement.ChangeColor cce) {
                        color = cce.color;
                    } else if (e instanceof FunctionalElement.ChangeTheme cte) {
                        if (!dynamicWidth && theme.bgColor != cte.theme.bgColor) {
                            int x1 = section.getStartX(right + 3, section.width) - 2;
                            pieces.add(new RenderPiece.Background(x1, staticWidthY - 2, x1 + section.width, y - 2, theme.bgColor));
                            staticWidthY = y;
                        }
                        theme = cte.theme;
                    } else if (e instanceof ItemRenderUtil ie) {
                        xOffset += ie.getTextWidth();
                    }
                } else {
                    builder.append(e.getString());
                }
            }

            if (!dynamicWidth) {
                int x1 = section.getStartX(right + 3, section.width) - 2;
                pieces.add(new RenderPiece.Background(x1, staticWidthY - 2, x1 + section.width, y - 2, theme.bgColor));
            }

        }
        return pieces;
    }

    private static int addElement(List<HudElement> allElements, HudElement element) {
        if (element instanceof ConditionalElement ce) {
            int nl = 0;
            for (HudElement e : ce.get())
                nl += addElement(allElements, e);
            return nl;
        }
        else {
            allElements.add(element);
            return element instanceof FunctionalElement.NewLine ? 1 : 0;
        }

    }

}
