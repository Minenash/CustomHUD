package com.minenash.customhud.v5;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ElementHelpers {

    public static Entity camera() { return CLIENT.getCameraEntity(); }
    public static boolean inNether() { return CLIENT.world.getRegistryKey().getValue().equals(World.NETHER.getValue()); }
    public static BlockPos blockPos() { return CLIENT.getCameraEntity().getBlockPos(); }
}
