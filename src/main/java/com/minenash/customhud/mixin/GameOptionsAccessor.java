package com.minenash.customhud.mixin;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.client.option.GameOptions;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {

    @Invoker("accept")
    void invokeAccept(GameOptions.Visitor visitor);

    @Accessor
    Object2FloatMap<SoundCategory> getSoundVolumeLevels();

}
