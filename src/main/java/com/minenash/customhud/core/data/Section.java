package com.minenash.customhud.core.data;

import com.minenash.customhud.core.elements.HudElement;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public abstract class Section {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public int xOffset = 0;
    public int yOffset = 0;
    public int width = -1;
    public float scale = 1;
    public boolean hideOnChat = false;

    public List<HudElement> elements = new ArrayList<>();

    public abstract int getStartX(int right, int lineWidth);
    public abstract int getStartY(HudTheme theme, int lines, int screenHeight);

    public static class TopLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return 3 + yOffset;
        }
    }

    public static class TopCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return 3 + yOffset;
        }
    }

    public static class TopRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return 3 + yOffset;
        }
    }



    public static class CenterLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }

    public static class CenterCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }

    public static class CenterRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale))/2 - (lines * (9 + theme.lineSpacing))/2 + yOffset;
        }
    }



    public static class BottomLeft extends Section {
        public int getStartX(int right, int lineWidth) {
            return 5 + xOffset;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }

    public static class BottomCenter extends Section {
        public int getStartX(int right, int lineWidth) {
            return right/2 - lineWidth/2;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }

    public static class BottomRight extends Section {
        public int getStartX(int right, int lineWidth) {
            return right - lineWidth;
        }
        public int getStartY(HudTheme theme, int lines, int screenHeight) {
            return (int) (screenHeight * (1 / scale)) - 6 - lines * (9 + theme.lineSpacing) + yOffset;
        }
    }

    @SuppressWarnings("DataFlowIssue")
    public static Section parse(Matcher matchedLine, float scale) {
        Section section = switch (matchedLine.group(1)) {
            case "topleft" -> new Section.TopLeft();
            case "topcenter" -> new Section.TopCenter();
            case "topright" -> new Section.TopRight();

            case "centerleft" -> new Section.CenterLeft();
            case "centercenter" -> new Section.CenterCenter();
            case "centerright" -> new Section.CenterRight();

            case "bottomleft" -> new Section.BottomLeft();
            case "bottomcenter" -> new Section.BottomCenter();
            case "bottomright" -> new Section.BottomRight();
            default -> null; // Can't Happen unless I break the regex pattern
        };
        section.xOffset = matchedLine.group(2) != null ? Integer.parseInt(matchedLine.group(2)) : 0;
        section.yOffset = matchedLine.group(3) != null ? Integer.parseInt(matchedLine.group(3)) : 0;
        section.width   = matchedLine.group(5) != null ? Integer.parseInt(matchedLine.group(5)) : -1;
        section.hideOnChat = matchedLine.group(4) != null && Boolean.parseBoolean(matchedLine.group(4));

        section.scale = scale;

        return section;
    }


}
