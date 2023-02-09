package com.minenash.customhud.data;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.conditionals.ConditionalParser;
import com.minenash.customhud.conditionals.Operation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    private static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?Section: ?(TopLeft|TopRight|BottomLeft|BottomRight) ?(?:, ?([-+]?\\d+))? ?(?:, ?([-+]?\\d+))? ?(?:, ?(true|false))? ?(?:, ?(\\d+))? ?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?TargetRange: ?(\\d+|max) ?==");
    private static final Pattern GLOBAL_THEME_PATTERN = Pattern.compile("== ?(.+) ?==");
    private static final Pattern LOCAL_THEME_PATTERN = Pattern.compile("= ?(.+) ?=");

    private static final Pattern IF_PATTERN = Pattern.compile("=if ?: ?(.+)=");
    private static final Pattern ELSEIF_PATTERN = Pattern.compile("=elseif ?: ?(.+)=");

    public ComplexData.Enabled enabled = new ComplexData.Enabled();

    public Section[] sections = new Section[4];


    public HudTheme baseTheme = HudTheme.defaults();
    public float targetDistance = 20;


    private Stack<ConditionalElement.MultiLineBuilder> tempIfStack = new Stack<>();

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

        int sectionId = -1;
        HudTheme localTheme = profile.baseTheme.copy();

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
                matcher = GLOBAL_THEME_PATTERN.matcher(line);
                if (matcher.matches() && profile.baseTheme.parse(matcher.group(1)))
                    continue;

            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(line);
            if (matcher.matches()) {
                localTheme = profile.baseTheme.copy();
                switch (matcher.group(1).toLowerCase()) {
                    case "topleft" -> sectionId = 0;
                    case "topright" -> sectionId = 1;
                    case "bottomleft" -> sectionId = 2;
                    case "bottomright" -> sectionId = 3;
                }
                if (profile.sections[sectionId] == null) {
                    profile.sections[sectionId] = switch (sectionId) {
                        case 0 -> new Section.TopLeft();
                        case 1 -> new Section.TopRight();
                        case 2 -> new Section.BottomLeft();
                        default -> new Section.BottomRight();
                    };
                }
                profile.sections[sectionId].xOffset = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                profile.sections[sectionId].yOffset = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                profile.sections[sectionId].width   = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : -1;
                profile.sections[sectionId].hideOnChat = matcher.group(4) != null && Boolean.parseBoolean(matcher.group(4));

                continue;
            }
            if (sectionId == -1) {
                profile.sections[(sectionId = 0)] = new Section.TopLeft();
            }

            //TODO: Look
//            matcher = LOCAL_THEME_PATTERN.matcher(line);
//            if (matcher.matches()) {
//                if ( localTheme.parse(matcher.group(1)) ) {
//                    HudElement cte = new FunctionalElement.ChangeTheme(localTheme);
//
//                    int lastElement = profile.sections[sectionId].size()-1;
//                    if (lastElement == -1)
//                        profile.sections[sectionId].add(cte);
//                    List<HudElement> elements = profile.sections[sectionId].get(lastElement);
//                    if (!elements.isEmpty() && elements.get(0) instanceof FunctionalElement.ChangeTheme)
//                        profile.sections[sectionId].set(lastElement, cte);
//                    else
//                        profile.sections[sectionId].add(cte);
//                    continue;
//                }
//            }

            matcher = IF_PATTERN.matcher(line);
            if (matcher.matches()) {
                profile.tempIfStack.push(new ConditionalElement.MultiLineBuilder());
                profile.tempIfStack.peek().setConditional(ConditionalParser.parseConditional(matcher.group(1), i+1, profile.enabled));
                continue;
            }

            matcher = ELSEIF_PATTERN.matcher(line);
            if (matcher.matches()) {
                profile.tempIfStack.peek().setConditional(ConditionalParser.parseConditional(matcher.group(1), i+1, profile.enabled));
                continue;
            }

            if (line.equals("=else=")) {
                profile.tempIfStack.peek().setConditional(new Operation.Literal(1));
                continue;
            }
            if (line.equals("=endif=")) {
                HudElement element = profile.tempIfStack.pop().build();
                if (profile.tempIfStack.empty())
                    profile.sections[sectionId].elements.add(element);
                else
                    profile.tempIfStack.peek().add(element);
                continue;
            }

            if (profile.tempIfStack.empty())
                profile.sections[sectionId].elements.addAll(VariableParser.addElements(line, i + 1, profile.enabled, true));
            else
                profile.tempIfStack.peek().addAll(VariableParser.addElements(line, i + 1, profile.enabled, true));
        }

        while (!profile.tempIfStack.empty()) {
            HudElement element = profile.tempIfStack.pop().build();
            if (profile.tempIfStack.empty())
                profile.sections[sectionId].elements.add(element);
            else
                profile.tempIfStack.peek().add(element);
        }
        profile.tempIfStack = null;

        for (int i = 0; i < 4; i++) {
            if (profile.sections[i] != null && profile.sections[i].elements.isEmpty())
                profile.sections[i] = null;
        }

        return profile;
    }

}