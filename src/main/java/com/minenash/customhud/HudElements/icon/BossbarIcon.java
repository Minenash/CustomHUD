package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;
import org.joml.Matrix3x2fStack;

import java.util.UUID;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class BossbarIcon extends IconElement {

    public static class BasicBar extends BossBar {
        public BasicBar(Color color, Style style) {
            super(UUID.randomUUID(), null, color, style);
        }
    }

    private final Supplier<BossBar> supplier;

    public BossbarIcon(UUID providerID, Supplier<BossBar> supplier, Flags flags) {
        super(flags, 182);
        this.providerID = providerID;
        this.supplier = supplier;
    }

    @Override
    public void render(DrawContext context, RenderPiece piece) {
        BossBar bossBar = piece.value != null ? (BossBar) piece.value : supplier.get();
        if (bossBar == null)
            return;

        Matrix3x2fStack matrices = context.getMatrices();
        matrices.pushMatrix();
        matrices.translate(piece.x + shiftX, piece.y + shiftY + 1);
        if (!referenceCorner)
            matrices.translate(0, -(5*scale-5)/2);
        matrices.scale(scale, scale);
        rotate(matrices, 182, 5);

        CLIENT.inGameHud.getBossBarHud().renderBossBar(context, 0, 0, bossBar);

        matrices.popMatrix();
    }

}
