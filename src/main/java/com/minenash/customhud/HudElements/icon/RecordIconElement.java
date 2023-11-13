package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.complex.MusicAndRecordTracker;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RecordIconElement extends IconElement {

    private static final ItemStack NO_RECORD = new ItemStack(Items.BARRIER);

    public RecordIconElement(Flags flags) {
        super(flags, 11);
    }

    @Override
    public Number getNumber() {
        return MusicAndRecordTracker.isRecordPlaying ? Item.getRawId(MusicAndRecordTracker.recordIcon.getItem()) : 0;
    }

    @Override
    public boolean getBoolean() {
        return MusicAndRecordTracker.isRecordPlaying && MusicAndRecordTracker.recordIcon.isEmpty();
    }

    public void render(DrawContext context, int x, int y, float profileScale) {
        renderItemStack(context, x, y, MusicAndRecordTracker.isRecordPlaying ? MusicAndRecordTracker.recordIcon : NO_RECORD);
    }

}
