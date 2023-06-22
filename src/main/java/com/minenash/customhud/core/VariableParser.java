package com.minenash.customhud.core;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.mc1_20.elements.*;
import com.minenash.customhud.core.elements.*;
import com.minenash.customhud.mc1_20.elements.icon.DebugGizmoElement;
import com.minenash.customhud.mc1_20.elements.icon.ItemIconElement;
import com.minenash.customhud.mc1_20.elements.icon.SpaceElement;
import com.minenash.customhud.mc1_20.elements.icon.TextureIconElement;
import com.minenash.customhud.mc1_20.elements.stats.CustomStatElement;
import com.minenash.customhud.mc1_20.elements.stats.TypedStatElement;
import com.minenash.customhud.core.conditionals.ConditionalParser;
import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.data.HudTheme;
import com.minenash.customhud.core.errors.ErrorType;
import com.minenash.customhud.core.errors.Errors;
import com.minenash.customhud.mc1_20.mod_compat.CustomHudRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Pair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableParser {

    private static final Pattern LINE_PARING_PATTERN = Pattern.compile("([^{}&]*)(\\{\\{(?:.*?, ?([\"']).*?\\3 ?)?}}|&?\\{.*?})?");
    private static final Pattern CONDITIONAL_PARSING_PATTERN = Pattern.compile("(.*?), ?\"(.*?)\"");
    private static final Pattern CONDITIONAL_PARSING_ALT_PATTERN = Pattern.compile("(.*?), ?'(.*?)'");
    private static final Pattern TEXTURE_ICON_PATTERN = Pattern.compile("((?:[a-z0-9/._-]+:)?[a-z0-9/._-]+)(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?");
    private static final Pattern HEX_COLOR_VARIABLE_PATTERN = Pattern.compile("&\\{(?:0x|#)?([0-9a-fA-F]{3,8})}");

    public static List<HudElement> addElements(String str, int profile, int debugLine, Enabled enabled, boolean line) {
        List<String> parts = new ArrayList<>();

//        System.out.println("[Line " + debugLine+ "] '" + str + "'");
        Matcher matcher = LINE_PARING_PATTERN.matcher(str);
        while (matcher.find()) {
            String left = matcher.group(1);

            List<String> sections = new ArrayList<>();
            int j = 0;
            for (int i = 0; i < left.length()-1; i++) {
                if (left.charAt(i) == '\\' && left.charAt(i+1) == 'n') {
                    sections.add(left.substring(j,i));
                    sections.add("\n");
                    i+=2;
                    j=i;
                }
            }
            sections.add(left.substring(j));
//            System.out.println(sections + "\n");
            parts.addAll(sections);

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

        String[] flagParts = part.split(" ");
        Flags flags = Flags.parse(profile, debugLine, flagParts);
        part = flagParts[0];


        if (part.startsWith("real_time:")) {
            try {
                return new RealTimeElement(new SimpleDateFormat(part.substring(10)));
            }
            catch (IllegalArgumentException e) {
                Errors.addError(profile, debugLine, original, ErrorType.INVALID_TIME_FORMAT, e.getMessage());
            }
        }


        else if (part.startsWith("stat:")) {
            String stat = part.substring(5);

            HudElement element = stat("mined:",   Stats.MINED,   Registries.BLOCK, stat, flags, enabled);
            if (element == null) element = stat("crafted:", Stats.CRAFTED, Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("used:",    Stats.USED,    Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("broken:",  Stats.BROKEN,  Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("dropped:", Stats.DROPPED, Registries.ITEM,  stat, flags, enabled);
            if (element == null) element = stat("picked_up:", Stats.PICKED_UP, Registries.ITEM, stat, flags, enabled);
            if (element == null) element = stat("killed:",    Stats.KILLED,    Registries.ENTITY_TYPE, stat, flags, enabled);
            if (element == null) element = stat("killed_by:", Stats.KILLED_BY, Registries.ENTITY_TYPE, stat, flags, enabled);

            if (element != null)
                return element;

            Identifier statId = Registries.CUSTOM_STAT.get(new Identifier(stat));
            if (Stats.CUSTOM.hasStat(statId)) {
                enabled.add(Enabled.UPDATE_STATS);
                return new CustomStatElement(Stats.CUSTOM.getOrCreateStat(statId), flags);
            }
            else
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_STATISTIC, stat);
        }

        else if (part.startsWith("icon:")) {
            part = part.substring(part.indexOf(':')+1);

            Item item = Registries.ITEM.get(Identifier.tryParse(part));
            if (item != Items.AIR)
                return new ItemIconElement(new ItemStack(item), flags);

            Matcher matcher = TEXTURE_ICON_PATTERN.matcher(part);
            if (!matcher.matches()) {
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ICON, part);
                return null;
            }

//            for (int i = 0; i <= matcher.groupCount(); i++)
//                System.out.println(i + ": " + matcher.group(i));

            Identifier id = new Identifier(matcher.group(1) + ".png");
            int u = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
            int v = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
            int w = matcher.group(4) == null ? -1 : Integer.parseInt(matcher.group(4));
            int h = matcher.group(5) == null ? -1 : Integer.parseInt(matcher.group(5));

            TextureIconElement element = new TextureIconElement(id, u, v, w, h, flags);
            if (!element.isIconAvailable())
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ICON, id.toString());
            return element;

        }

        else if (part.startsWith("itemcount:")) {
            part = part.substring(part.indexOf(':')+1);

            try {
                Item item = Registries.ITEM.get(new Identifier(part));
                if (item == Items.AIR)
                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ITEM_ID, part);
                else
                    return new ItemCountElement(item);
            }
            catch (InvalidIdentifierException e) {
                Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_ITEM_ID, part);
            }
        }

        else if (part.startsWith("item:")) {
            int firstCollinIndex = part.indexOf(':', 6);

            String slot = firstCollinIndex == -1? part.substring(5) : part.substring(5, firstCollinIndex);
            String method = firstCollinIndex == -1? "" : part.substring(firstCollinIndex+1);
            Pair<HudElement,ErrorType> element = SlotItemElement.create(slot, method, flags);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ITEM_PROPERTY ? method : slot);
                return null;
            }
            return element.getLeft();
        }

        else if (part.startsWith("s:") || part.startsWith("setting:")) {
            String setting = part.substring(part.indexOf(':') + 1).toLowerCase();
            Pair<HudElement,Pair<ErrorType,String>> element = SettingsElement.create(setting, flags);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight().getLeft(), element.getRight().getRight());
                return null;
            }
            return flags.anyTextUsed() ? new FormattedElement(element.getLeft(), flags) : element.getLeft();
        }

        else if (part.equals("gizmo"))
            return new DebugGizmoElement(flags);

        else if (part.startsWith("space:")) {
            String widthStr = part.substring(6);
            try {
                return new SpaceElement( Integer.parseInt(widthStr) );
            }
            catch (NumberFormatException e) {
                Errors.addError(profile, debugLine, original, ErrorType.NOT_A_WHOLE_NUMBER, "\"" + widthStr + "\"");
                return null;
            }
        }

        else {
            HudElement element = getSupplierElement(part, enabled, flags);
            if (element != null) {
                return flags.anyTextUsed() ? new FormattedElement(element, flags) : element;
            }
            else {
                Matcher keyMatcher = registryKey.matcher(part);
                if (keyMatcher.matches()) {
                    element = CustomHudRegistry.get(keyMatcher.group(1), part);
                    if (element != null)
                        return element;

                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_VARIABLE, part);
                }
                else
                    Errors.addError(profile, debugLine, original, ErrorType.UNKNOWN_VARIABLE, part);
            }
        }
        return null;
    }

    private static final Pattern registryKey = Pattern.compile("(\\w+).*");

    private static HudElement stat(String prefix, StatType<?> type, Registry<?> registry, String stat, Flags flags, Enabled enabled) {
        if (!stat.startsWith(prefix))
            return null;

        Optional<?> entry = registry.getOrEmpty( new Identifier(stat.substring(prefix.length())) );
        if (entry.isPresent()) {
            enabled.add(Enabled.UPDATE_STATS);
            return new TypedStatElement(type, entry.get(), flags);
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static HudElement getSupplierElement(String name, Enabled enabled, Flags flags) {
        SupplierEntry entry = SupplierRegistry.get(name);

        if (entry == null) return null;

        enabled.add(entry.enabledMask);

        return switch (entry.type) {
            case BOOLEAN -> new SupplierElements.Bool((Supplier) entry.entry);
            case STRING -> new SupplierElements.Str((Supplier) entry.entry);
            case STR_INT -> new SupplierElements.StrInt((Supplier) entry.entry);
            case INT -> new SupplierElements.Num((Supplier) entry.entry, flags.scale, flags.precision == -1 ? 0 : flags.precision);
            case DEC -> {
                var e = (SupplierElements.Num.Entry) entry.entry;
                yield new SupplierElements.Num(e, flags.scale, flags.precision == -1 ? e.precision() : flags.precision);
            }
            case SPECIAL -> new SupplierElements.Special((SupplierElements.Special.Entry) entry.entry);
        };
    }
    
}
