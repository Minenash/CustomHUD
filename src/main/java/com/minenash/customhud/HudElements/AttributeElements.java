package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.list.ListAttributeSuppliers;
import com.minenash.customhud.HudElements.list.ListSuppliers;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.NumberSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.errors.ErrorType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.CustomHud.CLIENT;

public class AttributeElements {

    public static final Supplier<Entity> PLAYER = () -> CLIENT.player;
    public static final Supplier<Entity> TARGET_ENTITY = () -> ComplexData.targetEntity;
    public static final Supplier<Entity> HOOKED_ENTITY = () -> CLIENT.player.fishHook == null ? null : CLIENT.player.fishHook.getHookedEntity();

    public static final List<String> NO_FLAGS = List.of("modifiers", "modifiers,");
    public static Pair<HudElement, ErrorType> create(Supplier<Entity> entity, String attribute, String method, Flags flags, int profile, int debugLine, ComplexData.Enabled enabled, String original) {
        EntityAttribute attr = Registries.ATTRIBUTE.get(Identifier.tryParse(attribute));
        if (attr == null)
            return new Pair<>(null, ErrorType.UNKNOWN_ATTRIBUTE);

        Supplier<EntityAttributeInstance> attrS = () -> {
            Entity e = ListSuppliers.getFullEntity(entity.get());
            if (!(e instanceof LivingEntity le)) return null;
            return le.getAttributeInstance(attr);
        };

        HudElement element = switch (method) {
            case "", "name" -> new StringSupplierElement( () ->  I18n.translate(attr.getTranslationKey()));
            case "tracked" -> new BooleanSupplierElement(attr::isTracked);
            case "default_value" -> new NumberSupplierElement(attr::getDefaultValue, flags.scale, flags.precision);
            case "base_value" -> new NumberSupplierElement( () -> {var a = attrS.get(); return a == null ? null : attrS.get().getBaseValue();}, flags.scale, flags.precision);
            case "value" -> new NumberSupplierElement( () -> {var a = attrS.get(); return a == null ? null : attrS.get().getValue();}, flags.scale, flags.precision);
            case "modifiers", "modifiers," -> {
                Supplier<List<?>> supplier = () -> {
                    var a = attrS.get();
                    return a == null ? Collections.EMPTY_LIST : a.getModifiers().stream().toList();};
                ListAttributeSuppliers.ATTRIBUTE_MAP.put(supplier, ListAttributeSuppliers.ATTRIBUTE_MODIFIERS_CHILDREN);
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

}
