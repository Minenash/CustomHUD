package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.terraformersmc.modmenu.util.mod.Mod;
import com.terraformersmc.modmenu.util.mod.fabric.FabricIconHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ModIconElement extends IconElement {

    private static Set<Identifier> cached = new HashSet<>();
    private static FabricIconHandler handler = new FabricIconHandler();

    public ModIconElement(UUID providerID, Flags flags) {
        super(flags, 11);
        this.providerID = providerID;
        cached.clear();
        handler.close();
        handler = new FabricIconHandler();
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        Mod mod = (Mod) piece.value;
        int size = (int)(10 * scale * CLIENT.options.getGuiScale().getValue());

        Identifier id = Identifier.of("custom_hud", size + "___" + mod.getId());

        if (!cached.contains(id)) {
            try {
                NativeImageBackedTexture icon = mod.getIcon(handler, size);
                CLIENT.getTextureManager().registerTexture(id, icon);
                cached.add(id);
            }
            catch (Exception e) {
                id = Identifier.of("textures/misc/unknown_pack.png");
            }
        }

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(piece.x + shiftX, piece.y + shiftY - 2);
        if (!referenceCorner)
            matrices.translate(0, -(11*scale-11)/2F);
//        matrices.scale(scale, scale, 0);
        int w = (int) (11 * scale);
        rotate(matrices, w, w);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, id, 0, 0, 0, 0, w, w, w, w);
        matrices.popMatrix();
    }

}
