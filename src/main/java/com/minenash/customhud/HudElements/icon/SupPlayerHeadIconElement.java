package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;

import java.util.UUID;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class SupPlayerHeadIconElement extends IconElement {

    private final Supplier<PlayerListEntry> supplier;
    public SupPlayerHeadIconElement(UUID providerID, Supplier<PlayerListEntry> supplier, Flags flags) {
        super(flags, 10);
        this.providerID = providerID;
        this.supplier = supplier;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        int y = piece.y;
        PlayerListEntry playerEntry = piece.value != null ? (PlayerListEntry) piece.value : supplier.get();
        if (playerEntry == null)
            return;

        context.getMatrices().push();
        if (!referenceCorner)
            y -= (10*scale-10)/2;

        PlayerEntity playerEntity = CLIENT.world.getPlayerByUuid(playerEntry.getProfile().getId());
        boolean flip = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
        boolean hat = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
        context.getMatrices().translate(piece.x+((int)scale) + shiftX, y + shiftY, 0);
        int size = (int)(8*scale);
        rotate(context.getMatrices(), size, size);
        PlayerSkinDrawer.draw(context, playerEntry.getSkinTextures().texture(), 0, 0, size, hat, flip, -1);
        context.getMatrices().pop();
    }

}
