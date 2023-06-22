package com.minenash.customhud.mc1_20.elements.icon;

import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.HudElement;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public abstract class ItemRenderUtil implements HudElement, FunctionalElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    protected final float scale;
    protected final int shiftX;
    protected final int shiftY;
    protected final boolean referenceCorner;

    protected ItemRenderUtil(Flags flags) {
        scale = (float) flags.scale;
        shiftX = flags.iconShiftX;
        shiftY = flags.iconShiftY;
        referenceCorner = flags.iconReferenceCorner;
    }

    public abstract void render(DrawContext context, int x, int y, float profileScale);
    public abstract int getTextWidth();

    @Override
    public String getString() {
        return "\uFFFE";
    }

    public static void renderItemStack(int x, int y, float profileScale, ItemStack stack, boolean referenceCorner, float scale) {
        BakedModel model = client.getItemRenderer().getModel(stack, null, null, 0);
        client.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
        RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        MatrixStack matrixStack = RenderSystem.getModelViewStack();
        matrixStack.push();
        matrixStack.scale(profileScale, profileScale, 1);

        matrixStack.translate(x+(5.5*scale), y+3.5, 100.0f); //+ client.getItemRenderer().zOffset

        if (referenceCorner)
            matrixStack.translate(0, (10*scale-10)/2, 0);

        matrixStack.scale(10 * scale, -10 * scale, 1);

        if (!model.isSideLit())
            DiffuseLighting.disableGuiDepthLighting();

        RenderSystem.applyModelViewMatrix();
        VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();
        client.getItemRenderer().renderItem(stack, ModelTransformationMode.GUI, false, new MatrixStack(), immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();

        if (!model.isSideLit())
            DiffuseLighting.enableGuiDepthLighting();

        matrixStack.pop();
        RenderSystem.applyModelViewMatrix();
    }
}
