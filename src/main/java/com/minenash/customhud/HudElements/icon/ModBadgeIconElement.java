package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import com.terraformersmc.modmenu.util.DrawingUtil;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix3x2fStack;

import java.util.UUID;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ModBadgeIconElement extends IconElement{

    public ModBadgeIconElement(UUID providerID, Flags flags) {
        super(flags, -1);
        this.providerID = providerID;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();

        Mod.Badge badge = (Mod.Badge) piece.value;
        int width = CLIENT.textRenderer.getWidth(badge.getText()) + 6;

        matrices.translate(piece.x + shiftX, piece.y + shiftY - 1);
        if (!referenceCorner)
            matrices.translate(0, -(9*scale-9)/2);
        matrices.scale(scale, scale);
        rotate(matrices, width+1, 9);


        DrawingUtil.drawBadge(context, 0, 0, width, badge.getText().asOrderedText(),badge.getOutlineColor(), badge.getFillColor(), piece.color);

        matrices.popMatrix();
    }

    @Override
    public int getTextWidth() {
        return width >= 0 ? width : MathHelper.ceil(scale*(CLIENT.textRenderer.getWidth( ((Mod.Badge)ListManager.getValue(providerID)).getText() ) + 6 + 1));
    }
}
