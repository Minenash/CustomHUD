package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.HudElements.stats.CustomStatElement;
import com.minenash.customhud.HudElements.stats.TypedStatElement;
import com.minenash.customhud.mod_compat.CustomHudRegistry;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    private static final Pattern LINE_PARING_PATTERN = Pattern.compile("([^{}&]*)(\\{\\{.*?}}|&?\\{.*?})?");
    private static final Pattern CONDITIONAL_PARSING_PATTERN = Pattern.compile("(.*?), ?\"(.*?)\"(, ?\"(.*?)\")?");
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

            profile.sections[sectionId].add(parseElements(line, i + 1,profile.enabled));
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

    public static List<HudElement> parseElements(String str, int debugLine, ComplexData.Enabled enabled) {
        List<String> parts = new ArrayList<>();

        Matcher matcher = LINE_PARING_PATTERN.matcher(str == null ? "" : str);
        while (matcher.find()) {
            parts.add(matcher.group(1));
            parts.add(matcher.group(2));
        }

        List<HudElement> elements = new ArrayList<>();

        for (String part : parts) {
            if (part == null || part.isEmpty())
                continue;

            if (!part.startsWith("{"))
                elements.add(new StringElement(part));

            else if (part.startsWith("{real_time:"))
                elements.add(new RealTimeElement(new SimpleDateFormat(part.substring(11,part.length()-1))));

            else if (part.startsWith("{stat:")) {
                String[] iparts = part.substring(1, part.length()-1).split(" ");
                String stat = iparts[0].substring(5);
                Flags flags = VariableParser.getFlags(iparts);

                if ( stat("mined:",   Stats.MINED,   Registry.BLOCK, stat, flags, elements, enabled, debugLine) ||
                     stat("crafted:", Stats.CRAFTED, Registry.ITEM,  stat, flags, elements, enabled, debugLine) ||
                     stat("used:",    Stats.USED,    Registry.ITEM,  stat, flags, elements, enabled, debugLine) ||
                     stat("broken:",  Stats.BROKEN,  Registry.ITEM,  stat, flags, elements, enabled, debugLine) ||
                     stat("dropped:", Stats.DROPPED, Registry.ITEM,  stat, flags, elements, enabled, debugLine) ||
                     stat("picked_up:", Stats.PICKED_UP, Registry.ITEM, stat, flags, elements, enabled, debugLine) ||
                     stat("killed:",    Stats.KILLED,    Registry.ENTITY_TYPE, stat, flags, elements, enabled, debugLine) ||
                     stat("killed_by:", Stats.KILLED_BY, Registry.ENTITY_TYPE, stat, flags, elements, enabled, debugLine)
                   )
                    continue;

                Identifier statId = Registry.CUSTOM_STAT.get(new Identifier(stat));
                if (Stats.CUSTOM.hasStat(statId)) {
                    elements.add(new CustomStatElement(Stats.CUSTOM.getOrCreateStat(statId), flags));
                    enabled.updateStats = true;
                }
                else
                    System.out.println("Unknown stat " + stat + " on line " + debugLine);
            }

            else if (part.startsWith("{{")) {
                Matcher args = CONDITIONAL_PARSING_PATTERN.matcher(part.substring(2,part.length()-2));
                if (!args.matches()) {
                    CustomHud.LOGGER.warn("Malformed conditional " + part + " on line " + debugLine);
                    continue;
                }
                HudElement conditional = VariableParser.getSupplierElement(args.group(1),enabled);
                if (conditional == null) {
                    CustomHud.LOGGER.warn("[Cond] Unknown Variable " + args.group(1) + " on line " + debugLine);
                    continue;
                }
                List<HudElement> positive = parseElements(args.group(2), debugLine,enabled);
                List<HudElement> negative = args.groupCount() > 2 ? parseElements(args.group(4), debugLine,enabled) : new ArrayList<>();
                elements.add(new ConditionalElement(conditional, positive, negative));
            }

            else {
                HudElement element = VariableParser.getSupplierElement(part.substring(1, part.length() - 1), enabled);
                if (element != null)
                    elements.add(element);
                else {
                    Matcher keyMatcher = registryKey.matcher(part);
                    if (keyMatcher.matches()) {
                        element = CustomHudRegistry.get(keyMatcher.group(1), part);

                        if (element != null)
                            elements.add(element);
                        else
                            CustomHud.LOGGER.warn("[I] Unknown Variable " + part + " on line " + debugLine);
                    }
                    else
                        CustomHud.LOGGER.warn("[O] Unknown Variable " + part + " on line " + debugLine);
                }
            }
        }

        return elements;
    }

    private static final Pattern registryKey = Pattern.compile("\\{(\\w+).*}");

    private static boolean stat(String prefix, StatType type, Registry registry, String stat, Flags flags, List<HudElement> elements, ComplexData.Enabled enabled, int debugLine) {
        if (!stat.startsWith(prefix))
            return false;

        Optional<?> entry = registry.getOrEmpty( new Identifier(stat.substring(prefix.length())) );
        if (entry.isPresent()) {
            elements.add(new TypedStatElement(type, entry.get(), flags));
            enabled.updateStats = true;
        }
        else
            System.out.println("Unknown value " + stat.substring(prefix.length()) + " on line " + debugLine);

        return true;
    }

}