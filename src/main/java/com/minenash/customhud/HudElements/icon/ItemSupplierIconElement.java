package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.function.Supplier;

public class ItemSupplierIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private final Supplier<ItemStack> supplier;
    private final boolean showCount, showDur, showCooldown;
    private final int width;
    private final int numSize;

    private List<ItemStack> stacks = null;
    private int stackIndex = 0;

    public ItemSupplierIconElement(Supplier<ItemStack> supplier, Flags flags) {
        super(flags);
        this.supplier = supplier;
        this.width = flags.iconWidth != -1 ? flags.iconWidth : MathHelper.ceil(11*scale);;
        this.showCount = flags.iconShowCount;
        this.showDur = flags.iconShowDur;
        this.showCooldown = flags.iconShowCooldown;
        this.numSize = flags.numSize;
    }

    @Override
    public Number getNumber() {
        return Item.getRawId(supplier.get().getItem());
    }

    @Override
    public boolean getBoolean() {
        return supplier.get().isEmpty();
    }

    @Override
    public int getTextWidth() {
        return supplier.get().isEmpty() ? 0 : width;
    }

    //TODO FIX: Conditional in list?
    public void render(DrawContext context, int x, int y, float profileScale) {
        ItemStack stack = stacks == null ? supplier.get() : stacks.get(stackIndex++);
        if (stack == null || stack.isEmpty())
            return;
        x += shiftX;
        y += shiftY;

        renderItemStack(x, y, profileScale, stack);
        if (showCount)
            renderCount(context, stack.getCount(), x, y);
        if (showDur)
            renderDur(context, stack, x, y);
        if (showCooldown)
            renderCooldown(context, stack, x, y);
    }

    private void renderCount(DrawContext context, int count, int x, int y) {
        if (count != 1) {
            MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.translate(x + (scale-1)/4,y + (scale-1)/4,200);
            if (referenceCorner)
                matrixStack.translate(0, (10*scale-10)/2, 0);
            matrixStack.scale(10/16F * scale, 10/16F * scale, 1);
            matrixStack.translate(0.6,0.6,0);

            String string = String.valueOf(count);
            string = numSize == 0 ? string : numSize == 1 ? Flags.subNums(string) : Flags.supNums(string);

            int offset = numSize == 0 ? 0 : numSize == 1 ? 1 : 9;

            context.drawTextWithShadow(client.textRenderer, string,
                    17 - client.textRenderer.getWidth(string), (int) (6 / scale + 0.5F) - offset, 0xFFFFFF);
            matrixStack.pop();
        }
    }

    public void renderDur(DrawContext context, ItemStack stack, int x, int y) {
        if (stack.isItemBarVisible()) {
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            this.renderGuiQuad(context, x + scale, y + 0.5 + 6*(1+(scale-1)/2), 9, 2*11/16F, 0xFF000000);
            this.renderGuiQuad(context, x + scale, y + 0.5 + 6*(1+(scale-1)/2), i-4, 11/16F, 0xFF000000 | j);
        }
    }

    public void renderCooldown(DrawContext context, ItemStack stack, int x, int y) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        float f = player == null ? 0.0f : player.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
        if (f > 0.0f) {
            this.renderGuiQuad(context, x+0.5*scale, y + MathHelper.floor(10 * (1.0f - f))*scale - (9*scale-9)/2 - 1, 10, MathHelper.ceil(10 * f), 0x40FFFFFF);
        }
    }

    private void renderGuiQuad(DrawContext context, double x, double y, double width, double height, int color) {
        if (referenceCorner)
            y += (10*scale-10)/2;
        width *= scale;
        height *= scale;

        context.fill((int) x, (int) y, (int) (x+width), (int) (y+height), color);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        stacks = (List<ItemStack>) values;
        stackIndex = 0;
    }
}