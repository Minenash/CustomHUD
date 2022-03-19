package com.minenash.customhud.mod_compat;

import net.fabricmc.loader.api.FabricLoader;

public class BuiltInModCompat {

    public static void register() {

        if (has("sodium")) SodiumCompat.registerCompat();
        if (has("iris")) IrisCompat.registerCompat();

    }

    private static boolean has(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

}
