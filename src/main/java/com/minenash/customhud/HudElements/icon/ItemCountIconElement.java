package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;

public class ItemCountIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    private final Item item;
    private final int numSize;

    public ItemCountIconElement(Item item, Flags flags) {
        super(flags, 11);
        this.item = item;
        this.numSize = flags.numSize;
    }

    @Override
    public String getString() {
        return getNumber().toString();
    }

    @Override
    public Number getNumber() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player == null ? 0 : player.getInventory().count(item);
    }

    @Override
    public boolean getBoolean() {
        return getNumber().intValue() > 0;
    }

    @Override
    public int getTextWidth() {
        return width;
    }

    public void render(DrawContext context, RenderPiece piece) {
        ItemStack stack = new ItemStack(item, (int) getNumber());

        Matrix3x2fStack matrices = context.getMatrices();

        matrices.pushMatrix();
        matrices.translate(piece.x + shiftX, piece.y + shiftY - 2);
        if (!referenceCorner)
            matrices.translate(0, -(11*scale-11)/2);
        matrices.scale(11/16F * scale, 11/16F * scale);
        rotate(matrices, 16, 16);

        context.drawItem(stack, 0, 0);

        String string = String.valueOf(stack.getCount());
        string = numSize == 0 ? string : numSize == 1 ? Flags.subNums(string) : Flags.supNums(string);
        matrices.translate(0.0F, 0.0F);
        context.drawText(client.textRenderer, string, 19 - 2 - client.textRenderer.getWidth(string), numSize == 2 ? 0 : 9, 16777215, true);

        matrices.popMatrix();
    }

}
