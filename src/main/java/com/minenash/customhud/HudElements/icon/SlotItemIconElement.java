package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class SlotItemIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private final int slot;
    private final boolean showCount, showDur, showCooldown;
    private final int width;

    public SlotItemIconElement(int slot, Flags flags) {
        super(flags);
        this.slot = slot;
        this.width = flags.iconWidth != -1 ? flags.iconWidth : (int)(11*scale);;
        this.showCount = flags.iconShowCount;
        this.showDur = flags.iconShowDur;
        this.showCooldown = flags.iconShowCooldown;
    }

    private ItemStack getStack() {
        return client.player.getStackReference(slot).get();
    }

    @Override
    public Number getNumber() {
        return Item.getRawId(getStack().getItem());
    }

    @Override
    public boolean getBoolean() {
        return getStack().isEmpty();
    }

    @Override
    public int getTextWidth() {
        return getStack().isEmpty() ? 0 : width;
    }

    public int render(MatrixStack matrix, int x, int y) {
        ItemStack stack = getStack();
        if (stack == null || stack.isEmpty())
            return 0;
        x += shiftX;
        y += shiftY;
        renderItemStack(x, y, stack);
        if (showCount)
            renderCount(stack.getCount(), x, y);
        if (showDur)
            renderDur(stack, x, y);
        if (showCooldown)
            renderCooldown(stack, x, y);

        return getTextWidth();
    }

    private void renderCount(int count, int x, int y) {
        if (count != 1) {
            MatrixStack matrixStack = new MatrixStack();
            matrixStack.scale(11/16F, 11/16F, 1);
            matrixStack.translate(-2, -4, 200);
            String string = String.valueOf(count);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            client.textRenderer.draw(string, (float)(x + 19 - 2 - client.textRenderer.getWidth(string)) * 16/11F, (float)(y + 6 + 3) * 16/11F, 0xFFFFFF, true, matrixStack.peek().getPositionMatrix(), immediate, false, 0, LightmapTextureManager.MAX_LIGHT_COORDINATE);
            immediate.draw();
        }
    }

    public void renderDur(ItemStack stack, int x, int y) {
        if (stack.isItemBarVisible()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            this.renderGuiQuad(bufferBuilder, x + 1, y + 9, 9, 2*11/16F, 0, 0, 0, 255);
            this.renderGuiQuad(bufferBuilder, x + 1, y + 9, i-4, 11/16F, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    public void renderCooldown(ItemStack stack, int x, int y) {
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;

        float f = clientPlayerEntity == null ? 0.0f : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
        if (f > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator2 = Tessellator.getInstance();
            BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
            this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(11 * (1.0f - f)), 11, MathHelper.ceil(11 * f), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private void renderGuiQuad(BufferBuilder buffer, double x, double y, double width, double height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x + 0, y + 0, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + 0, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + height, 0.0).color(red, green, blue, alpha).next();
        buffer.vertex(x + width, y + 0, 0.0).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithShader(buffer.end());
    }

}
