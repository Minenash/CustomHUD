package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.util.Pair;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class BossbarIcon extends IconElement{

    public static class BasicBar extends BossBar {
        public BasicBar(Color color, Style style) {
            super(UUID.randomUUID(), null, color, style);
        }
    }

    private final Supplier<BossBar> bossbarSupplier;
    private final boolean useSupplier;
    private List<BossBar> bossbars;
    private int bossbarsIndex = 0;

    public BossbarIcon(Supplier<BossBar> supplier, Flags flags) {
        super(flags, 182);
        this.bossbarSupplier = supplier;
        this.useSupplier = supplier != ListManager.SUPPLIER;
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.translate(x + shiftX, y + shiftY + 1, 0);
        if (!referenceCorner)
            matrices.translate(0, -(5*scale-5)/2, 0);
        matrices.scale(scale, scale, 0);
        rotate(matrices, 182, 5);

        BossBar bossBar = useSupplier ? bossbarSupplier.get() : bossbars.get(bossbarsIndex++);
        if (bossBar != null)
            CLIENT.inGameHud.getBossBarHud().renderBossBar(context, 0, 0, bossBar);

        matrices.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        bossbars = (List<BossBar>) values;
        bossbarsIndex = 0;
    }

}
