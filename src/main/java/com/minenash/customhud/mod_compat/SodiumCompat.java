package com.minenash.customhud.mod_compat;

import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
import me.jellysquid.mods.sodium.client.SodiumClientMod;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;

import java.util.List;
import java.util.function.Supplier;

import static com.minenash.customhud.mod_compat.CustomHudRegistry.registerElement;
import static com.minenash.customhud.mod_compat.CustomHudRegistry.registerComplexData;

public class SodiumCompat {

    public static void registerCompat() {

        registerElement("sodium_version", (_str) -> new StringSupplierElement(SodiumClientMod::getVersion));

    }


}
