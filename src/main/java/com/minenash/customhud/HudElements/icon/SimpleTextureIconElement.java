package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;

public class SimpleTextureIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Identifier TEXTURE_NOT_FOUND = Identifier.of("textures/item/barrier.png");

    private final Identifier texture;
    private final int textureWidth;
    private final int textureHeight;
    private final int width;
    private final int height;
    private final int yOffset;
    private final int textWidth;
    private final boolean crosshair;

    private final boolean iconAvailable;

    public SimpleTextureIconElement(Identifier texture, boolean crosshair, Flags flags) {
        super(flags, 0);
        this.crosshair = crosshair;

        NativeImage img = null;
        try {
            Optional<Resource> resource = client.getResourceManager().getResource(texture);
            if (resource.isPresent())
                img = NativeImage.read(resource.get().getInputStream());
        }
        catch (IOException e) { CustomHud.LOGGER.catching(e); }


        iconAvailable = img != null;
        this.texture = iconAvailable ? texture : TEXTURE_NOT_FOUND;

        textureWidth = iconAvailable ? img.getWidth() : 16;
        textureHeight = iconAvailable ? img.getHeight() : 16;

        height = (int) (11 * flags.scale);
        width = (int) (height * ((float)textureWidth/textureHeight));
        yOffset = referenceCorner ? 0 : (int) ((height*scale-height)/(scale*2));
        textWidth = flags.iconWidth == -1 ? width : flags.iconWidth;

    }

    @Override
    public Number getNumber() {
        return 0;
    }

    @Override
    public boolean getBoolean() {
        return true;
    }

    @Override
    public int getTextWidth() {
        return textWidth;
    }

    public boolean isIconAvailable() {
        return iconAvailable;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        if (width == 0)
            return;

        if (crosshair) RenderSystem.blendFuncSeparate(
            GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        context.getMatrices().push();
        context.getMatrices().translate(piece.x+shiftX, piece.y+shiftY-yOffset-2, 0);
        rotate(context.getMatrices(), width, height);
        context.drawTexture(texture, 0, 0, width, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        context.getMatrices().pop();

        RenderSystem.defaultBlendFunc();
    }


}
