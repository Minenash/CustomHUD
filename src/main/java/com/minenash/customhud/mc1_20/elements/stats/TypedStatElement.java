package com.minenash.customhud.mc1_20.elements.stats;

import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.elements.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.stat.StatType;

public class TypedStatElement<T> implements HudElement {

    private final StatType<T> type;
    private final T entry;
    private final Flags flags;

    public TypedStatElement(StatType<T> type, T entry, Flags flags) {
        this.type = type;
        this.entry = entry;
        this.flags = flags;
        if (flags.precision == -1)
            flags.precision = 0;
    }

    private int get() {
        return type.hasStat(entry) ? MinecraftClient.getInstance().player.getStatHandler().getStat(type.getOrCreateStat(entry)) : 0;
    }

    @Override
    public String getString() {
        if (!type.hasStat(entry))
            return "0";

        int value =  MinecraftClient.getInstance().player.getStatHandler().getStat(type, entry);
        return flags.formatted ? type.getOrCreateStat(entry).format(value) : String.format("%."+ flags.precision +"f", value * flags.scale);
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
