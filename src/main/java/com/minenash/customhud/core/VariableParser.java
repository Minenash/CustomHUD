package com.minenash.customhud.core;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.registry.VariableParseContext;
import com.minenash.customhud.core.registry.VariableRegistry;
import com.minenash.customhud.core.elements.*;
import com.minenash.customhud.core.conditionals.ConditionalParser;
import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.data.HudTheme;
import com.minenash.customhud.core.errors.ErrorType;
import com.minenash.customhud.core.errors.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser {

    private static final Pattern LINE_PARING_PATTERN = Pattern.compile("([^{}&]*)(\\{\\{(?:.*?, ?([\"']).*?\\3 ?)?}}|&?\\{.*?})?");
    private static final Pattern CONDITIONAL_PARSING_PATTERN = Pattern.compile("(.*?), ?\"(.*?)\"");
    private static final Pattern CONDITIONAL_PARSING_ALT_PATTERN = Pattern.compile("(.*?), ?'(.*?)'");
    private static final Pattern HEX_COLOR_VARIABLE_PATTERN = Pattern.compile("&\\{(?:0x|#)?([0-9a-fA-F]{3,8})}");

    public static List<HudElement> addElements(String str, int profile, int debugLine, Enabled enabled, boolean line) {
        List<String> parts = new ArrayList<>();

        Matcher matcher = LINE_PARING_PATTERN.matcher(str);
        while (matcher.find()) {
            String left = matcher.group(1);

            List<String> elements = new ArrayList<>();
            int j = 0;
            for (int i = 0; i < left.length()-1; i++) {
                if (left.charAt(i) == '\\' && left.charAt(i+1) == 'n') {
                    elements.add(left.substring(j,i));
                    elements.add("\n");
                    i+=2;
                    j=i;
                }
            }
            elements.add(left.substring(j));
            parts.addAll(elements);

            parts.add(matcher.group(2));
        }

        List<HudElement> elements = new ArrayList<>();

        for (String part : parts) {
            HudElement element = parseElement(part, profile, debugLine, enabled);
            if (element != null)
                elements.add(element);
        }

        if (line)
            elements.add(new FunctionalElement.NewLine());
        return elements;
    }

    private static List<ConditionalElement.ConditionalPair> parseConditional(Matcher args, String original, int profile, int debugLine, Enabled enabled) {
        List<ConditionalElement.ConditionalPair> pairs = new ArrayList<>();
        while (args.find()) {
//            System.out.println("Cond: '" + args.group(1) + "', Value: '" + args.group(2) + "'");
            pairs.add(new ConditionalElement.ConditionalPair(ConditionalParser.parseConditional(args.group(1), original, profile, debugLine, enabled), addElements(args.group(2), profile, debugLine, enabled, false)));
        }
        return pairs;
    }

    public static HudElement parseElement(String part, int profile, int debugLine, Enabled enabled) {
        if (part == null || part.isEmpty())
            return null;

        if (part.equals("\n"))
            return new FunctionalElement.NewLine();

        if (part.startsWith("&{")) {
            Matcher m = HEX_COLOR_VARIABLE_PATTERN.matcher(part);
            if (m.matches())
                return new FunctionalElement.ChangeColor(HudTheme.parseHexNumber(m.group(1), false));
            else {
                String colorStr = part.substring(2, part.length()-1).trim().toLowerCase();
                Integer color = HudTheme.parseColorName(colorStr);
                if (color != null)
                    return new FunctionalElement.ChangeColor(color);
                Errors.addError(profile, debugLine, part, ErrorType.UNKNOWN_COLOR, colorStr);
                return null;
            }
        }

        if (!part.startsWith("{"))
            return new SudoElements.Str(part);

        String original = part;
        part = part.substring(1, part.length()-1);
        if (part.startsWith("{") && part.length() > 1) {
            part = part.substring(1, part.length() - 1);

            List<ConditionalElement.ConditionalPair> pairs = parseConditional(CONDITIONAL_PARSING_PATTERN.matcher(part), original, profile, debugLine, enabled);
            if (pairs.isEmpty())
                pairs = parseConditional(CONDITIONAL_PARSING_ALT_PATTERN.matcher(part), original, profile, debugLine, enabled);
            if (pairs.isEmpty()) {
                Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                return null;
            }
            return new ConditionalElement(pairs);
        }

        String[] parts = part.split(" ");
        Flags flags = Flags.parse(profile, debugLine, parts);

        VariableParseContext context = new VariableParseContext(original, parts, parts[0], flags, enabled, profile, debugLine);
        HudElement element = VariableRegistry.get(context);

        if (element instanceof FunctionalElement.Error)
            return null;
        if (element != null)
            return element;

        Errors.addError(context, ErrorType.UNKNOWN_VARIABLE, parts[0]);
        return null;

    }
    
}