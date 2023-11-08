package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.mixin.accessors.AttributeContainerAccessor;
import com.minenash.customhud.mixin.accessors.DefaultAttributeContainerAccessor;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;
import java.util.function.Function;

import static com.minenash.customhud.CustomHud.CLIENT;

public class AttributeHelpers {

    public static final Function<String, EntityAttribute> ENTITY_ATTR_READER = (src) -> Registries.ATTRIBUTE.get(Identifier.tryParse(src));


    public static Entity getFullEntity(Entity entity) {
        return CLIENT.getServer() == null || entity == null? entity :
                CLIENT.getServer().getWorld(entity.getWorld().getRegistryKey()).getEntity(entity.getUuid());
    }
    public static EntityAttributeInstance getEntityAttr(Entity entity, EntityAttribute attribute) {
        Entity e = getFullEntity(entity);
        if (!(e instanceof LivingEntity le)) return null;
        return le.getAttributeInstance(attribute);
    }

    public static List<EntityAttributeInstance> getEntityAttributes(Entity entity) {
        entity = getFullEntity(entity);
        if (!(entity instanceof LivingEntity le) ) return Collections.EMPTY_LIST;
        AttributeContainerAccessor container = (AttributeContainerAccessor) le.getAttributes();
        Map<EntityAttribute, EntityAttributeInstance> instances = new HashMap<>(((DefaultAttributeContainerAccessor)container.getFallback()).getInstances());
        instances.putAll(container.getCustom());
        return (entity.getWorld().isClient ?
                instances.values().stream().filter(a -> a.getAttribute().isTracked()) : instances.values().stream())
                .sorted(Comparator.comparing(a -> I18n.translate(a.getAttribute().getTranslationKey()))).toList();
    }


}
