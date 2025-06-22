package com.minenash.customhud.mixin.accessors;

import net.minecraft.component.type.BlockPredicatesComponent;
import net.minecraft.predicate.BlockPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BlockPredicatesComponent.class)
public interface BlockPredicatesComponentAccessor {

    @Accessor List<BlockPredicate> getPredicates();

}
