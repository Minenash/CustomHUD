package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.boss.BossBar;

import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class BossbarIcon extends IconElement{

    private final int width;

    private final Supplier<BossBar> bossbarSupplier;
    private final boolean useSupplier;
    private List<BossBar> bossbars;
    private int bossbarsIndex = 0;

    public BossbarIcon(Supplier<BossBar> supplier, Flags flags) {
        super(flags);
        this.width = flags.iconWidth == -1 ? 182 : flags.iconWidth;
        this.bossbarSupplier = supplier;
        this.useSupplier = supplier != ListManager.SUPPLIER;
    }

    @Override
    public void render(DrawContext context, int x, int y, float profileScale) {
        context.getMatrices().push();
        context.getMatrices().translate(x + shiftX, y + shiftY + 1, 0);
        context.getMatrices().scale(scale, scale, 0);
        context.getMatrices().multiply(rotation); //TODO: TRANSLATE BACK

        BossBar bossBar = useSupplier ? bossbarSupplier.get() : bossbars.get(bossbarsIndex++);
        if (bossBar != null)
            CLIENT.inGameHud.getBossBarHud().renderBossBar(context, 0, 0, bossBar);

        context.getMatrices().pop();
    }

    @Override
    public int getTextWidth() {
        return width;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setList(List<?> values) {
        bossbars = (List<BossBar>) values;
        bossbarsIndex = 0;
    }
}
