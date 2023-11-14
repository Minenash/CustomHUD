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

    public static Pair<Color, Style> getSettings(String settings) {
        Color color = Color.WHITE;
        Style style = Style.PROGRESS;
        settings.charAt(settings.length()-1);


        switch (settings) {
            case "pink" -> color = Color.PINK;
            case "blue" -> color = Color.BLUE;
            case "red" -> color = Color.RED;
            case "green" -> color = Color.GREEN;
            case "yellow" -> color = Color.YELLOW;
            case "purple" -> color = Color.PURPLE;
            case "white" -> color = Color.WHITE;
            case "6" -> style = Style.NOTCHED_6;
            case "10" -> style = Style.NOTCHED_10;
            case "12" -> style = Style.NOTCHED_12;
            case "20" -> style = Style.NOTCHED_20;

            case "pink6" -> {color = Color.PINK; style = Style.NOTCHED_6;}
            case "blue6" -> {color = Color.BLUE; style = Style.NOTCHED_6;}
            case "red6" -> {color = Color.RED; style = Style.NOTCHED_6;}
            case "green6" -> {color = Color.GREEN; style = Style.NOTCHED_6;}
            case "yellow6" -> {color = Color.YELLOW; style = Style.NOTCHED_6;}
            case "purple6" -> {color = Color.PURPLE; style = Style.NOTCHED_6;}
            case "white6" -> {color = Color.WHITE; style = Style.NOTCHED_6;}

            case "pink10" -> {color = Color.PINK; style = Style.NOTCHED_10;}
            case "blue10" -> {color = Color.BLUE; style = Style.NOTCHED_10;}
            case "red10" -> {color = Color.RED; style = Style.NOTCHED_10;}
            case "green10" -> {color = Color.GREEN; style = Style.NOTCHED_10;}
            case "yellow10" -> {color = Color.YELLOW; style = Style.NOTCHED_10;}
            case "purple10" -> {color = Color.PURPLE; style = Style.NOTCHED_10;}
            case "white10" -> {color = Color.WHITE; style = Style.NOTCHED_10;}

            case "pink12" -> {color = Color.PINK; style = Style.NOTCHED_12;}
            case "blue12" -> {color = Color.BLUE; style = Style.NOTCHED_12;}
            case "red12" -> {color = Color.RED; style = Style.NOTCHED_12;}
            case "green12" -> {color = Color.GREEN; style = Style.NOTCHED_12;}
            case "yellow12" -> {color = Color.YELLOW; style = Style.NOTCHED_12;}
            case "purple12" -> {color = Color.PURPLE; style = Style.NOTCHED_12;}
            case "white12" -> {color = Color.WHITE; style = Style.NOTCHED_12;}

            case "pink20" -> {color = Color.PINK; style = Style.NOTCHED_20;}
            case "blue20" -> {color = Color.BLUE; style = Style.NOTCHED_20;}
            case "red20" -> {color = Color.RED; style = Style.NOTCHED_20;}
            case "green20" -> {color = Color.GREEN; style = Style.NOTCHED_20;}
            case "yellow20" -> {color = Color.YELLOW; style = Style.NOTCHED_20;}
            case "purple20" -> {color = Color.PURPLE; style = Style.NOTCHED_20;}
            case "white20" -> {color = Color.WHITE; style = Style.NOTCHED_20;}
        }
        return new Pair<>(color, style);
    }
}
