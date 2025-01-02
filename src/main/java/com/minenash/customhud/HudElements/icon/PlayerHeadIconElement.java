package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerModelPart;

import static com.minenash.customhud.CustomHud.CLIENT;

public class PlayerHeadIconElement extends IconElement {

    public PlayerHeadIconElement(Flags flags) {
        super(flags, 10);
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        context.getMatrices().push();
        float y = piece.y;
        if (!referenceCorner)
            y -= (10*scale-10)/2;

        boolean flip = CLIENT.player != null && LivingEntityRenderer.shouldFlipUpsideDown(CLIENT.player);
        boolean hat = CLIENT.player != null && CLIENT.player.isPartVisible(PlayerModelPart.HAT);
        context.getMatrices().translate(piece.x + scale + shiftX, y + shiftY, 0);
        int size = (int)(8*scale);
        rotate(context.getMatrices(), size, size);
        PlayerSkinDrawer.draw(context, CLIENT.player.getSkinTextures().texture(), 0, 0, size, hat, flip, -1);
        context.getMatrices().pop();
    }

}
