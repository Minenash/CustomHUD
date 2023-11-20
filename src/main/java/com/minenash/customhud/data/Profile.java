package com.minenash.customhud.data;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile {

    public String name;
    public KeyBinding keyBinding;
    public boolean cycle = true;

    public static final Pattern SECTION_DECORATION_PATTERN = Pattern.compile("== ?section: ?(topleft|topcenter|topright|centerleft|centercenter|centerright|bottomleft|bottomcenter|bottomright) ?(?:, ?([-+]?\\d+))? ?(?:, ?([-+]?\\d+))? ?(?:, ?(true|false))? ?(?:, ?(-?\\d+|fit|max))? ?==");
    private static final Pattern TARGET_RANGE_FLAG_PATTERN = Pattern.compile("== ?targetrange: ?(\\d+|max) ?==");
    private static final Pattern CROSSHAIR_PATTERN = Pattern.compile("== ?crosshair: ?(.*) ?==");
    private static final Pattern DISABLE_PATTERN = Pattern.compile("== ?disable: ?(.*) ?==");
    private static final Pattern GLOBAL_THEME_PATTERN = Pattern.compile("== ?(.+) ?==");
    private static final Pattern LOCAL_THEME_PATTERN = Pattern.compile("= ?(.+) ?=");

    private static final Pattern IF_PATTERN = Pattern.compile("=if ?: ?(.+)=");
    private static final Pattern ELSEIF_PATTERN = Pattern.compile("=elseif ?: ?(.+)=");
    private static final Pattern FOR_PATTERN = Pattern.compile("=for ?: ?(.+)=");

    public ComplexData.Enabled enabled = new ComplexData.Enabled();

    public List<Section> sections = new ArrayList<>();

    public HudTheme baseTheme = HudTheme.defaults();
    public float targetDistance = 20;
    public Crosshairs crosshair = Crosshairs.NORMAL;
    public EnumSet<DisableElement> disabled = EnumSet.noneOf(DisableElement.class);
    public Map<String,Toggle> toggles = new LinkedHashMap<>();

    private String displayName = null;

    private MultiLineStacker stacker = new MultiLineStacker();

    public static Profile create(String name) {
        Profile p = new Profile();
        p.name = name;
        p.keyBinding = new KeyBinding("custom_hud." + name, GLFW.GLFW_KEY_UNKNOWN, "Toggles");
        return p;
    }

    public static Profile parseProfile(Path path, String profileName) {
        Profile profile = parseProfileInner(path, profileName);

        if (!Errors.getErrors(profileName).isEmpty()) {
            System.out.println("\n");
            System.out.println("Errors Found in profile '" + profileName + "'");
            for (var e : Errors.getErrors(profileName))
                System.out.println(e.line() + " | " + e.type() + " | " + e.source() + " | " + e.context());
            System.out.println();
        }

        if (profile != null)
            profile.keyBinding = new KeyBinding("custom_hud." + profileName, GLFW.GLFW_KEY_UNKNOWN, "Toggles");
        return profile;
    }

    private static Profile parseProfileInner(Path path, String profileName) {
        Errors.clearErrors(profileName);

        List<String> lines;

        try {
            if(!Files.exists(path.getParent()))
                Files.createDirectory(path.getParent());
            //TODO: ReAdd This!
            if (!Files.exists(path)) {
                Files.createFile(path);
//                if (profileID == 1) {
//                    try (OutputStream writer = Files.newOutputStream(path); InputStream input = Profile.class.getClassLoader().getResourceAsStream("assets/custom_hud/example_profile.txt")) {
//                        input.transferTo(writer);
//                    }
//                }
            }
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
            Errors.addError(profileName, "N/A", path.relativize(FabricLoader.getInstance().getGameDir().getParent()).toString(), ErrorType.IO, e.getMessage());
            return null;
        }

        Profile profile = new Profile();
        profile.name = profileName;

        Section section = null;
        HudTheme localTheme = profile.baseTheme.copy();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).replaceAll("&([0-9a-u]|zm|zn)", "§$1");
            String lineLC = line.toLowerCase().trim();
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
                    profile.crosshair = Crosshairs.parse(matcher.group(1));
                    if (profile.crosshair == null) {
                        profile.crosshair = Crosshairs.NORMAL;
                        Errors.addError(profileName, i+1, line, ErrorType.UNKNOWN_CROSSHAIR, matcher.group(1));
                    }
                    continue;
                }

                matcher = DISABLE_PATTERN.matcher(lineLC);
                if (matcher.matches()) {
                    if (!DisableElement.add(profile.disabled, matcher.group(1)))
                        Errors.addError(profileName, i+1, line, ErrorType.UNKNOWN_HUD_ELEMENT, matcher.group(1));
                    continue;
                }

                matcher = GLOBAL_THEME_PATTERN.matcher(lineLC);
                if (matcher.matches() && profile.baseTheme.parse(true, matcher.group(1), profileName, i+1))
                    continue;

            }
            Matcher matcher = SECTION_DECORATION_PATTERN.matcher(lineLC);
            if (matcher.matches()) {
                localTheme = profile.baseTheme.copy();

                if (section != null)
                    section.elements = profile.stacker.finish(0, profile, i-1, false);
                profile.stacker = new MultiLineStacker();

                section = switch (matcher.group(1)) {
                    case "topleft" -> new Section.TopLeft();
                    case "topcenter" -> new Section.TopCenter();
                    case "topright" -> new Section.TopRight();

                    case "centerleft" -> new Section.CenterLeft();
                    case "centercenter" -> new Section.CenterCenter();
                    case "centerright" -> new Section.CenterRight();

                    case "bottomleft" -> new Section.BottomLeft();
                    case "bottomcenter" -> new Section.BottomCenter();
                    case "bottomright" -> new Section.BottomRight();
                    default -> null;
                };

                section.xOffset = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
                section.yOffset = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
                section.hideOnChat = matcher.group(4) != null && Boolean.parseBoolean(matcher.group(4));

                String width = matcher.group(5);
                if (width == null) section.width = -1;
                else section.width = switch (width) {
                        case "fit" -> -1;
                        case "max" -> -2;
                        default -> Integer.parseInt(matcher.group(5));
                    };

                profile.sections.add(section);

                continue;
            }
            if (section == null)
                profile.sections.add(section = new Section.TopLeft());

            if (( matcher = IF_PATTERN.matcher(lineLC) ).matches())
                profile.stacker.startIf(matcher.group(1), profile, i, line, profile.enabled);

            else if (( matcher = ELSEIF_PATTERN.matcher(lineLC) ).matches())
                profile.stacker.elseIf(matcher.group(1), profile, i, line, profile.enabled);

            else if (line.equalsIgnoreCase("=else="))
                profile.stacker.else1(profile, i, line);

            else if (line.equalsIgnoreCase("=endif="))
                profile.stacker.endIf(profile, i, line);

            else if (( matcher = FOR_PATTERN.matcher(lineLC) ).matches())
                profile.stacker.startFor(matcher.group(1), profile, i, profile.enabled, line);

            else if (line.equalsIgnoreCase("=endfor="))
                profile.stacker.endFor(profile, i, line);

            else if (( matcher = LOCAL_THEME_PATTERN.matcher(lineLC) ).matches()) {
                if (localTheme.parse(false, matcher.group(1), profileName, i+1))
                    profile.stacker.addElement(new FunctionalElement.ChangeTheme(localTheme.copy()));
                else
                    Errors.addError(profileName, i+1, line, ErrorType.UNKNOWN_THEME_FLAG, "");
            }

            else if (GLOBAL_THEME_PATTERN.matcher(lineLC).matches() )
                Errors.addError(profileName, i+1, line, ErrorType.ILLEGAL_GLOBAL_THEME_FLAG, "");

            else
                profile.stacker.addElements(line, profile, i + 1, profile.enabled);

        }

        if (section != null)
            section.elements = profile.stacker.finish(0, profile, lines.size(), true);
        profile.stacker = null;

        profile.sections.removeIf(s -> s.elements.isEmpty());

        return profile;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name;
    }

}