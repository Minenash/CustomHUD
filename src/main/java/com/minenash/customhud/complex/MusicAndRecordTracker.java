package com.minenash.customhud.complex;

import com.minenash.customhud.mixin.music.MinecraftClientAccess;
import com.minenash.customhud.mixin.music.MusicTrackerAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;
import org.apache.commons.lang3.text.WordUtils;

public class MusicAndRecordTracker {

    public static boolean isMusicPlaying = false;
    public static String musicId = "";
    public static String musicName = "";

    public static boolean isRecordPlaying = false;
    public static SoundInstance recordInstance = null;
    public static String recordId = "";
    public static String recordName = "";
    public static int recordLength = 0;
    public static int recordElapsed = 0;
    public static ItemStack recordIcon = null;

    private final static MinecraftClient client = MinecraftClient.getInstance();

    public static void tick() {
        isRecordPlaying = recordInstance != null && client.getSoundManager().isPlaying(recordInstance);
        if (isRecordPlaying)
            recordElapsed++;

        SoundInstance music = ((MusicTrackerAccess)((MinecraftClientAccess)client).getMusicTracker()).getCurrent();
        isMusicPlaying =  client.getSoundManager().isPlaying(music);
        if (music != null) {
            musicId = music.getSound().getIdentifier().toString();
            musicName = WordUtils.capitalize(musicId.substring(musicId.lastIndexOf('/')+1).replace("_", " ").replaceAll("(\\d+)", " $1"));
        }
    }

    public static void setRecord(SoundEvent song, SoundInstance instance) {
        recordElapsed = 0;
        if (song == null) {
            recordInstance = null;
            return;
        }

        recordInstance = instance;
        recordId = song.getId().toString();

        MusicDiscItem musicDiscItem = MusicDiscItem.bySound(song);
        if (musicDiscItem == null) {
            recordName = "Unknown Music Disc";
            recordLength = 0;
            recordIcon = ItemStack.EMPTY;
        }
        else {
            recordName = I18n.translate(musicDiscItem.getTranslationKey() + ".desc");
            recordLength = musicDiscItem.getSongLengthInTicks();
            recordIcon = new ItemStack(musicDiscItem);
        }

    }

}
