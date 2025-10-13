package com.minenash.customhud.ducks;

import net.minecraft.resource.PackVersion;
import net.minecraft.util.dynamic.Range;

public interface ResourcePackProfileMetadataDuck {

    Range<PackVersion> customhud$getPackVersionRange();
    void customhud$setPackVersionRange(Range<PackVersion> version);

}
