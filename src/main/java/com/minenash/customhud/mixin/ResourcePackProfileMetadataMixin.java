package com.minenash.customhud.mixin;

import com.minenash.customhud.ducks.ResourcePackProfileMetadataDuck;
import net.minecraft.resource.PackVersion;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.util.dynamic.Range;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourcePackProfile.Metadata.class)
public class ResourcePackProfileMetadataMixin implements ResourcePackProfileMetadataDuck {

    @Unique private Range<PackVersion> version = null;

    @Override
    public Range<PackVersion> customhud$getPackVersionRange() {
        return version;
    }

    @Override
    public void customhud$setPackVersionRange(Range<PackVersion> version) {
        this.version = version;
    }
}
