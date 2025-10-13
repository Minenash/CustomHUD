package com.minenash.customhud.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.minenash.customhud.ducks.ResourcePackProfileMetadataDuck;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.PackResourceMetadata;
import net.minecraft.util.dynamic.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ResourcePackProfile.class)
public class ResourcePackProfileMixin {

    @Unique private static Range<PackVersion> temp = null;

    @WrapOperation(method = "loadMetadata", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/metadata/PackResourceMetadata;supportedFormats()Lnet/minecraft/util/dynamic/Range;"))
    private static Range<PackVersion> setPackVersionPart1(PackResourceMetadata instance, Operation<Range<PackVersion>> original) {
        return temp = original.call(instance);
    }

    @Inject(method = "loadMetadata", at = @At("RETURN"))
    private static void setPackVersionPart2(ResourcePackInfo info, ResourcePackProfile.PackFactory packFactory, PackVersion version, ResourceType type, CallbackInfoReturnable<ResourcePackProfile.Metadata> cir) {
        ResourcePackProfileMetadataDuck value = ((ResourcePackProfileMetadataDuck)(Object)cir.getReturnValue());
        if (value != null)
            value.customhud$setPackVersionRange( temp );
    }
}
