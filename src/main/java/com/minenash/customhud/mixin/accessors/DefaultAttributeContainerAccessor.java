package com.minenash.customhud.mixin.accessors;

import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DefaultAttributeContainer.class)
public interface DefaultAttributeContainerAccessor {

    @Accessor Map<EntityAttribute, EntityAttributeInstance> getInstances();

}
