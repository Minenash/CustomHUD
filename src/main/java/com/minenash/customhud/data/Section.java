package com.minenash.customhud.data;

import com.minenash.customhud.HudElements.HudElement;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Section {
    private static MinecraftClient client = MinecraftClient.getInstance();

    public int xOffset = 0;
    public int yOffset = 0;
    public int width = -1;
    public boolean hideOnChat = false;

    public List<HudElement> elements = new ArrayList<>();

    public abstract int getStartX(int right, int lineWidth);
    public abstract int getStartY(HudTheme theme, int lines);

    public static class TopLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines) {
            return 3 + yOffset;
        }
    }

    public static class TopCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines) {
            return 3 + yOffset;
        }
    }

    public static class TopRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines) {
            return 3 + yOffset;
        }
    }



    public static class CenterLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }

    public static class CenterCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }

    public static class CenterRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }



    public static class BottomLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }

    public static class BottomCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }

    public static class BottomRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines) {
            return (int) (client.getWindow().getScaledHeight() * (1 / theme.scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }


}
