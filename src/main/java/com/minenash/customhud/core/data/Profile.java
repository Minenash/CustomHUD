package com.minenash.customhud.core.data;

import com.minenash.customhud.core.elements.ConditionalElement;
import com.minenash.customhud.core.elements.FunctionalElement;
import com.minenash.customhud.core.elements.HudElement;
import com.minenash.customhud.core.parsing.VariableParser;
import com.minenash.customhud.core.parsing.ExpressionParser;
import com.minenash.customhud.core.parsing.Operation;
import com.minenash.customhud.core.errors.ErrorType;
import com.minenash.customhud.core.errors.Errors;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    public static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?section: ?(topleft|topcenter|topright|centerleft|centercenter|centerright|bottomleft|bottomcenter|bottomright) ?(?:, ?([-+]?\\d+))? ?(?:, ?([-+]?\\d+))? ?(?:, ?(true|false))? ?(?:, ?(\\d+))? ?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?targetrange: ?(\\d+|max) ?==");
    private static final Pattern CROSSHAIR_PATTERN = Pattern.compile("== ?crosshair: ?(.*) ?==");
    private static final Pattern GLOBAL_THEME_PATTERN = Pattern.compile("== ?(.+) ?==");
    private static final Pattern LOCAL_THEME_PATTERN = Pattern.compile("= ?(.+) ?=");

    private static final Pattern IF_PATTERN = Pattern.compile("=if ?: ?(.+)=");
    private static final Pattern ELSEIF_PATTERN = Pattern.compile("=elseif ?: ?(.+)=");

    public Enabled enabled = new Enabled();

    public List<Section> sections = new ArrayList<>();

    public HudTheme baseTheme = HudTheme.defaults();
    public float targetDistance = 20;
    public Crosshairs crosshair = Crosshairs.NORMAL;


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
            if (!Files.exists(path)) {
                Files.createFile(path);
                if (profileID == 1) {
                    try (OutputStream writer = Files.newOutputStream(path); InputStream input = Profile.class.getClassLoader().getResourceAsStream("assets/custom_hud/example_profile.txt")) {
                        input.transferTo(writer);
                    }
                }
            }
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            Errors.addError(profileID, "N/A", path.relativize(FabricLoader.getInstance().getGameDir().getParent()).toString(), ErrorType.IO, e.getMessage());
            return null;
        }

        Profile profile = new Profile();

        Section section = null;
        HudTheme localTheme = profile.baseTheme.copy();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("&([0-9a-fk-or])", "§$1");
            String lineLC = line.toLowerCase();
            if (line.startsWith("//"))
                continue;
            if (section == null) {
                Matcher matcher = TARGET_RANGE_FLAG_PATTERN.matcher(lineLC);
                if (matcher.matches()) {
                    profile.targetDistance = matcher.group(1).equals("max") ? 725 : Integer.parseInt(matcher.group(1));
                    continue;
                }
                matcher = CROSSHAIR_PATTERN.matcher(lineLC);
                if (matcher.matches()) {
                    profile.crosshair = Crosshairs.parse(matcher.group(1).trim());
                    if (profile.crosshair == null) {
                        profile.crosshair = Crosshairs.NORMAL;
                        Errors.addError(profileID, i, line+1, ErrorType.UNKNOWN_CROSSHAIR, matcher.group(1));
                    }
                    continue;
                }

                matcher = GLOBAL_THEME_PATTERN.matcher(lineLC);
                if (matcher.matches() && profile.baseTheme.parse(true, matcher.group(1), profileID, i+1))
                    continue;

            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(lineLC);
            if (matcher.matches()) {
                localTheme = profile.baseTheme.copy();
                section = Section.parse(matcher, localTheme.scale);
                profile.sections.add(section);
                continue;
            }
            if (section == null)
                profile.sections.add(section = new Section.TopLeft());

            if (( matcher = IF_PATTERN.matcher(lineLC) ).matches())
                profile.tempIfStack.push(new ConditionalElement.MultiLineBuilder( ExpressionParser.parseConditional(matcher.group(1), line, profileID, i+1, profile.enabled) ));

            else if (( matcher = ELSEIF_PATTERN.matcher(lineLC) ).matches())
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i, line+1, ErrorType.CONDITIONAL_NOT_STARTED, "=else if: §ocond§r=");
                else
                    profile.tempIfStack.peek().setConditional(ExpressionParser.parseConditional(matcher.group(1), line, profileID, i + 1, profile.enabled));

            else if (line.equalsIgnoreCase("=else="))
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i+1, line, ErrorType.CONDITIONAL_NOT_STARTED, "=else=");
                else
                    profile.tempIfStack.peek().setConditional(new Operation.Literal(1));

            else if (line.equalsIgnoreCase("=endif="))
                if (profile.tempIfStack.isEmpty())
                    Errors.addError(profileID, i+1, line, ErrorType.CONDITIONAL_NOT_STARTED, "end");
                else
                    addElement(profile, section, profile.tempIfStack.pop().build());

            else if (( matcher = LOCAL_THEME_PATTERN.matcher(lineLC) ).matches()) {
                if (localTheme.parse(false, matcher.group(1), profileID, i+1))
                    addElement(profile, section, new FunctionalElement.ChangeTheme(localTheme.copy()));
                else
                    Errors.addError(profileID, i+1, line, ErrorType.UNKNOWN_THEME_FLAG, "");
            }

            else if (GLOBAL_THEME_PATTERN.matcher(lineLC).matches() )
                Errors.addError(profileID, i+1, line, ErrorType.ILLEGAL_GLOBAL_THEME_FLAG, "");

            else
                addAllElement(profile, section, VariableParser.addElements(line, profileID, i + 1, profile.enabled, true));

        }

        while (!profile.tempIfStack.empty()) {
            addElement(profile, section, profile.tempIfStack.pop().build());
            Errors.addError(profileID, lines.size()+1, "end of profile", ErrorType.CONDITIONAL_NOT_ENDED, "");
        }

        profile.tempIfStack = null;

        profile.sections.removeIf(s -> s.elements.isEmpty());

        return profile;
    }

    private static void addElement(Profile profile, Section section, HudElement element) {
        if (profile.tempIfStack.empty())
            section.elements.add(element);
        else
            profile.tempIfStack.peek().add(element);
    }

    private static void addAllElement(Profile profile, Section section, List<HudElement> elements) {
        if (profile.tempIfStack.empty())
            section.elements.addAll(elements);
        else
            profile.tempIfStack.peek().addAll(elements);
    }

}