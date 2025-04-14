package com.minenash.customhud.v5.elements;

import net.minecraft.util.math.MathHelper;

import static com.minenash.customhud.v5.ElementHelpers.*;
import static com.minenash.customhud.v5.ElementRegistry.*;

public class DirectionElements {

    public static void load() {
        register( "yaw", 1, () -> MathHelper.wrapDegrees(camera().getYaw()) );
        register( "pitch", 1, () -> MathHelper.wrapDegrees(camera().getPitch()) );
    }

}
