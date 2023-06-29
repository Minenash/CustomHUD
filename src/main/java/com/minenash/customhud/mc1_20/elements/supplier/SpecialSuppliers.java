package com.minenash.customhud.mc1_20.elements.supplier;

import com.minenash.customhud.mc1_20.ComplexData;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.text.WordUtils;

import static com.minenash.customhud.core.elements.SupplierElements.Special.*;

public class SpecialSuppliers {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean isFacingEastOrSouth() {
        Direction dir = client.getCameraEntity().getHorizontalFacing();
        return dir == Direction.EAST || dir == Direction.SOUTH;
    }

    public static final Entry TIME_HOUR_24 = of( () -> String.format("%02d", ComplexData.timeOfDay / 1000),
                                                 () -> ComplexData.timeOfDay / 1000,
                                                 () -> ComplexData.timeOfDay / 1000 >= 12);

    public static final Entry TIME_MINUTES = of( () -> String.format("%02d",(int)((ComplexData.timeOfDay % 1000) / (1000/60F))),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)),
                                                () -> (int)((ComplexData.timeOfDay % 1000) / (1000/60F)) != 0);

    public static final Entry TIME_SECONDS = of( () -> String.format("%02d",(int)((ComplexData.timeOfDay % 1000) % (1000/60F) * 3.6F)),
            () -> (int)((ComplexData.timeOfDay % 1000) % (1000/60F) * 3.6F),
            () -> (int)((ComplexData.timeOfDay % 1000) % (1000/60F) * 3.6F) != 0);

    public static final Entry TARGET_BLOCK = of( () -> I18n.translate(ComplexData.targetBlock.getBlock().getTranslationKey()),
                                                 () -> Block.getRawIdFromState(ComplexData.targetBlock),
                                                 () -> !ComplexData.targetBlock.isAir());

    public static final Entry TARGET_BLOCK_ID = of( () -> Registries.BLOCK.getId(ComplexData.targetBlock.getBlock()).toString(),
                                                    () -> Block.getRawIdFromState(ComplexData.targetBlock),
                                                    () -> !ComplexData.targetBlock.isAir());

    public static final Entry TARGET_FLUID = of( () -> WordUtils.capitalize(Registries.FLUID.getId(ComplexData.targetFluid.getFluid()).getPath().replace('_',' ')),
                                                 () -> Fluid.STATE_IDS.getRawId(ComplexData.targetFluid),
                                                 () -> !ComplexData.targetFluid.isEmpty());

    public static final Entry TARGET_FLUID_ID = of( () -> Registries.FLUID.getId(ComplexData.targetFluid.getFluid()).toString(),
                                                    () -> Fluid.STATE_IDS.getRawId(ComplexData.targetFluid),
                                                    () -> !ComplexData.targetFluid.isEmpty());

    public static final Entry ITEM = of( () -> I18n.translate(client.player.getMainHandStack().getItem().getTranslationKey()),
                                         () -> Item.getRawId(client.player.getMainHandStack().getItem()),
                                         () -> !client.player.getMainHandStack().isEmpty());

    public static final Entry ITEM_NAME = of( () -> client.player.getMainHandStack().getName().getString(),
            () -> client.player.getMainHandStack().getName().getString().length(),
            () -> !client.player.getMainHandStack().isEmpty());

    @Deprecated
    public static final Entry ITEM_ID = of( () -> Registries.ITEM.getId(client.player.getMainHandStack().getItem()).toString(),
                                            () -> Item.getRawId(client.player.getMainHandStack().getItem()),
                                            () -> !client.player.getMainHandStack().isEmpty());
    @Deprecated
    public static final Entry OFFHAND_ITEM = of( () -> I18n.translate(client.player.getOffHandStack().getItem().getTranslationKey()),
                                                 () -> Item.getRawId(client.player.getOffHandStack().getItem()),
                                                 () -> !client.player.getOffHandStack().isEmpty());
    @Deprecated
    public static final Entry OFFHAND_ITEM_NAME = of( () -> client.player.getOffHandStack().getName().getString(),
                                                      () -> client.player.getOffHandStack().getName().getString().length(),
                                                      () -> !client.player.getOffHandStack().isEmpty());
    @Deprecated
    public static final Entry OFFHAND_ITEM_ID = of( () -> Registries.ITEM.getId(client.player.getOffHandStack().getItem()).toString(),
                                                    () -> Item.getRawId(client.player.getOffHandStack().getItem()),
                                                    () -> !client.player.getOffHandStack().isEmpty());

    public static final Entry GRAPHICS_MODE = of( () -> client.options.getGraphicsMode().getValue().toString(),
                                                  () -> client.options.getGraphicsMode().getValue() == GraphicsMode.FAST ? 0 : (client.options.getGraphicsMode().getValue() == GraphicsMode.FANCY ? 1 : 2),
                                                  () -> true);

    public static final Entry CLOUDS = of( () -> client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "off" : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? "fast" : "fancy"),
                                           () -> client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? 0 : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? 1 : 2),
                                           () -> client.options.getCloudRenderMode().getValue() != CloudRenderMode.OFF);

    public static final Entry FACING_TOWARDS_PN_WORD = of( () -> isFacingEastOrSouth() ? "positive" : "negative",
            () -> isFacingEastOrSouth() ? 1 : 0,
            SpecialSuppliers::isFacingEastOrSouth);

    public static final Entry FACING_TOWARDS_PN_SIGN = of( () -> isFacingEastOrSouth() ? "+" : "-",
            () -> isFacingEastOrSouth() ? 1 : 0,
            SpecialSuppliers::isFacingEastOrSouth);

}