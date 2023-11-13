package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class PlayerHeadIconElement extends IconElement {

    private List<PlayerListEntry> players;
    private int playerIndex = 0;

    public PlayerHeadIconElement(Flags flags) {
        super(flags, 10);
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {
        PlayerListEntry playerEntry = players.get(playerIndex);
        playerIndex++;
        if (playerIndex >= players.size())
            playerIndex = 0;

        if (!referenceCorner)
            y -= (10*scale-10)/2;

        PlayerEntity playerEntity = CLIENT.world.getPlayerByUuid(playerEntry.getProfile().getId());
        boolean flip = playerEntity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerEntity);
        boolean hat = playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT);
        context.getMatrices().translate(x+((int)scale) + shiftX, y + shiftY, 0);
        int size = (int)(8*scale);
        rotate(context.getMatrices(), size, size);
        PlayerSkinDrawer.draw(context, playerEntry.getSkinTextures().texture(), 0, 0, size, hat, flip);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        this.players = (List<PlayerListEntry>) values;
    }

}
