package com.minenash.customhud;

import com.minenash.customhud.HudElements.HudElement;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomHudRenderer {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("([^{}&]*)(&\\{(#|0x)?([0-9a-fA-F]*)})?");
    private static final Pattern CONTAINS_HEX_COLOR_PATTERN = Pattern.compile(".*&\\{(#|0x)?[0-9a-fA-F]*}.*");


    public static void render(MatrixStack matrix) {

        Profile profile = CustomHud.getActiveProfile();
        if (profile == null)
            return;

        matrix.push();

        if (profile.scale != 1.0)
            matrix.scale(profile.scale,profile.scale,0);

        for (int i = 0; i < 4; i++) {
            List<List<HudElement>> section = profile.sections[i];
            if (section == null)
                continue;

            int y = (i == 0 || i == 1 ? 3 : client.getWindow().getScaledHeight() - 3 - section.size()*(9 + profile.lineSpacing)) + profile.offsets[i][1];

            for(List<HudElement> elements : section) {
                StringBuilder builder = new StringBuilder();
                for (HudElement e : elements)
                    builder.append(e.getString());

                String line = builder.toString().replaceAll("\\s+$", "");
                if (line.isEmpty() && !elements.isEmpty())
                    continue;
                if (!line.isEmpty()) {

                    boolean left = i == 0 || i == 2;

                    if (!CONTAINS_HEX_COLOR_PATTERN.matcher(line).matches()) {
                        int width = client.textRenderer.getWidth(line);
                        int x = (left ? 5 : client.getWindow().getScaledWidth() - 3 - width) + profile.offsets[i][0];
                        DrawableHelper.fill(matrix, x - 2, y, x + width + (left ? 0 : 1), y + 9 + profile.lineSpacing, profile.bgColor);
                        client.textRenderer.drawWithShadow(matrix, line, x, y + (profile.lineSpacing/2) + 1, profile.fgColor);
                    }
                    else {
                        List<Map.Entry<String,Integer>> parts = new ArrayList<>();
                        Matcher matcher = HEX_COLOR_PATTERN.matcher(line);
                        int color = profile.fgColor;
                        while (matcher.find()) {
                            parts.add(new AbstractMap.SimpleEntry<>(matcher.group(1), color));
                            if (matcher.group(4) != null)
                                color = Profile.parseHexNumber(matcher.group(4));
                        }

                        int totalWidth = parts.stream().map(e -> client.textRenderer.getWidth(e.getKey())).mapToInt(Integer::intValue).sum();
                        int baseX = (left ? 5 : client.getWindow().getScaledWidth() - 3 - totalWidth) + profile.offsets[i][0];
                        DrawableHelper.fill(matrix, baseX - 2, y, baseX + totalWidth + (left ? 0 : 1), y + 9 + profile.lineSpacing, profile.bgColor);

                        int xOffset = 0;
                        for (Map.Entry<String,Integer> part : parts) {
                            client.textRenderer.drawWithShadow(matrix, part.getKey(), baseX + xOffset, y + (profile.lineSpacing/2) + 1, part.getValue());
                            xOffset += client.textRenderer.getWidth(part.getKey());
                        }
                    }
                }
                y += 9 + profile.lineSpacing;

            }
        }
        matrix.pop();
    }


}
