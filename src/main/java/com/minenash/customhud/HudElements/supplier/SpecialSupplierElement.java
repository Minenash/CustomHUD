package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.function.Supplier;

public class SpecialSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static final Entry TIME_HOUR_24 = of( () -> String.format("%02d", ComplexData.timeOfDay / 1000),
                                                 () -> ComplexData.timeOfDay / 1000,
                                                 () -> ComplexData.timeOfDay / 1000 >= 12);

    public static final Entry TIME_MINUTE = of( () -> String.format("%02d",(int)((ComplexData.timeOfDay % 1000) / (1000/60F))),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)) != 0);

    public static final Entry TARGET_BLOCK = of( () -> I18n.translate(ComplexData.targetBlock.getBlock().getTranslationKey()),
                                                 () -> Block.getRawIdFromState(ComplexData.targetBlock),
                                                 () -> !ComplexData.targetBlock.isAir());

    public static final Entry TARGET_BLOCK_ID = of( () -> Registry.BLOCK.getId(ComplexData.targetBlock.getBlock()).toString(),
                                                    () -> Block.getRawIdFromState(ComplexData.targetBlock),
                                                    () -> !ComplexData.targetBlock.isAir());

    public static final Entry TARGET_FLUID = of( () -> WordUtils.capitalize(Registry.FLUID.getId(ComplexData.targetFluid.getFluid()).getPath().replace('_',' ')),
                                                 () -> Fluid.STATE_IDS.getRawId(ComplexData.targetFluid),
                                                 () -> !ComplexData.targetFluid.isEmpty());

    public static final Entry TARGET_FLUID_ID = of( () -> Registry.FLUID.getId(ComplexData.targetFluid.getFluid()).toString(),
                                                    () -> Fluid.STATE_IDS.getRawId(ComplexData.targetFluid),
                                                    () -> !ComplexData.targetFluid.isEmpty());

    public static final Entry ITEM = of( () -> I18n.translate(client.player.getMainHandStack().getItem().getTranslationKey()),
                                         () -> Item.getRawId(client.player.getMainHandStack().getItem()),
                                         () -> !client.player.getMainHandStack().isEmpty());

    public static final Entry ITEM_ID = of( () -> Registry.ITEM.getId(client.player.getMainHandStack().getItem()).toString(),
                                            () -> Item.getRawId(client.player.getMainHandStack().getItem()),
                                            () -> !client.player.getMainHandStack().isEmpty());

    public static final Entry OFFHAND_ITEM = of( () -> I18n.translate(client.player.getOffHandStack().getItem().getTranslationKey()),
                                                 () -> Item.getRawId(client.player.getOffHandStack().getItem()),
                                                 () -> !client.player.getOffHandStack().isEmpty());

    public static final Entry OFFHAND_ITEM_ID = of( () -> Registry.ITEM.getId(client.player.getOffHandStack().getItem()).toString(),
                                                    () -> Item.getRawId(client.player.getOffHandStack().getItem()),
                                                    () -> !client.player.getOffHandStack().isEmpty());

    public static final Entry CLOUDS = of( () -> client.options.cloudRenderMode == CloudRenderMode.OFF ? "off" : (client.options.cloudRenderMode == CloudRenderMode.FAST ? "fast-clouds" : "fancy-clouds"),
                                           () -> client.options.cloudRenderMode == CloudRenderMode.OFF ? 0 : (client.options.cloudRenderMode == CloudRenderMode.FAST ? 1 : 2),
                                           () -> client.options.cloudRenderMode != CloudRenderMode.OFF);

    public record Entry(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {}
    public static Entry of(Supplier<String> stringSupplier, Supplier<Number> numberSupplier, Supplier<Boolean> booleanSupplier) {
        return new Entry(stringSupplier, numberSupplier, booleanSupplier);
    }

    private final Entry entry;

    public SpecialSupplierElement(Entry entry) {
        this.entry = entry;
    }

    @Override
    public String getString() {
        return sanitize(entry.stringSupplier, "-");
    }

    @Override
    public Number getNumber() {
        return sanitize(entry.numberSupplier, Double.NaN);
    }

    @Override
    public boolean getBoolean() {
        return sanitize(entry.booleanSupplier, false);
    }

}
