package com.minenash.customhud.HudElements.list;

import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.minenash.customhud.CustomHud.CLIENT;

public class ListSuppliers {

    public static final Supplier<List<Object>>
        STATUS_EFFECTS = () -> Arrays.asList(CLIENT.player.getStatusEffects().toArray());

}
