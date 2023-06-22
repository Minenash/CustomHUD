package com.minenash.customhud.mc1_20.elements.icon;

import com.minenash.customhud.core.elements.IconElement;
import com.minenash.customhud.core.data.Flags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
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
        this.width = flags.iconWidth != -1 ? flags.iconWidth : MathHelper.ceil(11*scale);;
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

    public void render(DrawContext context, int x, int y, float profileScale) {
        ItemStack stack = getStack();
        if (stack == null || stack.isEmpty())
            return;
        x += shiftX;
        y += shiftY;

        ItemRenderUtil.renderItemStack(x, y, profileScale, stack, referenceCorner, scale);
        if (showCount)
            renderCount(context, stack.getCount(), x, y, profileScale);
        if (showDur)
            renderDur(context, stack, x, y, profileScale);
        if (showCooldown)
            renderCooldown(context, stack, x, y, profileScale);
    }

    private void renderCount(DrawContext context, int count, int x, int y, float profileScale) {
        if (count != 1) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.scale(profileScale, profileScale, 1);
            matrixStack.translate(x + (scale-1)/4,y + (scale-1)/4,200);
            if (referenceCorner)
                matrixStack.translate(0, (10*scale-10)/2, 0);
            matrixStack.scale(10/16F * scale, 10/16F * scale, 1);

            String string = String.valueOf(count);
            context.drawTextWithShadow(client.textRenderer, string,
                    (int) (7.5F - client.textRenderer.getWidth(string)),
                    (int) (6 / scale + 0.5F),
                    0xFFFFFF);
            matrixStack.pop();
        }
    }

    public void renderDur(DrawContext context, ItemStack stack, int x, int y, float profileScale) {
        if (stack.isItemBarVisible()) {
            //TODO: CHECK
//            RenderSystem.disableDepthTest();
//            RenderSystem.disableBlend();
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            this.renderGuiQuad(context, profileScale, x + scale, y + 0.5 + 6*(1+(scale-1)/2), 9, 2*11/16F, 0xFF000000);
            this.renderGuiQuad(context, profileScale, x + scale, y + 0.5 + 6*(1+(scale-1)/2), i-4, 11/16F, 0xFF000000 | j);
//            RenderSystem.enableBlend();
//            RenderSystem.enableDepthTest();
        }
    }

    public void renderCooldown(DrawContext context, ItemStack stack, int x, int y, float profileScale) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        float f = player == null ? 0.0f : player.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
        if (f > 0.0f) {
//            RenderSystem.disableDepthTest();
//            RenderSystem.enableBlend();
//            RenderSystem.defaultBlendFunc();
            this.renderGuiQuad(context, profileScale, x+0.5*scale, y + MathHelper.floor(10 * (1.0f - f))*scale - (9*scale-9)/2 - 1, 10, MathHelper.ceil(10 * f), 0x40FFFFFF);
//            RenderSystem.enableDepthTest();
        }
    }

    private void renderGuiQuad(DrawContext context, float profileScale, double x, double y, double width, double height, int color) {
        if (referenceCorner)
            y += (10*scale-10)/2;
        x *= profileScale;
        y *= profileScale;
        width *= profileScale;
        height *= profileScale;
        width *= scale;
        height *= scale;

        context.fill((int) x, (int) y, (int) (x+width), (int) (y+height), color);
    }

}
