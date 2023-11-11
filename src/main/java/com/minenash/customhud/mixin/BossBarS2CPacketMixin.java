package com.minenash.customhud.mixin;

import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.complex.ServerBossBarManager;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BossBarS2CPacket.class)
public class BossBarS2CPacketMixin {

    @Inject(method = "add", at = @At("HEAD"))
    private static void addBossBar(BossBar bar, CallbackInfoReturnable<BossBarS2CPacket> cir) {
        if (!(bar instanceof CommandBossBar))
            ComplexData.bossbars.put(bar.getUuid(), bar);
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private static void removeBossBar(UUID uuid, CallbackInfoReturnable<BossBarS2CPacket> cir) {
        ComplexData.bossbars.remove(uuid);
    }

}
