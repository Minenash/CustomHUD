package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ProgressBarIcon extends IconElement {

    private final Operation numerator;
    private final Operation denominator;
    private final BarStyle style;



    public ProgressBarIcon(Operation numerator, Operation denominator, BarStyle style, Flags flags) {
        super(flags, 182);
        this.numerator = numerator;
        this.denominator = denominator;
        this.style = style == null ? DEFAULT : style;

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

        style.render(context, (float) MathHelper.clamp(numerator.getValue() / denominator.getValue(), 0, 1));

        matrices.pop();
    }

    public static BarStyle getStyle(String settings) {
        switch (settings) {
            case "experience", "xp": return XP;
            case "jump", "horse": return JUMP;
        }

        Color color = null;
        Style style = null;

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
        if (color == null && style == null)
            return null;
        return new BossBarStyle(color == null ? Color.WHITE : color, style == null ? Style.PROGRESS : style);


    }

    public static BarStyle DEFAULT = new BossBarStyle(Color.WHITE, Style.PROGRESS);
    public static BarStyle XP = new TextureStyle(new Identifier("hud/experience_bar_progress"), new Identifier("hud/experience_bar_background"));
    public static BarStyle JUMP = new TextureStyle(new Identifier("hud/jump_bar_progress"), new Identifier("hud/jump_bar_background"));
    public interface BarStyle {
        void render(DrawContext context, float progress);
    }


    public static class BossBarStyle implements BarStyle {
        private final BossBar bossBar;
        public BossBarStyle(Color color, Style style) {this.bossBar = new BossbarIcon.BasicBar(color, style);}
        public void render(DrawContext context, float progress) {
            bossBar.setPercent(progress);
            CLIENT.inGameHud.getBossBarHud().renderBossBar(context, 0, 0, bossBar);
        }
    }

    public static class TextureStyle implements BarStyle {
        private final Identifier fg;
        private final Identifier bg;
        public TextureStyle(Identifier fg, Identifier bg) {this.fg = fg; this.bg = bg;}
        public void render(DrawContext context, float progress) {
            context.drawGuiTexture(bg, 0, 0, 182, 5);
            if (progress > 0)
                context.drawGuiTexture(fg, 182, 5, 0, 0, 0, 0, (int)(progress*182), 5);

        }
    }

}
