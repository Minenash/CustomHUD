package com.minenash.customhud.mixin.accessors;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {

    @Invoker("accept") void invokeAccept(GameOptions.Visitor visitor);

    @Accessor Map<SoundCategory, SimpleOption<Double>> getSoundVolumeLevels();

}
