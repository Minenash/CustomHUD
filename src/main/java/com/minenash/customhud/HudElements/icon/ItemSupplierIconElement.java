package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
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
    private final int numSize;

    private List<ItemStack> stacks = null;
    private int stackIndex = 0;

    public ItemSupplierIconElement(Supplier<ItemStack> supplier, Flags flags) {
        super(flags, 11);
        this.supplier = supplier;
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
        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(x + shiftX, y + shiftY - 2, 0);
        if (!referenceCorner)
            matrices.translate(0, -(11*scale-11)/2, 0);
        matrices.scale(11/16F * scale, 11/16F * scale, 1);
        rotate(matrices, 16, 16);

        context.drawItem(stack, 0, 0);

        if (showCount && stack.getCount() != 1) {
            String string = String.valueOf(stack.getCount());
            string = numSize == 0 ? string : numSize == 1 ? Flags.subNums(string) : Flags.supNums(string);
            matrices.translate(0.0F, 0.0F, 200.0F);
            context.drawText(client.textRenderer, string, 19 - 2 - client.textRenderer.getWidth(string), numSize == 2 ? 0 : 9, 16777215, true);
        }

        if (showDur && stack.isItemBarVisible()) {
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            context.fill(RenderLayer.getGuiOverlay(), 2, 13, 2 + 13, 13 + 2, -16777216);
            context.fill(RenderLayer.getGuiOverlay(), 2, 13, 2 + i, 13 + 1, j | -16777216);
        }

        float f = client.player.getItemCooldownManager().getCooldownProgress(stack.getItem(), client.getTickDelta());
        if (showCooldown && f > 0.0F) {
            int k = MathHelper.floor(16.0F * (1.0F - f));
            int l = k + MathHelper.ceil(16.0F * f);
            context.fill(RenderLayer.getGuiOverlay(), 0, k, 16, l, Integer.MAX_VALUE);
        }

        matrices.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        stacks = (List<ItemStack>) values;
        stackIndex = 0;
    }
}
