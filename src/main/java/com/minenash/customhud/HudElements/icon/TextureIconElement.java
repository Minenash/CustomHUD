package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;

public class TextureIconElement extends IconElement {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Identifier TEXTURE_NOT_FOUND = new Identifier("textures/item/barrier.png");

    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;
    private final int regionWidth;
    private final int regionHeight;
    private final int width;
    private final int height;
    private final int xOffset;
    private final int yOffset;
    private final int textWidth;

    private final boolean iconAvailable;


    public TextureIconElement(Identifier texture, int u, int v, int w, int h, Flags flags) {
        super(flags);
        this.u = u;
        this.v = v;

        NativeImage img = null;
        try {
            Optional<Resource> resource = client.getResourceManager().getResource(texture);
            if (resource.isPresent())
                img = NativeImage.read(resource.get().getInputStream());
        }
        catch (IOException e) { e.printStackTrace(); }


        iconAvailable = img != null;
        this.texture = iconAvailable ? texture : TEXTURE_NOT_FOUND;

        textureWidth = iconAvailable ? img.getWidth() : 16;
        textureHeight = iconAvailable ? img.getHeight() : 16;
        regionWidth = w != -1 ? w : textureWidth;
        regionHeight = h != -1 ? h : textureHeight;

        height = (int) (11 * flags.scale);
        width = (int) (height * ((float)textureWidth/textureHeight));
        xOffset = referenceCorner ? 0 : (int) ((width*scale-width)/(scale*2));
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
    public void render(DrawContext context, int x, int y, float profileScale) {
        if (width == 0)
            return;
        context.getMatrices().multiply(rotation);
        context.drawTexture(texture, x+shiftX+xOffset, y+shiftY-yOffset, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }


}
