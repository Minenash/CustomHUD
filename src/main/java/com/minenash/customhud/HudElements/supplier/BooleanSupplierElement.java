package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class BooleanSupplierElement implements HudElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean isInDim(Identifier id) { return client.world.getRegistryKey().getValue().equals(id); }
    private static BlockPos blockPos() { return client.getCameraEntity().getBlockPos(); }

    public static final Supplier<Boolean> VSYNC = () -> client.options.getEnableVsync().getValue();

    public static final Supplier<Boolean> CHUNK_CULLING = () -> client.chunkCullingEnabled;
    public static final Supplier<Boolean> IN_OVERWORLD = () -> isInDim(World.OVERWORLD.getValue());
    public static final Supplier<Boolean> IN_NETHER = () -> isInDim(World.NETHER.getValue());
    public static final Supplier<Boolean> IN_END = () -> isInDim(World.END.getValue());

    public static final Supplier<Boolean> SPRINTING = () -> client.player.isSprinting() && !client.player.isSwimming();
    public static final Supplier<Boolean> SNEAKING = () -> client.player.isSneaking();
    public static final Supplier<Boolean> SWIMMING = () -> client.player.isSwimming();
    public static final Supplier<Boolean> ON_GROUND = () -> client.player.isOnGround();

    public static final Supplier<Boolean> IS_SLIME_CHUNK = () -> ChunkRandom.getSlimeRandom(blockPos().getX() >> 4, blockPos().getZ() >> 4, ((StructureWorldAccess)ComplexData.world).getSeed(), 987234911L).nextInt(10) == 0;

    public static final Supplier<Boolean> ITEM_HAS_DURABILITY = () -> client.player.getMainHandStack().getMaxDamage() > 0;
    public static final Supplier<Boolean> OFFHAND_ITEM_HAS_DURABILITY = () -> client.player.getOffHandStack().getMaxDamage() > 0;

    private final Supplier<Boolean> supplier;

    public BooleanSupplierElement(Supplier<Boolean> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getString() {
        return sanitize(supplier, false) ? "true" : "false";
    }

    @Override
    public Number getNumber() {
        return sanitize(supplier, false) ? 1 : 0;
    }

    @Override
    public boolean getBoolean() {
        return sanitize(supplier, false);
    }
}
