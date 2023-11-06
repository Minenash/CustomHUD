package com.minenash.customhud.HudElements.methoded;

import com.minenash.customhud.HudElements.FormattedElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.list.ListAttributeSuppliers;
import com.minenash.customhud.HudElements.list.ListSuppliers;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.NumberSupplierElement;
import com.minenash.customhud.HudElements.supplier.SpecialSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.errors.ErrorType;
import com.mojang.datafixers.types.Func;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.HudElements.list.ListSuppliers.ENTRY_ORDERING;

public class TeamElements {

    public static final List<String> NO_FLAGS = List.of("players", "players,");
    public static Pair<HudElement, ErrorType> create(String teamStr, String method, Flags flags, int profile, int debugLine, ComplexData.Enabled enabled, String original) {
        Team team = CLIENT.world.getScoreboard().getTeam(teamStr);
        if (team == null)
            return new Pair<>(null, ErrorType.UNKNOWN_TEAM);

        HudElement element = switch (method) {

            case "", "name" -> new StringSupplierElement(() -> {Team t = get(teamStr); return t == null ? null : t.getDisplayName().getString(); });
            case "id" -> new StringSupplierElement(() -> {Team t = get(teamStr); return t == null ? null : t.getName(); });
            case "friendly_fire" -> new BooleanSupplierElement(() -> {Team t = get(teamStr); return t == null ? null : t.isFriendlyFireAllowed(); });
            case "see_friendly_invisibility", "friendly_invis" -> new BooleanSupplierElement(() -> {Team t = get(teamStr); return t == null ? null : t.shouldShowFriendlyInvisibles(); });
            case "name_tag_visibility", "name_tag" -> new SpecialSupplierElement(SpecialSupplierElement.of(
                    () -> {Team t = get(teamStr); return t == null ? null : t.getNameTagVisibilityRule().getDisplayName().getString(); },
                    () -> {Team t = get(teamStr); return t == null ? null : t.getNameTagVisibilityRule().value; },
                    () -> {Team t = get(teamStr); return t == null ? null : ListAttributeSuppliers.visibleToPlayer(t, t.getNameTagVisibilityRule()); }
            ));
            case "death_msg_visibility", "death_msg" -> new SpecialSupplierElement(SpecialSupplierElement.of(
                    () -> {Team t = get(teamStr); return t == null ? null : t.getDeathMessageVisibilityRule().getDisplayName().getString(); },
                    () -> {Team t = get(teamStr); return t == null ? null : t.getDeathMessageVisibilityRule().value; },
                    () -> {Team t = get(teamStr); return t == null ? null : ListAttributeSuppliers.visibleToPlayer(t, t.getDeathMessageVisibilityRule()); }
            ));
            case "collision" -> new SpecialSupplierElement(SpecialSupplierElement.of(
                    () -> {Team t = get(teamStr); return t == null ? null : t.getCollisionRule().getDisplayName().getString(); },
                    () -> {Team t = get(teamStr); return t == null ? null : t.getCollisionRule().value; },
                    () -> {Team t = get(teamStr); return t == null ? null : t.getCollisionRule() != AbstractTeam.CollisionRule.NEVER; }
            ));
            case "members", "members," -> {
                Supplier<List<?>> supplier = () -> {
                    var a = get(teamStr);
                    return a == null ? Collections.EMPTY_LIST : Arrays.asList(a.getPlayerList().toArray());};
                ListAttributeSuppliers.ATTRIBUTE_MAP.put(supplier, ListAttributeSuppliers.TEAM_MEMBERS);
                String fullText = original.substring(1, original.length()-1);
                yield  VariableParser.listElement(supplier, fullText, fullText.indexOf(','), profile, debugLine, enabled, original);
            }
           case "online_players", "online_players,", "players", "players," -> {
               Supplier<List<?>> supplier = () -> {
                   var a = get(teamStr);
                   return a == null ? Collections.EMPTY_LIST : CLIENT.getNetworkHandler().getPlayerList().stream().filter(p -> p.getScoreboardTeam() == a).sorted(ENTRY_ORDERING).toList();};
               ListAttributeSuppliers.ATTRIBUTE_MAP.put(supplier, ListAttributeSuppliers.PLAYERS);
               String fullText = original.substring(1, original.length()-1);
               yield  VariableParser.listElement(supplier, fullText, fullText.indexOf(','), profile, debugLine, enabled, original);
           }
            default -> null;
        };

        if (element == null)
            return new Pair<>(null, ErrorType.UNKNOWN_ATTRIBUTE_PROPERTY);

        if (flags.anyTextUsed())
            return new Pair<>(new FormattedElement(element, flags), null);

        return new Pair<>(element, null);

    }

    public static Team get(String name) {
        return CLIENT.world.getScoreboard().getTeam(name);
    }

}
