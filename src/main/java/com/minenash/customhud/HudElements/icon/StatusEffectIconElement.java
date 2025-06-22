package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
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
        context.getMatrices().pushMatrix();
        StatusEffectInstance effect = piece.value != null ? (StatusEffectInstance) piece.value : supplier.get();
        if (effect == null)
            return;

        int y= piece.y - 2;
        if (!referenceCorner && scale != 1)
           y-= (renderWidth-12)/2;

        Identifier texture = InGameHud.getEffectTexture(effect.getEffectType());
        int m = effect.getDuration();
        float f = !effect.isDurationBelow(200) ? 1.0f :
            MathHelper.clamp((float)m / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)m * (float)Math.PI / 5.0f) * MathHelper.clamp((float)(10 - m / 20) / 10.0f * 0.25f, 0.0f, 0.25f);

        context.getMatrices().translate(piece.x + shiftX, y + shiftY);
        rotate(context.getMatrices(), renderWidth, renderWidth);

        if (background)
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, effect.isAmbient() ? EFFECT_BACKGROUND_AMBIENT_TEXTURE : EFFECT_BACKGROUND_TEXTURE, 0, 0, renderWidth, renderWidth);
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, texture, effectOffset, effectOffset, (int)(9*scale), (int)(9*scale), ColorHelper.getWhite(f));
        context.getMatrices().popMatrix();

    }

}
