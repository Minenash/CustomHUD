package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.HudElements.list.ListProvider;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;
import java.util.function.Supplier;

public class StatusEffectIconElement extends IconElement {
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = Identifier.of("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = Identifier.of("hud/effect_background");

    private final Supplier<StatusEffectInstance> supplier;
    private final boolean background;
    private final int effectOffset;
    private final int renderWidth;

    public StatusEffectIconElement(UUID providerID, Supplier<StatusEffectInstance> supplier, Flags flags, boolean background) {
        super(flags, flags.scale == 1 ? 11 : 12);
        this.supplier = supplier;
        this.background = background;
        this.effectOffset = scale == 1 ? 1 : Math.round(3F/2*scale);
        this.providerID = providerID;
        this.renderWidth = flags.scale == 1 ? 11 : (int) (flags.scale * 12);
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        context.getMatrices().push();
        StatusEffectInstance effect = piece.value != null ? (StatusEffectInstance) piece.value : supplier.get();
        if (effect == null)
            return;

        int y= piece.y - 2;
        if (!referenceCorner && scale != 1)
           y-= (renderWidth-12)/2;

        Sprite sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(effect.getEffectType());
        int m = effect.getDuration();
        float f = !effect.isDurationBelow(200) ? 1.0f :
            MathHelper.clamp((float)m / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)m * (float)Math.PI / 5.0f) * MathHelper.clamp((float)(10 - m / 20) / 10.0f * 0.25f, 0.0f, 0.25f);

        context.getMatrices().translate(piece.x + shiftX, y + shiftY, 0);
        rotate(context.getMatrices(), renderWidth, renderWidth);

        RenderSystem.enableBlend();
        if (background)
            context.drawGuiTexture(RenderLayer::getGuiTextured, effect.isAmbient() ? EFFECT_BACKGROUND_AMBIENT_TEXTURE : EFFECT_BACKGROUND_TEXTURE, 0, 0, renderWidth, renderWidth);
        context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, effectOffset, effectOffset, (int)(9*scale), (int)(9*scale), ColorHelper.getWhite(f));
        RenderSystem.disableBlend();
        context.getMatrices().pop();

    }

}
