package com.minenash.customhud.mc1_20.elements.stats;

import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.elements.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.stat.Stat;
import net.minecraft.util.Identifier;

public class CustomStatElement implements HudElement {

    private final Stat<Identifier> stat;
    private final Flags flags;

    public CustomStatElement(Stat<Identifier> stat, Flags flags) {
        this.stat = stat;
        this.flags = flags;
        if (flags.precision == -1)
            flags.precision = 0;
    }

    private int get() {
        return MinecraftClient.getInstance().player.getStatHandler().getStat(stat);
    }

    @Override
    public String getString() {
        return flags.formatted ? stat.format(get()) : String.format("%."+ flags.precision +"f", get() * flags.scale);
    }

    @Override
    public Number getNumber() {
        return get();
    }

    @Override
    public boolean getBoolean() {
        return get() > 0;
    }
}
