package com.minenash.customhud.mixin;

import com.minenash.customhud.ducks.ResourcePackProfileMetadataDuck;
import net.minecraft.resource.ResourcePackProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ResourcePackProfile.Metadata.class)
public class ResourcePackProfileMetadataMixin implements ResourcePackProfileMetadataDuck {

    @Unique private int version = 0;

    @Override
    public int customhud$getPackVersion() {
        return version;
    }

    @Override
    public void customhud$setPackVersion(int version) {
        this.version = version;
    }
}
