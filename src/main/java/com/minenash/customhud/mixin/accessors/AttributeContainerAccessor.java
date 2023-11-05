package com.minenash.customhud.mixin.accessors;

import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(AttributeContainer.class)
public interface AttributeContainerAccessor {

    @Accessor Map<EntityAttribute, EntityAttributeInstance> getCustom();
    @Accessor DefaultAttributeContainer getFallback();

}
