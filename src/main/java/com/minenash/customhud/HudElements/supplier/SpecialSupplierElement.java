package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.text.WordUtils;

import java.util.function.Supplier;

import static com.minenash.customhud.ProfileManager.getActive;

public class SpecialSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean isFacingEastOrSouth() {
        Direction dir = client.getCameraEntity().getHorizontalFacing();
        return dir == Direction.EAST || dir == Direction.SOUTH;
    }

    public static final Entry DIFFICULTY = of( () -> client.world.getDifficulty().getName(),
                                               () -> client.world.getDifficulty().getId(),
                                               () -> client.world.getDifficulty().getId() != 0);

    public static final Entry MAX_FPS = of( () -> client.options.getMaxFps().getValue() == GameOptions.MAX_FPS_LIMIT ? null : client.options.getMaxFps().getValue().toString(),
                                            () ->  client.options.getMaxFps().getValue(),
                                            () -> client.options.getMaxFps().getValue() == GameOptions.MAX_FPS_LIMIT);

    public static final Entry PROFILE_KEYBIND = of( () -> getActive() == null ? "" : getActive().keyBinding.getBoundKeyLocalizedText().getString(),
                                                    () -> getActive() == null ? 0 : getActive().keyBinding.boundKey.getCode(),
                                                    () -> getActive() != null && !getActive().keyBinding.isUnbound());

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

    public static final Entry TARGET_FLUID = of( () -> WordUtils.capitalize(Registries.FLUID.getId(ComplexData.targetFluid.getFluid()).getPath().replace('_',' ')),
                                                 () -> Fluid.STATE_IDS.getRawId(ComplexData.targetFluid),
                                                 () -> !ComplexData.targetFluid.isEmpty());

    public static final Entry ITEM_OLD = of( () -> I18n.translate(client.player.getMainHandStack().getItem().getTranslationKey()),
                                         () -> Item.getRawId(client.player.getMainHandStack().getItem()),
                                         () -> !client.player.getMainHandStack().isEmpty());

    public static final Entry ITEM_NAME = of( () -> client.player.getMainHandStack().getName().getString(),
            () -> client.player.getMainHandStack().getName().getString().length(),
            () -> !client.player.getMainHandStack().isEmpty());

    @Deprecated
    public static final Entry OFFHAND_ITEM = of( () -> I18n.translate(client.player.getOffHandStack().getItem().getTranslationKey()),
                                                 () -> Item.getRawId(client.player.getOffHandStack().getItem()),
                                                 () -> !client.player.getOffHandStack().isEmpty());
    @Deprecated
    public static final Entry OFFHAND_ITEM_NAME = of( () -> client.player.getOffHandStack().getName().getString(),
                                                      () -> client.player.getOffHandStack().getName().getString().length(),
                                                      () -> !client.player.getOffHandStack().isEmpty());

    public static final Entry GRAPHICS_MODE = of( () -> client.options.getGraphicsMode().getValue().toString(),
                                                  () -> client.options.getGraphicsMode().getValue() == GraphicsMode.FAST ? 0 : (client.options.getGraphicsMode().getValue() == GraphicsMode.FANCY ? 1 : 2),
                                                  () -> true);

    public static final Entry CLOUDS = of( () -> client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? "off" : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? "fast" : "fancy"),
                                           () -> client.options.getCloudRenderMode().getValue() == CloudRenderMode.OFF ? 0 : (client.options.getCloudRenderMode().getValue() == CloudRenderMode.FAST ? 1 : 2),
                                           () -> client.options.getCloudRenderMode().getValue() != CloudRenderMode.OFF);

    public static final Entry GAMEMODE = of ( () -> client.interactionManager.getCurrentGameMode().getName(),
                                              () -> client.interactionManager.getCurrentGameMode().getId(),
                                              () -> true);

    public static final Entry FACING_TOWARDS_PN_WORD = of( () -> isFacingEastOrSouth() ? "positive" : "negative",
            () -> isFacingEastOrSouth() ? 1 : 0,
            SpecialSupplierElement::isFacingEastOrSouth);

    public static final Entry FACING_TOWARDS_PN_SIGN = of( () -> isFacingEastOrSouth() ? "+" : "-",
            () -> isFacingEastOrSouth() ? 1 : 0,
            SpecialSupplierElement::isFacingEastOrSouth);

    public static final Entry ACTIVE_RENDERER = of( () -> {var r = renderer(); return r != null ? r.getClass().getSimpleName() : "none (vanilla)";},
                                                    () -> {var r = renderer(); return r != null ? r.getClass().getSimpleName().length() : 7;},
                                                    () -> renderer() != null);

    public static Renderer renderer() { try {return Renderer.get();} catch (Exception e) { return null;}}

    public static final Entry CAMERA_PERSPECTIVE = of (
            () -> switch (client.options.getPerspective()) {
                case FIRST_PERSON -> "First Person";
                case THIRD_PERSON_BACK -> "Third Person (Back)";
                case THIRD_PERSON_FRONT -> "Third Person (Front)";
            },
            () -> client.options.getPerspective().ordinal(),
            () -> client.options.getPerspective().ordinal() != 0
    );


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
