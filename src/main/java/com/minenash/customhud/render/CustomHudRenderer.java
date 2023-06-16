package com.minenash.customhud.render;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.core.render.RenderBuilder;
import com.minenash.customhud.core.render.RenderPiece;
import com.minenash.customhud.elements.icon.ItemRenderUtil;
import com.minenash.customhud.core.data.Profile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.render.*;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

import java.util.List;

public class CustomHudRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static Identifier font;

    public static void render(DrawContext context, float _tickDelta) {

        Profile profile = CustomHud.getActiveProfile();
        if (profile == null || client.options.debugEnabled)
            return;

        context.getMatrices().push();
        context.getMatrices().scale(profile.baseTheme.scale, profile.baseTheme.scale, 1);
        BufferBuilder bgBuilder = Tessellator.getInstance().getBuffer();
        bgBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        List<RenderPiece> pieces = RenderBuilder.build(profile, client.currentScreen instanceof ChatScreen, client.getWindow().getScaledWidth(), client.textRenderer::getWidth);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferRenderer.drawWithGlobalProgram(bgBuilder.end());
        RenderSystem.disableBlend();


        for (RenderPiece piece : pieces)
            if (piece instanceof RenderPiece.Background bgPiece)
                addLineBg(context, bgBuilder, bgPiece);

        for (RenderPiece piece : pieces)
            if (piece instanceof RenderPiece.Foreground fgPiece) {
                if (fgPiece.element instanceof String value && !value.isEmpty()) {
                    font = new Identifier(fgPiece.font);
                    context.drawText(client.textRenderer, value, fgPiece.x, fgPiece.y, fgPiece.color, fgPiece.shadow);
                }
                if (fgPiece.element instanceof ItemRenderUtil ie)
                    ie.render(context, fgPiece.x, fgPiece.y, profile.baseTheme.scale);
            }


        context.getMatrices().pop();

    }

    private static void addLineBg(DrawContext context, BufferBuilder builder, RenderPiece.Background bg) {
        Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
        float f = (float)(bg.color >> 24 & 255) / 255.0F;
        float g = (float)(bg.color >> 16 & 255) / 255.0F;
        float h = (float)(bg.color >> 8 & 255) / 255.0F;
        float j = (float)(bg.color & 255) / 255.0F;
        builder.vertex(matrix, (float)bg.x1, (float)bg.y2, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)bg.x2, (float)bg.y2, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)bg.x2, (float)bg.y1, 0.0F).color(g, h, j, f).next();
        builder.vertex(matrix, (float)bg.x1, (float)bg.y1, 0.0F).color(g, h, j, f).next();
    }


}
