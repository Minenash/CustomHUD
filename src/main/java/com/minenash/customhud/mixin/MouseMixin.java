package com.minenash.customhud.mixin;

import com.minenash.customhud.CustomHud;
import net.minecraft.client.Mouse;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void setIsMouseKeyDown(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS)
            CustomHud.IS_MOUSE_DOWN.put(button, true);
        else if (action == GLFW.GLFW_RELEASE)
            CustomHud.IS_MOUSE_DOWN.put(button, false);
    }

}
