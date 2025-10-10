package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import org.apache.commons.lang3.text.WordUtils;
import oshi.hardware.CentralProcessor;

import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.supplier.EntryNumberSuppliers.*;

public class StringSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static Entity cameraEntity() { return client.getCameraEntity(); }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }

    public static final Supplier<String> PROFILE_NAME = () -> ProfileManager.getActive() == null ? null : ProfileManager.getActive().name;

    public static final Supplier<String> VERSION = () -> SharedConstants.getGameVersion().getName();
    public static final Supplier<String> CLIENT_VERSION = client::getGameVersion;
    public static final Supplier<String> MODDED_NAME = ClientBrandRetriever::getClientModName;
    public static final Supplier<String> USERNAME = () -> client.player.getGameProfile().getName() == null ? null : client.player.getGameProfile().getName();
    public static final Supplier<String> UUID = () -> client.player.getGameProfile().getId().toString();

    public static final Supplier<String> SERVER_BRAND = () -> client.player.networkHandler.getBrand();
    public static final Supplier<String> SERVER_NAME = () -> client.getCurrentServerEntry().name;
    public static final Supplier<String> SERVER_ADDRESS = () -> client.getCurrentServerEntry().address;
    public static final Supplier<String> WORLD_NAME = () -> !client.isIntegratedServerRunning() ? null : client.getServer().getSaveProperties().getLevelName();

    public static final Supplier<String> DIMENSION = () -> WordUtils.capitalize(client.world.getRegistryKey().getValue().getPath().replace("_"," "));
    public static final Supplier<String> BIOME = () -> I18n.translate("biome." + client.world.getBiome(blockPos()).getKey().get().getValue().toString().replace(':', '.'));

    private static final String[] moon_phases = new String[]{"full moon", "waning gibbous", "last quarter", "waning crescent", "new moon", "waxing crescent", "first quarter", "waxing gibbous"};
    public static final Supplier<String> MOON_PHASE_WORD = () -> ComplexData.clientChunk.isEmpty() ? null : moon_phases[client.world.getMoonPhase()];

    public static final Supplier<String> TIME_AM_PM = () -> ComplexData.timeOfDay < 12000 ? "am" : "pm";

    public static final Supplier<String> FACING4 = () -> cameraEntity().getHorizontalFacing().getId();
    public static final Supplier<String> FACING4_SHORT = () -> cameraEntity().getHorizontalFacing().getId().substring(0, 1).toUpperCase();
    public static final Supplier<String> FACING_TOWARDS_XZ = () ->
            cameraEntity().getHorizontalFacing() == Direction.EAST || cameraEntity().getHorizontalFacing() == Direction.WEST ? "X" : "Z";

    public static final Supplier<String> FACING8 = () -> {
        float yaw = MathHelper.wrapDegrees(cameraEntity().getYaw());
        if (yaw > 157.5 || yaw < -157.5) return "north";
        if (yaw > 112.5) return "northwest";
        if (yaw > 67.5)  return "west";
        if (yaw > 22.5)  return "southwest";
        if (yaw < -112.5) return "northeast";
        if (yaw < -67.5)  return "east";
        if (yaw < -22.5)  return "southeast";
        return "south";
    };
    public static final Supplier<String> FACING8_SHORT = () -> {
        float yaw = MathHelper.wrapDegrees(cameraEntity().getYaw());
        if (yaw > 157.5 || yaw < -157.5) return "N";
        if (yaw > 112.5) return "NW";
        if (yaw > 67.5)  return "W";
        if (yaw > 22.5)  return "SW";
        if (yaw < -112.5) return "NE";
        if (yaw < -67.5)  return "E";
        if (yaw < -22.5)  return "SE";
        return "S";
    };

    public static final Supplier<String> JAVA_VERSION = () -> System.getProperty("java.version");
    public static final Supplier<String> CPU_NAME = () -> ComplexData.cpu == null ? null : ((CentralProcessor)ComplexData.cpu).getProcessorIdentifier().getName().trim();
    public static final Supplier<String> GPU_NAME = () -> RenderSystem.getDevice().getRenderer();
    public static final Supplier<String> GPU_VENDOR = () -> RenderSystem.getDevice().getVendor();
    public static final Supplier<String> GL_VERSION = () -> RenderSystem.getDevice().getVersion().substring(0, RenderSystem.getDevice().getVersion().indexOf(' '));
    public static final Supplier<String> GPU_DRIVER = () -> RenderSystem.getDevice().getVersion().substring(RenderSystem.getDevice().getVersion().indexOf(' ') + 1);

    public static final Supplier<String> MUSIC_NAME = () -> MusicAndRecordTracker.isMusicPlaying ? MusicAndRecordTracker.musicName : null;

    public static final Supplier<String> BIOME_BUILDER_PEAKS = () -> isNoise() ? VanillaBiomeParameters.getPeaksValleysDescription(DensityFunctions.getPeaksValleysNoise((float)sample(sampler().ridges()))) : null;
    public static final Supplier<String> BIOME_BUILDER_CONTINENTS = () -> isNoise() ? par.getContinentalnessDescription(sample(sampler().continents())) : null;

    public static final Supplier<String> VILLAGER_BIOME = () -> ComplexData.targetEntity instanceof VillagerEntity ve ? WordUtils.capitalize(ve.getVillagerData().type().toString()) : null;
    public static final Supplier<String> VILLAGER_LEVEL_WORD = () -> ComplexData.targetEntity instanceof VillagerEntity ve ? I18n.translate("merchant.level." + ve.getVillagerData().level()) : null;


    private final Supplier<String> supplier;

    public StringSupplierElement(Supplier<String> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getString() {
        return sanitize(supplier, "-");
    }

    @Override
    public Number getNumber() {
        try {
            return supplier.get().length();
        }
        catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean getBoolean() {
        return getNumber().intValue() > 0;
    }
}
