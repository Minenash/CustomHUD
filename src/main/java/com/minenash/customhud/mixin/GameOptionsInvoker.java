package com.minenash.customhud.mixin;

import net.minecraft.client.option.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameOptions.class)
public interface GameOptionsInvoker {

    @Invoker("accept")
    void invokeAccept(GameOptions.Visitor visitor);

}
