package com.minenash.customhud;

import com.minenash.customhud.HudElements.*;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.icon.*;
import com.minenash.customhud.HudElements.list.ListCountElement;
import com.minenash.customhud.HudElements.list.ListElement;
import com.minenash.customhud.HudElements.methoded.AttributeElements;
import com.minenash.customhud.HudElements.methoded.SlotItemElement;
import com.minenash.customhud.HudElements.methoded.TeamElements;
import com.minenash.customhud.HudElements.stats.CustomStatElement;
import com.minenash.customhud.HudElements.stats.TypedStatElement;
import com.minenash.customhud.HudElements.supplier.*;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.conditionals.ExpressionParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.data.HudTheme;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.mod_compat.CustomHudRegistry;
import net.minecraft.entity.Entity;
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

import static com.minenash.customhud.HudElements.list.ListAttributeSuppliers.*;
import static com.minenash.customhud.HudElements.supplier.BooleanSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.EntryNumberSuppliers.*;
import static com.minenash.customhud.HudElements.supplier.IntegerSuppliers.*;
import static com.minenash.customhud.HudElements.list.ListSuppliers.*;
import static com.minenash.customhud.HudElements.supplier.SpecialSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringIntSupplierElement.*;
import static com.minenash.customhud.HudElements.supplier.StringSupplierElement.*;

public class VariableParser {

    private static final Pattern TEXTURE_ICON_PATTERN = Pattern.compile("((?:[a-z0-9/._-]+:)?[a-z0-9/._-]+)(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?(?:,(\\d+))?");
    private static final Pattern HEX_COLOR_VARIABLE_PATTERN = Pattern.compile("&\\{(?:0x|#)?([0-9a-fA-F]{3,8})}");
    private static final Pattern EXPRESSION_WITH_PRECISION = Pattern.compile("\\$(?:(\\d+) *,)?(.*)");
    private static final Pattern ITEM_VARIABLE_PATTERN = Pattern.compile("([\\w.]*)(?::([\\w.]*))?.*");

    public static List<HudElement> addElements(String str, int profile, int debugLine, ComplexData.Enabled enabled, boolean line, Supplier<?> listSupplier) {
//        System.out.println("[Line " + debugLine+ "] '" + str + "'");

        List<HudElement> elements = new ArrayList<>();

        System.out.println("PARTITION:");
        for (String part : partition(str)) {
            System.out.println("`" + part + "`");
            HudElement element = parseElement(part, profile, debugLine, enabled, listSupplier);
            if (element != null)
                elements.add(element);
        }

        if (line)
            elements.add(new FunctionalElement.NewLine());
        return elements;
    }

    private static List<String> partition(String str) {
        char[] chars = str.toCharArray();
        List<String> parts = new ArrayList<>();

        int nest = 0;
        int startIndex = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = chars[i];

            switch (c) {
                case '\\' -> {
                    if (nest == 0 && i+1 < chars.length && chars[i+1] == 'n') {
                        parts.add(str.substring(startIndex, i));
                        parts.add("\n");
                        startIndex = i+2;
                        i++;
                    }
                }
                case '&' -> {
                    if (i < chars.length-1 && chars[i+1] == '{') {
                        if (nest == 0 && i != startIndex) {
                            parts.add(str.substring(startIndex, i));
                            startIndex = i;
                        }
                        nest++;
                    }
                }
                case '{' -> {
                    if (i > 0 && (chars[i-1] == '{' || chars[i-1] == '&')) continue;
                    if (nest == 0 && i != startIndex) {
                        parts.add(str.substring(startIndex, i));
                        startIndex = i;
                    }
                    nest++;
                }
                case '}' -> {
                    if (i < chars.length-1 && chars[i+1] == '}') continue;
                    if (nest == 1) {
                        parts.add(str.substring(startIndex, i+1));
                        startIndex = i+1;
                    }
                    nest--;
                }
            }
        }
        if (startIndex < chars.length)
            parts.add(str.substring(startIndex, chars.length));

        return parts;
    }

    private static List<String> partitionConditional(String str) {
        char[] chars = str.toCharArray();
        List<String> parts = new ArrayList<>();

        int nest = 0;
        int qNest = 0;
        int startIndex = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = chars[i];

            switch (c) {
                case '&' -> {
                    if (i < chars.length-1 && chars[i+1] == '{')
                        nest++;
                }
                case '{' -> {
                    if (i > 0 && (chars[i-1] == '{' || chars[i-1] == '&')) continue;
                    nest++;
                }
                case '}' -> {
                    if (i < chars.length-1 && chars[i+1] == '}') continue;
                    nest--;
                }
                case ',' -> {
                    if (nest == 0 && qNest == 0) {
                        parts.add(str.substring(startIndex, i));
                        startIndex = i+1;
                    }
                }
                case '"' -> {
                    if (qNest == nest) qNest++;
                    else qNest--;
                }
            }
        }
        if (startIndex < chars.length-1)
            parts.add(str.substring(startIndex, chars.length));

        return parts;
    }

    public static HudElement parseElement(String part, int profile, int debugLine, ComplexData.Enabled enabled, Supplier<?> listSupplier) {
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
            return new StringElement(part);

        String original = part;
        part = part.substring(1, part.length()-1);
        if (part.startsWith("{") && part.length() > 1) {
            part = part.substring(1, part.length() - 1);

            System.out.println("COND:");
            List<String> ps = partitionConditional(part);
            for (String p : ps)
                System.out.println("`" + p + "`");

            if (ps.size() < 2) {
                Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                return null;
            }
            List<ConditionalElement.ConditionalPair> pairs = new ArrayList<>();

            for (int i = 0; i < ps.size()-1; i+=2) {
                String cond = ps.get(i);
                String result = ps.get(i+1).trim();
                if (!result.startsWith("\"") || !result.endsWith("\"")) {
                    Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                    return null;
                }
                result = result.substring(1, result.length()-1);

                pairs.add(new ConditionalElement.ConditionalPair(ExpressionParser.parseExpression(cond, original, profile, debugLine, enabled, listSupplier), addElements(result, profile, debugLine, enabled, false, listSupplier)));
            }
            if (ps.size() % 2 == 1) {
                String result = ps.get(ps.size()-1).trim();
                if (!result.startsWith("\"") || !result.endsWith("\"")) {
                    Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                    return null;
                }
                result = result.substring(1, result.length()-1);

                pairs.add(new ConditionalElement.ConditionalPair(new Operation.Literal(1), addElements(result, profile, debugLine, enabled, false, listSupplier)));
            }

            if (pairs.isEmpty()) {
                Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
                return null;
            }
            return new ConditionalElement(pairs);
        }

        if (part.startsWith("$")) {
            try {
                Matcher matcher = EXPRESSION_WITH_PRECISION.matcher(part);
                matcher.matches();
                int precision = matcher.group(1) == null ? -1 : Integer.parseInt(matcher.group(1));
                return new ExpressionElement( ExpressionParser.parseExpression(matcher.group(2), original, profile, debugLine, enabled, listSupplier), precision );
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        HudElement he = getListSupplierElements(part, profile, debugLine, enabled, original);
        if (he != null) return he;

        if (part.startsWith("real_time:")) {
            try {
                return new RealTimeElement(new SimpleDateFormat(part.substring(10)));
            }
            catch (IllegalArgumentException e) {
                Errors.addError(profile, debugLine, original, ErrorType.INVALID_TIME_FORMAT, e.getMessage());
            }
        }

        else if (part.startsWith("item:")) {
            Matcher matcher = ITEM_VARIABLE_PATTERN.matcher(part.substring(5));

            if (!matcher.matches()) return null;

            String slot = matcher.group(1) == null ? "" : matcher.group(1);
            String method = matcher.group(2) == null ? "" : matcher.group(2);

            Flags flags = SlotItemElement.NO_FLAGS.contains(method) ? new Flags() : Flags.parse(profile, debugLine, part.split(" "));
            Pair<HudElement,ErrorType> element = SlotItemElement.create(slot, method, flags, profile, debugLine, enabled, original);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ITEM_PROPERTY ? method : slot);
                return null;
            }
            return element.getLeft();
        }

        else if (part.startsWith("attribute:")
                || part.startsWith("target_entity_attribute:") || part.startsWith("target_entity_attr:") || part.startsWith("tea:")
                || part.startsWith("hooked_entity_attribute:") || part.startsWith("hooked_entity_attr:") || part.startsWith("hea:")
        ) {
            Matcher matcher = ITEM_VARIABLE_PATTERN.matcher(part.substring(part.indexOf(':')+1));

            if (!matcher.matches()) return null;

            String attribute = matcher.group(1) == null ? "" : matcher.group(1);
            String method = matcher.group(2) == null ? "" : matcher.group(2);
            Supplier<Entity> entity = switch (part.charAt(0)) {
                case 't' -> AttributeElements.TARGET_ENTITY;
                case 'h' -> AttributeElements.HOOKED_ENTITY;
                default -> AttributeElements.PLAYER;
            };

            Flags flags = AttributeElements.NO_FLAGS.contains(method) ? new Flags() : Flags.parse(profile, debugLine, part.split(" "));
            Pair<HudElement,ErrorType> element = AttributeElements.create(entity, attribute, method, flags, profile, debugLine, enabled, original);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ATTRIBUTE_PROPERTY ? method : attribute);
                return null;
            }
            return element.getLeft();
        }

        else if (part.startsWith("team:")) {
            Matcher matcher = ITEM_VARIABLE_PATTERN.matcher(part.substring(5));

            if (!matcher.matches()) return null;

            String team = matcher.group(1) == null ? "" : matcher.group(1);
            String method = matcher.group(2) == null ? "" : matcher.group(2);

            Flags flags = SlotItemElement.NO_FLAGS.contains(method) ? new Flags() : Flags.parse(profile, debugLine, part.split(" "));
            Pair<HudElement,ErrorType> element = TeamElements.create(team, method, flags, profile, debugLine, enabled, original);

            if (element.getRight() != null) {
                Errors.addError(profile, debugLine, original, element.getRight(), element.getRight() == ErrorType.UNKNOWN_ATTRIBUTE_PROPERTY ? method : team);
                return null;
            }
            return element.getLeft();
        }



        String[] flagParts = part.split(" ");
        Flags flags = Flags.parse(profile, debugLine, flagParts);
        part = flagParts[0];

        if (listSupplier != null) {
            HudElement element = getListAttributeSupplierElement(part, enabled, flags, listSupplier);
            if (element instanceof FunctionalElement.CreateListElement cle) {
                String p = original.substring(1, original.length() - 1);
                return listElement(cle.supplier, p, p.indexOf(','), profile, debugLine, enabled, original);
            }
            if (element != null)
                return element;
        }

        if (part.startsWith("stat:")) {
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
                enabled.updateStats = true;
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

        else if (part.equals("record_icon")) {
            enabled.music = true;
            return new RecordIconElement(flags);
        }

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

    private static HudElement stat(String prefix, StatType<?> type, Registry<?> registry, String stat, Flags flags, ComplexData.Enabled enabled) {
        if (!stat.startsWith(prefix))
            return null;

        Optional<?> entry = registry.getOrEmpty( new Identifier(stat.substring(prefix.length())) );
        if (entry.isPresent()) {
            enabled.updateStats = true;
            return new TypedStatElement(type, entry.get(), flags);
        }

        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static HudElement getSupplierElement(String name, ComplexData.Enabled enabled, Flags flags) {

        Supplier supplier = getStringSupplier(name, enabled);
        if (supplier != null)
            return new StringSupplierElement(supplier);

        supplier = getBooleanSupplier(name, enabled);
        if (supplier != null)
            return new BooleanSupplierElement(supplier);

        supplier = getIntegerSupplier(name, enabled);
        if (supplier != null)
            return new NumberSupplierElement(supplier, flags.scale, flags.precision);

        NumberSupplierElement.Entry entry = getDecimalSupplier(name, enabled);
        if (entry != null)
            return new NumberSupplierElement(entry, flags.scale, flags.precision, flags.formatted);

        supplier = getStringIntSupplier(name, enabled);
        if (supplier != null)
            return new StringIntSupplierElement(supplier);

        SpecialSupplierElement.Entry entry2 = getSpecialSupplierElements(name, enabled);
        if (entry2 != null)
            return new SpecialSupplierElement(entry2);

        return null;
    }

    private static Supplier<String> getStringSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "version" -> VERSION;
            case "client_version" -> CLIENT_VERSION;
            case "modded_name" -> MODDED_NAME;
            case "display_name", "name" -> DISPLAY_NAME;
            case "username" -> USERNAME;
            case "uuid" -> UUID;
            case "team" -> TEAM;
            case "dimension" -> DIMENSION;
            case "dimension_id" -> DIMENSION_ID;
            case "facing" -> FACING;
            case "facing_short" -> FACING_SHORT;
            case "facing_towards_xz" -> FACING_TOWARDS_XZ;
            case "biome" -> BIOME;
            case "biome_id" -> BIOME_ID;
            case "moon_phase_word" -> { enabled.clientChunk = true; yield MOON_PHASE_WORD; }
            case "target_entity", "te" -> {enabled.targetEntity = true; yield TARGET_ENTITY;}
            case "target_entity_id", "tei" -> {enabled.targetEntity = true; yield TARGET_ENTITY_ID;}
            case "target_entity_name", "ten" -> {enabled.targetEntity = true; yield TARGET_ENTITY_NAME;}
            case "target_entity_uuid", "teu" -> {enabled.targetEntity = true; yield TARGET_ENTITY_UUID;}
            case "last_hit", "lh" -> {enabled.targetEntity = true; yield LAST_HIT_ENTITY;}
            case "last_hit_id", "lhi" -> {enabled.targetEntity = true; yield LAST_HIT_ENTITY_ID;}
            case "last_hit_name", "lhn" -> {enabled.targetEntity = true; yield LAST_HIT_ENTITY_NAME;}
            case "last_hit_uuid", "lhu" -> {enabled.targetEntity = true; yield LAST_HIT_ENTITY_UUID;}
            case "hooked_entity", "he" -> HOOKED_ENTITY;
            case "hooked_entity_id", "hei" -> HOOKED_ENTITY_ID;
            case "hooked_entity_name", "hen" -> HOOKED_ENTITY_NAME;
            case "hooked_entity_uuid", "heu" -> HOOKED_ENTITY_UUID;
            case "world_name", "world" -> WORLD_NAME;
            case "server_name" -> SERVER_NAME;
            case "server_address", "address", "ip" -> SERVER_ADDRESS;
            case "java_version" -> JAVA_VERSION;
            case "cpu_name" -> CPU_NAME;
            case "gpu_name" -> GPU_NAME;
            case "server_brand" -> SERVER_BRAND;

            case "music_id" -> {enabled.music = true; yield MUSIC_ID;}
            case "music_name" -> {enabled.music = true; yield MUSIC_NAME;}
            case "record_id" -> {enabled.music = true; yield RECORD_ID;}
            case "record_name" -> {enabled.music = true; yield RECORD_NAME;}

            case "bb_peaks","biome_builder_peaks" -> {enabled.serverWorld = true; yield BIOME_BUILDER_PEAKS;}
            case "bb_cont","biome_builder_continents" -> {enabled.serverWorld = true; yield BIOME_BUILDER_CONTINENTS;}

            case "am_pm" -> { enabled.time = true; yield TIME_AM_PM; }
            default -> null;
        };
    }

    private static Supplier<Boolean> getBooleanSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "vsync" -> VSYNC;
            case "sp", "singleplayer" -> SINGLEPLAYER;
            case "mp", "multiplayer" -> MULTIPLAYER;
            case "survival" -> SURVIVAL;
            case "creative" -> CREATIVE;
            case "adventure" -> ADVENTURE;
            case "spectator" -> SPECTATOR;
            case "chunks_culling" -> CHUNK_CULLING;
            case "overworld" -> IN_OVERWORLD;
            case "nether" -> IN_NETHER;
            case "end" -> IN_END;
            case "raining" -> {enabled.world = true; yield IS_RAINING;}
            case "thundering" -> {enabled.world = true; yield IS_THUNDERING;}
            case "snowing" -> {enabled.world = true; yield IS_SNOWING;}
            case "slime_chunk" -> {enabled.world = true; yield IS_SLIME_CHUNK;}
            case "sprinting" -> SPRINTING;
            case "sneaking" -> SNEAKING;
            case "swimming" -> SWIMMING;
            case "flying" -> FLYING;
            case "on_ground" -> ON_GROUND;
            case "sprint_held" -> SPRINT_HELD;
            case "screen_open" -> SCREEN_OPEN;
            case "chat_open" -> CHAT_OPEN;
            case "player_list_open","tab_open" -> PLAYER_LIST_OPEN;
            case "item_has_durability", "item_has_dur" -> ITEM_HAS_DURABILITY;
            case "offhand_item_has_durability", "oitem_has_dur" -> OFFHAND_ITEM_HAS_DURABILITY;
            case "fishing_is_cast" -> FISHING_IS_CAST;
            case "fishing_is_hooked" -> FISHING_IS_HOOKED;
            case "fishing_has_caught" -> FISHING_HAS_CAUGHT;
            case "fishing_in_open_water" -> FISHING_IN_OPEN_WATER;

            case "music_playing" -> {enabled.music = true; yield MUSIC_PLAYING;}
            case "record_playing" -> {enabled.music = true; yield RECORD_PLAYING;}

            case "has_noise" -> {enabled.serverWorld = true; yield HAS_NOISE;}

            default -> null;
        };
    }

    private static Supplier<Number> getIntegerSupplier(String element, ComplexData.Enabled enabled) {
        return switch (element) {
            case "fps" -> FPS;
            case "max_fps" -> MAX_FPS;
            case "biome_blend" -> BIOME_BLEND;
            case "simulation_distance", "sd" -> SIMULATION_DISTANCE;
            case "packets_sent", "tx" -> PACKETS_SENT;
            case "packets_received", "rx" -> PACKETS_RECEIVED;
            case "chunks_rendered" -> CHUNKS_RENDERED;
            case "chunks_loaded" -> CHUNKS_LOADED;
            case "render_distance" -> RENDER_DISTANCE;
            case "queued_tasks" -> QUEUED_TASKS;
            case "upload_queue" -> UPLOAD_QUEUE;
            case "buffer_count" -> BUFFER_COUNT;
            case "entities_rendered" -> ENTITIES_RENDERED;
            case "entities_loaded" -> ENTITIES_LOADED;
            case "force_loaded_chunks", "fc" -> { enabled.world = true; yield FORCED_LOADED_CHUNKS; }
            case "block_x", "bx" -> BLOCK_X;
            case "block_y", "by" -> BLOCK_Y;
            case "block_z", "bz" -> BLOCK_Z;
            case "target_block_x", "target_x", "tbx" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_X; }
            case "target_block_y", "target_y", "tby" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_Y; }
            case "target_block_z", "target_z", "tbz" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_Z; }
            case "target_block_distance", "target_distance", "tbd" -> { enabled.world = enabled.targetBlock = true; yield TARGET_BLOCK_DISTANCE; }
            case "target_fluid_x", "tfx" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_X; }
            case "target_fluid_y", "tfy" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_Y; }
            case "target_fluid_z", "tfz" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_Z; }
            case "target_fluid_distance", "tfd" -> { enabled.world = enabled.targetFluid = true; yield TARGET_FLUID_DISTANCE; }
            case "in_chunk_x", "icx" -> IN_CHUNK_X;
            case "in_chunk_y", "icy" -> IN_CHUNK_Y;
            case "in_chunk_z", "icz" -> IN_CHUNK_Z;
            case "chunk_x", "cx" -> CHUNK_X;
            case "chunk_y", "cy" -> CHUNK_Y;
            case "chunk_z", "cz" -> CHUNK_Z;
            case "region_x", "rex" -> REGION_X;
            case "region_z", "rez" -> REGION_Z;
            case "region_relative_x", "rrx" -> REGION_RELATIVE_X;
            case "region_relative_z", "rrz" -> REGION_RELATIVE_Z;
            case "client_light", "light" -> { enabled.clientChunk = true; yield CLIENT_LIGHT; }
            case "client_light_sky", "light_sky" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_SKY; }
            case "client_light_sun", "light_sun" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_SUN; }
            case "client_light_block", "light_block" -> { enabled.clientChunk = true; yield CLIENT_LIGHT_BLOCK; }
            case "server_light_sky" -> { enabled.world = enabled.serverChunk = true; yield SERVER_LIGHT_SKY; }
            case "server_light_block" -> { enabled.world = enabled.serverChunk = true; yield SERVER_LIGHT_BLOCK; }
            case "client_height_map_surface", "chs" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_SURFACE; }
            case "client_height_map_motion_blocking", "chm" -> { enabled.clientChunk = true; yield CLIENT_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_surface", "shs" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_SURFACE; }
            case "server_height_map_ocean_floor", "sho" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_OCEAN_FLOOR; }
            case "server_height_map_motion_blocking", "shm" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING; }
            case "server_height_map_motion_blocking_no_leaves", "shml" -> { enabled.serverChunk = true; yield SERVER_HEIGHT_MAP_MOTION_BLOCKING_NO_LEAVES; }
            case "moon_phase" -> { enabled.clientChunk = true; yield MOON_PHASE; }
            case "spawn_chunks", "sc" -> { enabled.serverWorld = true; yield SPAWN_CHUNKS; }
            case "monsters" -> { enabled.serverWorld = true; yield MONSTERS; }
            case "creatures" -> { enabled.serverWorld = true; yield CREATURES; }
            case "ambient_mobs" -> { enabled.serverWorld = true; yield AMBIENT_MOBS; }
            case "water_creatures" -> { enabled.serverWorld = true; yield WATER_CREATURES; }
            case "water_ambient_mobs" -> { enabled.serverWorld = true; yield WATER_AMBIENT_MOBS; }
            case "underground_water_creatures" -> { enabled.serverWorld = true; yield UNDERGROUND_WATER_CREATURE; }
            case "axolotls" -> { enabled.serverWorld = true; yield AXOLOTLS; }
            case "misc_mobs" -> { enabled.serverWorld = true; yield MISC_MOBS; }
            case "java_bit" -> JAVA_BIT;
            case "cpu_cores" -> CPU_CORES;
            case "cpu_threads" -> CPU_THREADS;
            case "display_width" -> DISPLAY_WIDTH;
            case "display_height" -> DISPLAY_HEIGHT;
            case "display_refresh_rate" -> DISPLAY_REFRESH_RATE;
            case "mods" -> MODS;
            case "ping" -> {enabled.pingMetrics = true; yield PING;}
            case "latency" -> LATENCY;
            case "time", "solar_time" -> SOLAR_TIME;
            case "lunar_time" -> LUNAR_TIME;
            case "slots_used" -> {enabled.slots = true; yield SLOTS_USED;}
            case "slots_empty" -> {enabled.slots = true; yield SLOTS_EMPTY;}

            case "health","hp" -> HEALTH;
            case "max_health","max_hp" -> HEALTH_MAX;
            case "food","hunger" -> FOOD_LEVEL;
            case "food_per","food_percentage" -> FOOD_LEVEL_PERCENTAGE;
            case "saturation" -> SATURATION_LEVEL;
            case "saturation_per","saturation_percentage" -> SATURATION_LEVEL_PERCENTAGE;
            case "armor","armour" -> ARMOR_LEVEL;
            case "armor_per","armor_percentage","armour_per","armour_percentage" -> ARMOR_LEVEL_PERCENTAGE;
            case "air" -> AIR_LEVEL;
            case "xp_level" -> XP_LEVEL;
            case "xp" -> XP_POINTS;
            case "xp_needed" -> XP_POINTS_NEEDED;

            case "bb_erosion","biome_builder_erosion" -> {enabled.serverWorld = true; yield BIOME_BUILDER_EROSION;}
            case "bb_temp","biome_builder_temperature" -> {enabled.serverWorld = true; yield BIOME_BUILDER_TEMPERATURE;}
            case "bb_veg","biome_builder_vegetation" -> {enabled.serverWorld = true; yield BIOME_BUILDER_VEGETATION;}

            case "item_durability", "item_dur" -> ITEM_DURABILITY;
            case "item_max_durability", "item_max_dur" -> ITEM_MAX_DURABILITY;
            case "offhand_item_durability", "oitem_dur" -> OFFHAND_ITEM_DURABILITY;
            case "offhand_item_max_durability", "oitem_max_dur" -> OFFHAND_ITEM_MAX_DURABILITY;
            case "hour12", "hour" -> { enabled.time = true; yield TIME_HOUR_12; }
            case "lcps" -> { enabled.clicksPerSeconds = true; yield LCPS; }
            case "rcps" -> { enabled.clicksPerSeconds = true; yield RCPS; }
            default -> null;
        };
    }

    private static Supplier<String> getStringIntSupplier(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "particles", "p": return PARTICLES;
            case "streaming_sounds", "sounds": enabled.sound = true; return STREAMING_SOUNDS;
            case "max_streaming_sounds", "max_sounds": enabled.sound = true; return MAX_STREAMING_SOUNDS;
            case "static_sounds": enabled.sound = true; return STATIC_SOUNDS;
            case "max_static_sounds": enabled.sound = true; return MAX_STATIC_SOUNDS;
            default: return null;
        }
    }

    private static NumberSupplierElement.Entry getDecimalSupplier(String element, ComplexData.Enabled enabled) {
        if (element.startsWith("velocity_"))
            enabled.velocity = true;
        return switch (element) {
            case "x" -> X;
            case "y" -> Y;
            case "z" -> Z;
            case "nether_x", "nx" -> NETHER_X;
            case "nether_z", "nz" -> NETHER_Z;
            case "target_entity_x", "tex" -> {enabled.targetEntity = true; yield TARGET_ENTITY_X;}
            case "target_entity_y", "tey" -> {enabled.targetEntity = true; yield TARGET_ENTITY_Y;}
            case "target_entity_z", "tez" -> {enabled.targetEntity = true; yield TARGET_ENTITY_Z;}
            case "target_entity_distance", "ted" -> {enabled.targetEntity = true; yield TARGET_ENTITY_DISTANCE;}
            case "last_hit_distance", "lhd" -> {enabled.targetEntity = true; yield LAST_HIT_ENTITY_DISTANCE;}
            case "hooked_entity_x", "hex" -> HOOKED_ENTITY_X;
            case "hooked_entity_y", "hey" -> HOOKED_ENTITY_Y;
            case "hooked_entity_z", "hez" -> HOOKED_ENTITY_Z;
            case "hooked_entity_distance", "hed" -> HOOKED_ENTITY_DISTANCE;
            case "reach_distance", "reach" -> REACH_DISTANCE;
            case "fishing_hook_distance" -> FISHING_HOOK_DISTANCE;
            case "velocity_xz" -> VELOCITY_XZ;
            case "velocity_y" -> VELOCITY_Y;
            case "velocity_xyz" -> VELOCITY_XYZ;
            case "velocity_xz_kmh" -> VELOCITY_XZ_KMH;
            case "velocity_y_kmh" -> VELOCITY_Y_KMH;
            case "velocity_xyz_kmh" -> VELOCITY_XYZ_KMH;
            case "yaw" -> YAW;
            case "pitch" -> PITCH;
            case "day" -> DAY;
            case "mood" -> MOOD;
            case "tps" -> TPS;
            case "memory_used_percentage" -> MEMORY_USED_PERCENTAGE;
            case "memory_used" -> MEMORY_USED;
            case "memory_total" -> TOTAL_MEMORY;
            case "memory_allocated_percentage" -> ALLOCATED_PERCENTAGE;
            case "memory_allocated" -> ALLOCATED;
            case "cpu_usage", "cpu" -> {enabled.cpu = true; yield CPU_USAGE;}
            case "gpu_usage", "gpu" -> {enabled.gpuMetrics = true; yield GPU_USAGE;}
            case "ms_ticks", "tick_ms" -> TICK_MS;
            case "frame_ms_min" -> { enabled.frameMetrics = true; yield FRAME_MS_MIN;}
            case "frame_ms_max" -> { enabled.frameMetrics = true; yield FRAME_MS_MAX;}
            case "frame_ms_avg" -> { enabled.frameMetrics = true; yield FRAME_MS_AVG;}
            case "fps_min" -> { enabled.frameMetrics = true; yield FPS_MIN;}
            case "fps_max" -> { enabled.frameMetrics = true; yield FPS_MAX;}
            case "fps_avg" -> { enabled.frameMetrics = true; yield FPS_AVG;}
            case "frame_ms_samples","fps_samples" -> { enabled.frameMetrics = true; yield FRAME_MS_SAMPLES;}
            case "tick_ms_min" -> { enabled.tickMetrics = true; yield TICK_MS_MIN;}
            case "tick_ms_max" -> { enabled.tickMetrics = true; yield TICK_MS_MAX;}
            case "tick_ms_avg" -> { enabled.tickMetrics = true; yield TICK_MS_AVG;}
            case "tick_ms_samples" -> { enabled.tickMetrics = true; yield TICK_MS_SAMPLES;}
            case "tps_min" -> { enabled.tpsMetrics = true; yield TPS_MIN;}
            case "tps_max" -> { enabled.tpsMetrics = true; yield TPS_MAX;}
            case "tps_avg" -> { enabled.tpsMetrics = true; yield TPS_AVG;}
            case "tps_samples" -> { enabled.tpsMetrics = true; yield TICK_MS_SAMPLES;}
            case "ping_min" -> { enabled.pingMetrics = true; yield PING_MIN;}
            case "ping_max" -> { enabled.pingMetrics = true; yield PING_MAX;}
            case "ping_avg" -> { enabled.pingMetrics = true; yield PING_AVG;}
            case "ping_samples" -> { enabled.pingMetrics = true; yield PING_SAMPLES;}
            case "packet_size_min" -> { enabled.packetMetrics = true; yield PACKET_SIZE_MIN;}
            case "packet_size_max" -> { enabled.packetMetrics = true; yield PACKET_SIZE_MAX;}
            case "packet_size_avg" -> { enabled.packetMetrics = true; yield PACKET_SIZE_AVG;}
            case "packet_size_samples" -> { enabled.packetMetrics = true; yield PACKET_SIZE_SAMPLES;}
            case "slots_percentage", "slots_per" -> {enabled.slots = true; yield SLOTS_PERCENTAGE;}
            case "record_elapsed_percentage","record_elapsed_per" -> {enabled.music = true; yield RECORD_ELAPSED_PER;}
            case "record_length" -> {enabled.music = true; yield RECORD_LENGTH;}
            case "record_elapsed" -> {enabled.music = true; yield RECORD_ELAPSED;}
            case "record_remaining" -> {enabled.music = true; yield RECORD_REMAINING;}

            case "xp_per", "xp_percentage" -> XP_POINTS_PER;
            case "air_per", "air_percentage" -> AIR_LEVEL_PERCENTAGE;
            case "health_per", "health_percentage", "hp_per" -> HEALTH_PERCENTAGE;

            case "nr_temp","noise_temperature" -> {enabled.serverWorld = true; yield NOISE_ROUTER_TEMPERATURE;}
            case "nr_veg","noise_vegetation" -> {enabled.serverWorld = true; yield NOISE_ROUTER_VEGETATION;}
            case "nr_cont","noise_continents" -> {enabled.serverWorld = true; yield NOISE_ROUTER_CONTINENTS;}
            case "nr_erosion","noise_erosion" -> {enabled.serverWorld = true; yield NOISE_ROUTER_EROSION;}
            case "nr_depth","noise_depth" -> {enabled.serverWorld = true; yield NOISE_ROUTER_DEPTH;}
            case "nr_ridges","noise_ridges" -> {enabled.serverWorld = true; yield NOISE_ROUTER_RIDGES;}
            case "nr_peaks","noise_peaks" -> {enabled.serverWorld = true; yield NOISE_ROUTER_PEAKS;}
            case "nr_init","noise_init_density" -> {enabled.serverWorld = true; yield NOISE_ROUTER_INIT_DENSITY;}
            case "nr_final","noise_final_density" -> {enabled.serverWorld = true; yield NOISE_ROUTER_FINAL_DENSITY;}

            case "item_durability_percent", "item_dur_per" -> ITEM_DURABILITY_PERCENT;
            case "offhand_item_durability_percent", "oitem_dur_per" -> OFFHAND_ITEM_DURABILITY_PERCENT;
            case "local_difficulty" -> { enabled.localDifficulty = enabled.world = true; yield LOCAL_DIFFICULTY; }
            case "clamped_local_difficulty" -> { enabled.localDifficulty = enabled.world = true; yield CLAMPED_LOCAL_DIFFICULTY; }
            default -> null;
        };
    }

    private static SpecialSupplierElement.Entry getSpecialSupplierElements(String element, ComplexData.Enabled enabled) {
        switch (element) {
            case "hour24": { enabled.time = true; return TIME_HOUR_24; }
            case "minute": { enabled.time = true; return TIME_MINUTES; }
            case "second": { enabled.time = true; return TIME_SECONDS; }
            case "target_block", "tb": {enabled.world = enabled.targetBlock = true; return TARGET_BLOCK;}
            case "target_block_id", "tbi": {enabled.world = enabled.targetBlock = true; return TARGET_BLOCK_ID;}
            case "target_fluid", "tf": {enabled.world = enabled.targetFluid = true; return TARGET_FLUID;}
            case "target_fluid_id", "tfi": {enabled.world = enabled.targetFluid = true; return TARGET_FLUID_ID;}
            case "item": return ITEM;
            case "item_name": return ITEM_NAME;
            case "item_id": return ITEM_ID;
            case "offhand_item", "oitem": return OFFHAND_ITEM;
            case "offhand_item_name": return OFFHAND_ITEM_NAME;
            case "offhand_item_id", "oitem_id": return OFFHAND_ITEM_ID;
            case "clouds": return CLOUDS;
            case "graphics_mode": return GRAPHICS_MODE;
            case "facing_towards_pn_word": return FACING_TOWARDS_PN_WORD;
            case "facing_towards_pn_sign": return FACING_TOWARDS_PN_SIGN;
            case "gamemode": return GAMEMODE;
            default: return null;
        }
    }

    private static HudElement getListSupplierElements(String part, int profile, int debugLine, ComplexData.Enabled enabled, String original) {
        int commaIndex = part.indexOf(",");
        String variable = part;
        if (commaIndex != -1)
            variable = variable.substring(0, commaIndex);

        Supplier<List<?>> supplier = getListSupplier(variable, enabled);
        if (supplier == null)
            return null;

        return listElement(supplier, part, commaIndex, profile, debugLine, enabled, original);
    }

    public static Supplier<List<?>> getListSupplier(String variable, ComplexData.Enabled enabled) {
        return switch (variable) {
            case "effects" -> STATUS_EFFECTS;
            case "pos_effects", "positive_effects" -> STATUS_EFFECTS_POSITIVE;
            case "neg_effects", "negative_effects" -> STATUS_EFFECTS_NEGATIVE;
            case "neu_effects", "neutral_effects" -> STATUS_EFFECTS_NEUTRAL;
            case "players" -> ONLINE_PLAYERS;
            case "subtitles" -> SUBTITLES;
            case "target_block_props", "target_block_properties", "tbp" -> {enabled.targetBlock = true; yield TARGET_BLOCK_PROPERTIES;}
            case "target_block_tags", "tbt" -> {enabled.targetBlock = true; yield TARGET_BLOCK_TAGS;}
            case "attributes" -> PLAYER_ATTRIBUTES;
            case "target_entity_attributes", "target_entity_attrs", "teas" -> {enabled.targetEntity = true; yield TARGET_ENTITY_ATTRIBUTES;}
            case "hooked_entity_attributes", "hooked_entity_attrs", "heas" -> HOOKED_ENTITY_ATTRIBUTES;
            case "teams" -> TEAMS;

            default -> null;
        };
    }

    public static HudElement listElement(Supplier<List<?>> supplier, String part, int commaIndex, int profile, int debugLine, ComplexData.Enabled enabled, String original) {
        if (commaIndex == -1)
            return new ListCountElement(supplier);

        List<String> parts = partitionConditional(part);
        System.out.println("SUPPLIER");
        for (String p : parts) {
            System.out.println("`" + p + "`");
        }
        if (parts.size() < 2)
            return null;

        String format = parts.get(1).trim();
        if (!format.startsWith("\"") || !format.endsWith("\"")) {
            Errors.addError(profile, debugLine, original, ErrorType.MALFORMED_CONDITIONAL, null);
            return null;
        }
        format = format.substring(1, format.length()-1);
        System.out.println("Format: " + format);

        return new ListElement(supplier, addElements(format, profile, debugLine, enabled, false, supplier));
    }

    private static HudElement getListAttributeSupplierElement(String name, ComplexData.Enabled enabled, Flags flags, Supplier<?> listSupplier) {
        return switch (name) {
            case "count", "c" -> new NumberSupplierElement(COUNT, flags.scale, flags.precision);
            case "index", "i" -> new NumberSupplierElement(INDEX, flags.scale, flags.precision);
            case "raw" -> new StringSupplierElement(RAW);
            default -> ATTRIBUTE_MAP.get(listSupplier).apply(name, flags);
        };

    }

    
}
