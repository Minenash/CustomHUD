package com.minenash.customhud.data;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.conditionals.ConditionalParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    public static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?section: ?(topleft|topright|bottomleft|bottomright) ?(?:, ?([-+]?\\d+))? ?(?:, ?([-+]?\\d+))? ?(?:, ?(true|false))? ?(?:, ?(\\d+))? ?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?targetrange: ?(\\d+|max) ?==");
    private static final Pattern CROSSHAIR_PATTERN = Pattern.compile("== ?crosshair: ?(normal|debug) ?==");
    private static final Pattern GLOBAL_THEME_PATTERN = Pattern.compile("== ?(.+) ?==");
    private static final Pattern LOCAL_THEME_PATTERN = Pattern.compile("= ?(.+) ?=");

    private static final Pattern IF_PATTERN = Pattern.compile("=if ?: ?(.+)=");
    private static final Pattern ELSEIF_PATTERN = Pattern.compile("=elseif ?: ?(.+)=");

    public ComplexData.Enabled enabled = new ComplexData.Enabled();

    public Section[] sections = new Section[4];

    public HudTheme baseTheme = HudTheme.defaults();
    public float targetDistance = 20;
    public boolean debugCrosshair = false;


    private Stack<ConditionalElement.MultiLineBuilder> tempIfStack = new Stack<>();

    public static Profile parseProfile(Path path, int profileID) {
        Profile profile = parseProfileInner(path, profileID);

        if (!Errors.getErrors(profileID).isEmpty()) {
            System.out.println("\n");
            System.out.println("Errors Found in profile " + profileID);
            for (var e : Errors.getErrors(profileID))
                System.out.println(e.line() + " | " + e.type() + " | " + e.source() + " | " + e.context());
            System.out.println();
        }

        return profile;
    }

    private static Profile parseProfileInner(Path path, int profileID) {
        Errors.clearErrors(profileID);

        List<String> lines;

        try {
            if(!Files.exists(path.getParent()))
                Files.createDirectory(path.getParent());
            if (!Files.exists(path))
                Files.createFile(path);
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            Errors.addError(profileID, "N/A", path.relativize(FabricLoader.getInstance().getGameDir().getParent()).toString(), ErrorType.IO, e.getMessage());
            return null;
        }

        Profile profile = new Profile();

        int sectionId = -1;
        HudTheme localTheme = profile.baseTheme.copy();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("&([0-9a-fk-or])", "§$1");
            String lineLC = line.toLowerCase();
            if (line.startsWith("//") || (sectionId == -1 && line.trim().isEmpty()))
                continue;
            if (sectionId == -1) {
                Matcher matcher = TARGET_RANGE_FLAG_PATTERN.matcher(lineLC);
                if (matcher.matches()) {
                    profile.targetDistance = matcher.group(1).equals("max") ? 725 : Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = CROSSHAIR_PATTERN.matcher(lineLC);
                if (matcher.matches()) {
                    profile.debugCrosshair = matcher.group(1).equals("Debug");
                    continue;
                }

                matcher = GLOBAL_THEME_PATTERN.matcher(lineLC);
                if (matcher.matches() && profile.baseTheme.parse(true, matcher.group(1)))
                    continue;

            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(lineLC);
            if (matcher.matches()) {
                localTheme = profile.baseTheme.copy();
                switch (matcher.group(1)) {
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
            if (sectionId == -1)
                profile.sections[(sectionId = 0)] = new Section.TopLeft();

            if (( matcher = IF_PATTERN.matcher(lineLC) ).matches())
                profile.tempIfStack.push(new ConditionalElement.MultiLineBuilder( ConditionalParser.parseConditional(matcher.group(1), line, profileID, i+1, profile.enabled) ));

            else if (( matcher = ELSEIF_PATTERN.matcher(lineLC) ).matches())
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i+1, line, ErrorType.CONDITIONAL_NOT_STARTED, "=else if: §ocond§r=");
                else
                    profile.tempIfStack.peek().setConditional(ConditionalParser.parseConditional(matcher.group(1), line, profileID, i + 1, profile.enabled));

            else if (line.equalsIgnoreCase("=else="))
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i+1, line, ErrorType.CONDITIONAL_NOT_STARTED, "=else=");
                else
                    profile.tempIfStack.peek().setConditional(new Operation.Literal(1));

            else if (line.equalsIgnoreCase("=endif="))
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i+1, line, ErrorType.CONDITIONAL_NOT_STARTED, "end");
                else
                    addElement(profile, sectionId, profile.tempIfStack.pop().build());

            else if (( matcher = LOCAL_THEME_PATTERN.matcher(lineLC) ).matches()) {
                if (localTheme.parse(false, matcher.group(1)))
                    addElement(profile, sectionId, new FunctionalElement.ChangeTheme(localTheme));
                else
                    Errors.addError(profileID, i+1, line, ErrorType.UNKNOWN_THEME_FLAG, "");
            }

            else if (GLOBAL_THEME_PATTERN.matcher(lineLC).matches() )
                Errors.addError(profileID, i+1, line, ErrorType.ILLEGAL_GLOBAL_THEME_FLAG, "");

            else
                addAllElement(profile, sectionId, VariableParser.addElements(line, profileID, i + 1, profile.enabled, true));

        }

        while (!profile.tempIfStack.empty())
            addElement(profile, sectionId, profile.tempIfStack.pop().build());

        profile.tempIfStack = null;

        for (int i = 0; i < 4; i++) {
            if (profile.sections[i] != null && profile.sections[i].elements.isEmpty())
                profile.sections[i] = null;
        }

        return profile;
    }

    private static void addElement(Profile profile, int sectionId, HudElement element) {
        if (profile.tempIfStack.empty())
            profile.sections[sectionId].elements.add(element);
        else
            profile.tempIfStack.peek().add(element);
    }

    private static void addAllElement(Profile profile, int sectionId, List<HudElement> elements) {
        if (profile.tempIfStack.empty())
            profile.sections[sectionId].elements.addAll(elements);
        else
            profile.tempIfStack.peek().addAll(elements);
    }

}