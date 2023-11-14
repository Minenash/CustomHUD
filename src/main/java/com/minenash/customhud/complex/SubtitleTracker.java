package com.minenash.customhud.complex;

import com.google.common.collect.Lists;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.minenash.customhud.CustomHud.CLIENT;

public class SubtitleTracker implements SoundInstanceListener {

    public static final SubtitleTracker INSTANCE = new SubtitleTracker();

    public final List<SubtitlesHud.SubtitleEntry> entries = Lists.newArrayList();

    private boolean enabled = false;

    public void setEnable(boolean enable) {
        if (enable) tick();
        if (this.enabled == enable) return;
        this.enabled = enable;
        if (enable) CLIENT.getSoundManager().registerListener(this);
        else CLIENT.getSoundManager().unregisterListener(this);
    }

    @Override
    public void onSoundPlayed(SoundInstance sound, WeightedSoundSet soundSet) {
        if (soundSet.getSubtitle() != null) {
            Text text = soundSet.getSubtitle();
            if (!this.entries.isEmpty()) {
                for (var entry : entries) {
                    if (entry.getText().equals(text)) {
                        entry.reset(new Vec3d(sound.getX(), sound.getY(), sound.getZ()));
                        return;
                    }
                }
            }
            this.entries.add(new SubtitlesHud.SubtitleEntry(text, new Vec3d(sound.getX(), sound.getY(), sound.getZ())));
        }
    }

    public void tick() {
        double d = CLIENT.options.getNotificationDisplayTime().getValue();
        this.entries.removeIf( e -> e.getTime() + 3000.0 * d <= Util.getMeasuringTimeMs());
    }
}