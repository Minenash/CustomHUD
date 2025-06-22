package com.minenash.customhud.HudElements.icon;

import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.minenash.customhud.CustomHud;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix3x2fStack;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import static com.minenash.customhud.CustomHud.CLIENT;

public class PackIconElement extends IconElement {

    private final Map<String, Identifier> iconTextures = Maps.newHashMap();
    public PackIconElement(UUID providerID, Flags flags) {
        super(flags, 11);
        this.providerID = providerID;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        ResourcePackProfile pack = (ResourcePackProfile) piece.value;
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(piece.x + shiftX, piece.y + shiftY - 2);
        if (!referenceCorner)
            matrices.translate(0, -(11*scale-11)/2F);
//        matrices.scale(scale, scale, 0);
        int width = (int) (11*scale);
        rotate(matrices, width, width);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, getPackIconTexture(pack), 0, 0, 0, 0, width, width, width, width);
        matrices.popMatrix();
    }

    private Identifier getPackIconTexture(ResourcePackProfile resourcePackProfile) {
        return this.iconTextures.computeIfAbsent(resourcePackProfile.getId(), (profileName) -> loadPackIcon(CLIENT.getTextureManager(), resourcePackProfile));
    }

    private static final Identifier UNKNOWN_PACK = Identifier.of("textures/misc/unknown_pack.png");
    public static Identifier loadPackIcon(TextureManager textureManager, ResourcePackProfile resourcePackProfile) {
        try (ResourcePack resourcePack = resourcePackProfile.createResourcePack()) {
            InputSupplier<InputStream> inputSupplier = resourcePack.openRoot("pack.png");
            if (inputSupplier == null)
                return UNKNOWN_PACK;

            String name = resourcePackProfile.getId();
            String safeName = Util.replaceInvalidChars(name, Identifier::isPathCharacterValid);
            Identifier identifier = Identifier.of("minecraft", "pack/" + safeName + "/" + Hashing.sha1().hashUnencodedChars(name) + "/icon");

            try (InputStream inputStream = inputSupplier.get()) {
                NativeImage nativeImage = NativeImage.read(inputStream);
                textureManager.registerTexture(identifier, new NativeImageBackedTexture(identifier::toString, nativeImage));
                return identifier;
            }
        } catch (Exception var14) {
            CustomHud.LOGGER.warn("[CustomHud] Failed to load icon from pack {}", resourcePackProfile.getId(), var14);
            return UNKNOWN_PACK;
        }
    }

}
