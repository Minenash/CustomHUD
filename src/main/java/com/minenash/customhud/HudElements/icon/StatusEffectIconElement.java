package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class StatusEffectIconElement extends IconElement {
    private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = new Identifier("hud/effect_background_ambient");
    private static final Identifier EFFECT_BACKGROUND_TEXTURE = new Identifier("hud/effect_background");

    private final boolean background;
    private final int width;
    private final int bgWidth;
    private final int effectOffset;

    private List<StatusEffectInstance> effects;
    private int effectIndex = 0;

    public StatusEffectIconElement(Flags flags, boolean background) {
        super(flags);
        this.background = background;
        bgWidth = scale == 1 ? 11 : (int)(12*scale);
        effectOffset = scale == 1 ? 1 : Math.round(3F/2*scale);
        this.width = flags.iconWidth != -1 ? flags.iconWidth : bgWidth;
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {
        StatusEffectInstance effect = effects.get(effectIndex);
        effectIndex++;

        y-=2;
        if (!referenceCorner && scale != 1)
           y-= (bgWidth-12)/2;

        Sprite sprite = MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(effect.getEffectType());
        int m = effect.getDuration();
        float f = !effect.isDurationBelow(200) ? 1.0f :
            MathHelper.clamp((float)m / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)m * (float)Math.PI / 5.0f) * MathHelper.clamp((float)(10 - m / 20) / 10.0f * 0.25f, 0.0f, 0.25f);

        RenderSystem.enableBlend();
        if (background)
            context.drawGuiTexture(effect.isAmbient() ? EFFECT_BACKGROUND_AMBIENT_TEXTURE : EFFECT_BACKGROUND_TEXTURE, x, y, bgWidth, bgWidth);
        context.setShaderColor(1.0f, 1.0f, 1.0f, f);
        context.drawSprite(x+effectOffset, y+effectOffset, 0, (int)(9*scale), (int)(9*scale), sprite);
        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();

    }

    @Override
    public int getTextWidth() {
        return width;
    }


    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        effects = (List<StatusEffectInstance>) values;
        effectIndex = 0;
    }
}
