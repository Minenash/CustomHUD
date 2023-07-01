package com.minenash.customhud.mc1_20.mixin;

import com.minenash.customhud.core.ProfileHandler;
import net.minecraft.client.Keyboard;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void updateToggles(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action == GLFW.GLFW_PRESS)
            ProfileHandler.toggleKey(scancode);

        System.out.println("Scan: " + scancode + ", Key: " + key + ", Name:" + GLFW.glfwGetKeyName(key, scancode));
    }

    @Inject(method = "onChar", at = @At("HEAD"))
    private void updateToggles(long window, int codePoint, int modifiers, CallbackInfo ci) {
        codePoint = Character.toUpperCase(codePoint);
        ProfileHandler.toggleKey(codePoint);

        System.out.println("Code: " + codePoint + ", Chars: " + Arrays.toString(Character.toChars(codePoint)));
    }

}
