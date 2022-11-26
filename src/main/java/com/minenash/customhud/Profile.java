package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.HudElement;
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

    private static final Pattern IF_PATTERN = Pattern.compile("=if ?, ?(.+)=");
    private static final Pattern ELSEIF_PATTERN = Pattern.compile("=elseif ?, ?(.+)=");

    public List<List<HudElement>>[] sections = new List[4];
    public ComplexData.Enabled enabled = new ComplexData.Enabled();
    public int[][] offsets = new int[4][2];
    public int[] width = new int[]{-1,-1,-1,-1};
    public boolean[] hideOnChat = new boolean[4];

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

        for (int i = 0; i < 4; i++) {
            profile.sections[i] = new ArrayList<>();
            profile.offsets[i] = new int[2];
        }

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
                profile.offsets[sectionId][0] = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                profile.offsets[sectionId][1] = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                profile.width[sectionId]      = matcher.group(5) != null ? Integer.parseInt(matcher.group(5)) : -1;
                profile.hideOnChat[sectionId] = matcher.group(4) != null && Boolean.parseBoolean(matcher.group(4));

                continue;
            }
            if (sectionId == -1) {
                sectionId = 0;
            }

            matcher = LOCAL_THEME_PATTERN.matcher(line);
            if (matcher.matches()) {
                if ( localTheme.parse(matcher.group(1)) ) {
                    List<HudElement> cte = Collections.singletonList(new FunctionalElement.ChangeTheme(localTheme));

                    int lastElement = profile.sections[sectionId].size()-1;
                    if (lastElement == -1)
                        profile.sections[sectionId].add(cte);
                    List<HudElement> elements = profile.sections[sectionId].get(lastElement);
                    if (!elements.isEmpty() && elements.get(0) instanceof FunctionalElement.ChangeTheme)
                        profile.sections[sectionId].set(lastElement, cte);
                    else
                        profile.sections[sectionId].add(cte);
                    continue;
                }
            }

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
                List<HudElement> c = Collections.singletonList(profile.tempIfStack.pop().build());
                if (profile.tempIfStack.empty())
                    profile.sections[sectionId].add(c);
                else
                    profile.tempIfStack.peek().add(c);
                continue;
            }

            if (profile.tempIfStack.empty())
                profile.sections[sectionId].add(VariableParser.parseElements(line, i + 1, profile.enabled));
            else
                profile.tempIfStack.peek().add(VariableParser.parseElements(line, i + 1, profile.enabled));
        }

        while (!profile.tempIfStack.empty()) {
            List<HudElement> c = Collections.singletonList(profile.tempIfStack.pop().build());
            if (profile.tempIfStack.empty())
                profile.sections[sectionId].add(c);
            else
                profile.tempIfStack.peek().add(c);
        }
        profile.tempIfStack = null;

        for (int i = 0; i < 4; i++) {
            if (profile.sections[i].isEmpty())
                profile.sections[i] = null;
        }

        return profile;
    }

}