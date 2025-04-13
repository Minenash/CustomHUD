package com.minenash.customhud.render;

import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.data.Section;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;

import java.util.ArrayList;
import java.util.List;

public class BackgroundBuilder {

    public final int sectionWidth;
    public final Section.Align textAlign;
    public final boolean isDynamic;
    public final boolean isMax;
    public final boolean isSet;

    public BackgroundBuilder(Section section) {
        this.sectionWidth = section.width;
        this.textAlign = section.textAlign;
        this.isDynamic = sectionWidth == -1;
        this.isMax = sectionWidth == -2;
        this.isSet = sectionWidth >= 0;
    }

    public int maxWidth = 0;
    List<BgRenderPiece> bgPieces = new ArrayList<>();
    BgRenderPiece nonDynamicBgPiece = null;

    public void addLine(int yOffset, int width, int height, int color) {
        maxWidth = Math.max(maxWidth, width);
        if (isDynamic)
            bgPieces.add( new BgRenderPiece(0, yOffset, width, height, color) );
        else if (nonDynamicBgPiece == null)
            nonDynamicBgPiece = new BgRenderPiece(0, yOffset, width, height, color);
        else
            nonDynamicBgPiece.height += height;
    }

    public void onThemeChange(FunctionalElement.ChangeTheme cte) {
        if (!isDynamic && nonDynamicBgPiece != null && nonDynamicBgPiece.color != cte.theme.bgColor) {
            int yOffset = nonDynamicBgPiece.y + nonDynamicBgPiece.height;
            bgPieces.add( nonDynamicBgPiece );
            nonDynamicBgPiece = new BgRenderPiece(0, yOffset, 0, 0, cte.theme.bgColor);
        }

    }

    public void finalizeBg(DrawContext context, int x, int y) {
        if (isDynamic) {
            if (textAlign == Section.Align.RIGHT)
                for (var piece : bgPieces)
                    piece.x = maxWidth - piece.width;
            else if (textAlign == Section.Align.CENTER)
                for (var piece : bgPieces)
                    piece.x = maxWidth/2 - piece.width/2;
        }
        else {
            if (nonDynamicBgPiece != null)
                bgPieces.add( nonDynamicBgPiece );
            int width = isSet ? sectionWidth : maxWidth;
            for (var piece : bgPieces)
                piece.width = width;
        }

        for (var piece : bgPieces) {
            context.fill(x + piece.x, y + piece.y, x + piece.x + piece.width, y + piece.y + piece.height, piece.color);
        }
    }
}
