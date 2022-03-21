package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    private static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?Section: ?(TopLeft|TopRight|BottomLeft|BottomRight) ?(, ?([-+]?\\d+))? ?(, ?([-+]?\\d+))? ?(, ?(\\d+))? ?(, ?HideOnChat: ?(true|false))? ?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?TargetRange: ?(\\d+|max) ?==");
    private static final Pattern SPACING_FLAG_PATTERN = Pattern.compile("== ?LineSpacing: ?([-+]?\\d+) ?==");
    private static final Pattern SCALE_FLAG_PATTERN = Pattern.compile("== ?Scale: ?(\\d+.?\\d*|.?\\d+) ?==");
    private static final Pattern COLOR_FLAG_PATTERN = Pattern.compile("== ?(Back|Fore)groundColou?r: ?(0x|#)?([0-9a-fA-F]+) ?==");
    private static final Pattern FONT_FLAG_PATTERN = Pattern.compile("== ?Font: ?(\\w*:?\\w+) ?==");

    public List<List<HudElement>>[] sections = new List[4];
    public ComplexData.Enabled enabled = new ComplexData.Enabled();
    public int[][] offsets = new int[4][2];
    public int[] width = new int[4];
    public boolean[] hideOnChat = new boolean[4];

    public int bgColor;
    public int fgColor;
    public int lineSpacing;
    public float targetDistance;
    public float scale;
    public Identifier font;


    public static Profile parseProfile(Path path) {
        List<String> lines;

        try {
            if(!Files.exists(path.getParent()))
                Files.createDirectory(path.getParent());
            if (!Files.exists(path))
                Files.createFile(path);
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Profile profile = new Profile();

        for (int i = 0; i < 4; i++) {
            profile.sections[i] = new ArrayList<>();
            profile.offsets[i] = new int[2];
        }
        profile.bgColor = 0x44000000;
        profile.fgColor = 0xffffffff;
        profile.targetDistance = 20;
        profile.lineSpacing = 2;
        profile.scale = 1;
        profile.font = null;

        int sectionId = -1;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("&([0-9a-fk-or])", "§$1");
            if (line.startsWith("//") || (sectionId == -1 && line.trim().isEmpty()))
                continue;
            if (sectionId == -1) {
                Matcher matcher = TARGET_RANGE_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.targetDistance = matcher.group(1).equals("max") ? 725 : Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = COLOR_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    if (matcher.group(1).equals("Fore"))
                        profile.fgColor = parseHexNumber(matcher.group(3));
                    else
                        profile.bgColor = parseHexNumber(matcher.group(3));
                    continue;
                }
                matcher = SPACING_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.lineSpacing = Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = SCALE_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.scale = Float.parseFloat(matcher.group(1));
                    continue;
                }
                matcher = FONT_FLAG_PATTERN.matcher(line);
                if (matcher.matches()) {
                    profile.font = new Identifier(matcher.group(1));
                    continue;
                }
            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(line);
            if (matcher.matches()) {

                switch (matcher.group(1).toLowerCase()) {
                    case "topleft" -> sectionId = 0;
                    case "topright" -> sectionId = 1;
                    case "bottomleft" -> sectionId = 2;
                    case "bottomright" -> sectionId = 3;
                }
                profile.offsets[sectionId][0] = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                profile.offsets[sectionId][1] = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : 0;
                profile.width[sectionId]      = matcher.group(7) != null ? Integer.parseInt(matcher.group(7)) : -1;
                profile.hideOnChat[sectionId] = matcher.group(9) != null && Boolean.parseBoolean(matcher.group(9));

                continue;
            }
            if (sectionId == -1) {
                sectionId = 0;
            }

            profile.sections[sectionId].add(VariableParser.parseElements(line, i + 1,profile.enabled));
        }

        for (int i = 0; i < 4; i++) {
            if (profile.sections[i].isEmpty())
                profile.sections[i] = null;
        }
        return profile;
    }

    public static int parseHexNumber(String str) {
        long color = Long.parseLong(str,16);
        return (int) (color >= 0x100000000L ? color - 0x100000000L : color);
    }

}