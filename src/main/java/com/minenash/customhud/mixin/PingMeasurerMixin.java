package com.minenash.customhud.mixin;

import net.minecraft.client.network.PingMeasurer;
import net.minecraft.network.packet.s2c.query.PingResultS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PingMeasurer.class)
public class PingMeasurerMixin {

    @Inject(method = "onPingResult", at = @At("HEAD"))
    public void getPing(PingResultS2CPacket packet, CallbackInfo ci) {
//        ComplexData.ping = (int) (Util.getMeasuringTimeMs() - packet.getStartTime());
    }

}
