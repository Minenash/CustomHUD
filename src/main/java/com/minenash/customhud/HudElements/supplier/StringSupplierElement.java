package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.mojang.blaze3d.platform.GlDebugInfo;
import net.minecraft.SharedConstants;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.biome.source.util.VanillaBiomeParameters;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import org.apache.commons.lang3.text.WordUtils;

import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.supplier.EntryNumberSuppliers.*;

public class StringSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static Entity cameraEntity() { return client.getCameraEntity(); }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }
    private static Entity hooked() {return client.player.fishHook == null ? null : client.player.fishHook.getHookedEntity();}

    public static final Supplier<String> VERSION = () -> SharedConstants.getGameVersion().getName();
    public static final Supplier<String> CLIENT_VERSION = client::getGameVersion;
    public static final Supplier<String> MODDED_NAME = ClientBrandRetriever::getClientModName;
    public static final Supplier<String> DISPLAY_NAME = () -> client.player.getDisplayName().getString();
    public static final Supplier<String> USERNAME = () -> client.player.getGameProfile().getName() == null ? null : client.player.getGameProfile().getName();
    public static final Supplier<String> UUID = () -> client.player.getGameProfile().getId().toString();
    public static final Supplier<String> PLAYER_TEAM = () -> client.player.getScoreboardTeam().getName();

    public static final Supplier<String> SERVER_BRAND = () -> client.player.networkHandler.getBrand();
    public static final Supplier<String> SERVER_NAME = () -> client.getCurrentServerEntry().name;
    public static final Supplier<String> SERVER_ADDRESS = () -> client.getCurrentServerEntry().address;
    public static final Supplier<String> WORLD_NAME = () -> !client.isIntegratedServerRunning() ? null : client.getServer().getSaveProperties().getLevelName();

    public static final Supplier<String> DIMENSION = () -> WordUtils.capitalize(client.world.getRegistryKey().getValue().getPath().replace("_"," "));
    public static final Supplier<String> DIMENSION_ID = () -> client.world.getRegistryKey().getValue().toString();
    public static final Supplier<String> BIOME = () -> I18n.translate("biome." + client.world.getBiome(blockPos()).getKey().get().getValue().toString().replace(':', '.'));
    public static final Supplier<String> BIOME_ID = () -> client.world.getBiome(blockPos()).getKey().get().getValue().toString();

    private static final String[] moon_phases = new String[]{"full moon", "waning gibbous", "last quarter", "waning crescent", "new moon", "waxing crescent", "first quarter", "waxing gibbous"};
    public static final Supplier<String> MOON_PHASE_WORD = () -> ComplexData.clientChunk.isEmpty() ? null : moon_phases[client.world.getMoonPhase()];

    public static final Supplier<String> TIME_AM_PM = () -> ComplexData.timeOfDay < 12000 ? "am" : "pm";

    public static final Supplier<String> FACING = () -> cameraEntity().getHorizontalFacing().getName();
    public static final Supplier<String> FACING_SHORT = () -> cameraEntity().getHorizontalFacing().getName().substring(0, 1).toUpperCase();
    public static final Supplier<String> FACING_TOWARDS_XZ = () ->
            cameraEntity().getHorizontalFacing() == Direction.EAST || cameraEntity().getHorizontalFacing() == Direction.WEST ? "X" : "Z";

    public static final Supplier<String> JAVA_VERSION = () -> System.getProperty("java.version");
    public static final Supplier<String> CPU_NAME = () -> ComplexData.cpu.getProcessorIdentifier().getName();
    public static final Supplier<String> GPU_NAME = () -> (Util.getOperatingSystem() == Util.OperatingSystem.WINDOWS ? GlDebugInfo.getRenderer() : GlDebugInfo.getRenderer().substring(0, GlDebugInfo.getRenderer().indexOf("("))).trim();

    public static final Supplier<String> MUSIC_ID = () -> MusicAndRecordTracker.isMusicPlaying ? MusicAndRecordTracker.musicId : null;
    public static final Supplier<String> MUSIC_NAME = () -> MusicAndRecordTracker.isMusicPlaying ? MusicAndRecordTracker.musicName : null;
    public static final Supplier<String> RECORD_NAME = () -> MusicAndRecordTracker.isRecordPlaying ? MusicAndRecordTracker.recordName : null;
    public static final Supplier<String> RECORD_ID = () -> MusicAndRecordTracker.isRecordPlaying ? MusicAndRecordTracker.recordId : null;

    public static final Supplier<String> BIOME_BUILDER_PEAKS = () -> isNoise() ? VanillaBiomeParameters.getPeaksValleysDescription(DensityFunctions.getPeaksValleysNoise((float)sample(sampler().ridges()))) : null;
    public static final Supplier<String> BIOME_BUILDER_CONTINENTS = () -> isNoise() ? par.getContinentalnessDescription(sample(sampler().continents())) : null;

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
