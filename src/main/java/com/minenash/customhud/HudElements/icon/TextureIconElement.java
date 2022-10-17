package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.Flags;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
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
    private boolean avaliable;
    private int textureWidth;
    private int textureHeight;
    private int width;
    private int height;
    private int textWidth;

    public TextureIconElement(Identifier texture, Flags flags) {
        this.texture = texture;
        init(flags);
    }

    private void init(Flags flags) {
        NativeImage img = null;
        try {
            Optional<Resource> resource = client.getResourceManager().getResource(texture);
            if (resource.isPresent())
                img = NativeImage.read(resource.get().getInputStream());
        }
        catch (IOException e) { e.printStackTrace(); }

        textWidth = flags.iconWidth;
        if (img != null) {
            width = (int) (11 * flags.scale);
            height = (int) (11 * flags.scale);
            textureWidth = img.getWidth();
            textureHeight = img.getHeight();
            avaliable = true;
            return;
        }

        width = height = 11;
        textureWidth = textureHeight = 16;
        avaliable = false;

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
    public int getTextureWidth() {
        return textWidth;
    }

    @Override
    public int render(MatrixStack matrix, int x, int y) {
        int displayWidth = width * width / height;
        if (displayWidth == 0)
            return 0;

        RenderSystem.setShaderTexture(0, avaliable ? texture : TEXTURE_NOT_FOUND);
        DrawableHelper.drawTexture(matrix, x, y, displayWidth, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
        return displayWidth;
    }


}
