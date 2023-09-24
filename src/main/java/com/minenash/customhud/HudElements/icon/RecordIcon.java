package com.minenash.customhud.HudElements.icon;

import com.minenash.customhud.MusicAndRecordTracker;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;

public class RecordIcon extends IconElement {

    private final int width;

    public RecordIcon(Flags flags) {
        super(flags);
        this.width = flags.iconWidth != -1 ? flags.iconWidth : (int)(11*scale);
    }

    @Override
    public Number getNumber() {
        return Item.getRawId(MusicAndRecordTracker.recordIcon.getItem());
    }

    @Override
    public boolean getBoolean() {
        return MusicAndRecordTracker.recordIcon.isEmpty();
    }

    @Override
    public int getTextWidth() {
        return width;
    }

    public void render(DrawContext context, int x, int y, float profileScale) {
        renderItemStack(x+shiftX, y+shiftY, profileScale, MusicAndRecordTracker.recordIcon);
    }

}
