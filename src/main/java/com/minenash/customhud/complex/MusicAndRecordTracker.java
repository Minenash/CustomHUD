package com.minenash.customhud.complex;

import com.minenash.customhud.mixin.music.MinecraftClientAccess;
import com.minenash.customhud.mixin.music.MusicTrackerAccess;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.block.jukebox.JukeboxSong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MusicAndRecordTracker {

    public static boolean isMusicPlaying = false;
    public static Identifier musicId = null;
    public static String musicName = "";

    public static boolean isRecordPlaying = false;

    public static List<RecordInstance> records = new ArrayList<>();
    public static class RecordInstance {
        public SoundInstance sound = null;
        public Identifier id = null;
        public Text name = Text.literal("Unknown Music Disc");
        public int length = 0;
        public int elapsed = 0;
        public ItemStack icon = new ItemStack(Items.BARRIER);
    }

    public static RecordInstance getClosestRecord() {
        if (client.player == null) return records.get(records.size()-1);
        Vec3d pos = client.player.getPos();

        RecordInstance closestInstance = records.get(0);
        double closestDistance = pos.squaredDistanceTo(closestInstance.sound.getX(), closestInstance.sound.getY(), closestInstance.sound.getZ());


        for (int i = 1; i < records.size(); i++) {
            RecordInstance instance = records.get(i);
            double distance = pos.squaredDistanceTo(instance.sound.getX(), instance.sound.getY(), instance.sound.getZ());
            if (distance <= closestDistance) {
                closestDistance = distance;
                closestInstance = instance;
            }
        }

        return closestInstance;
    }

    private final static MinecraftClient client = MinecraftClient.getInstance();

    public static void tick() {
//        isRecordPlaying = recordInstance != null && client.getSoundManager().isPlaying(recordInstance);
//        if (isRecordPlaying)
//            recordElapsed++;


        Iterator<RecordInstance> iterator = records.iterator();
        while (iterator.hasNext()) {
            RecordInstance instance = iterator.next();
            if (!client.getSoundManager().isPlaying(instance.sound))
                iterator.remove();
            else
                instance.elapsed++;
        }
        isRecordPlaying = !records.isEmpty();


        SoundInstance music = ((MusicTrackerAccess)((MinecraftClientAccess)client).getMusicTracker()).getCurrent();
        isMusicPlaying =  client.getSoundManager().isPlaying(music);
        if (music != null) {
            musicId = music.getSound().getIdentifier();
            String idStr = musicId.toString();
            musicName = WordUtils.capitalize(idStr.substring(idStr.lastIndexOf('/')+1).replace("_", " ").replaceAll("(\\d+)", " $1"));
        }
    }

    public static void setRecord(RegistryEntry<JukeboxSong> song, SoundInstance instance, BlockPos jukeboxPos) {
        if (song == null)
            return;

        JukeboxSong jbs = song.value();

        RecordInstance record = new RecordInstance();
        record.sound = instance;
        record.id = song.getKey().isPresent() ? song.getKey().get().getValue() : null;
        record.name = jbs.description();
        record.length = jbs.getLengthInTicks();

        if (client.getServer() != null && client.world != null) {
            BlockEntity state = client.getServer().getWorld(client.world.getRegistryKey()).getWorldChunk(jukeboxPos).getBlockEntity(jukeboxPos, WorldChunk.CreationType.IMMEDIATE);
            record.icon = state instanceof JukeboxBlockEntity jbe ? jbe.getStack() : ItemStack.EMPTY;
        }
        if (record.icon == ItemStack.EMPTY) {
            record.icon = new ItemStack(switch (record.id.toString()) {
                case "minecraft:13" -> Items.MUSIC_DISC_13;
                case "minecraft:cat" -> Items.MUSIC_DISC_CAT;
                case "minecraft:blocks" -> Items.MUSIC_DISC_BLOCKS;
                case "minecraft:chirp" -> Items.MUSIC_DISC_CHIRP;
                case "minecraft:far" -> Items.MUSIC_DISC_FAR;
                case "minecraft:mall" -> Items.MUSIC_DISC_MALL;
                case "minecraft:mellohi" -> Items.MUSIC_DISC_MELLOHI;
                case "minecraft:stal" -> Items.MUSIC_DISC_STAL;
                case "minecraft:strad" -> Items.MUSIC_DISC_STRAD;
                case "minecraft:ward" -> Items.MUSIC_DISC_WARD;
                case "minecraft:11" -> Items.MUSIC_DISC_11;
                case "minecraft:wait" -> Items.MUSIC_DISC_WAIT;
                case "minecraft:pigstep" -> Items.MUSIC_DISC_PIGSTEP;
                case "minecraft:otherside" -> Items.MUSIC_DISC_OTHERSIDE;
                case "minecraft:5" -> Items.MUSIC_DISC_5;
                case "minecraft:relic" -> Items.MUSIC_DISC_RELIC;
                case "minecraft:precipice" -> Items.MUSIC_DISC_PRECIPICE;
                case "minecraft:creator" -> Items.MUSIC_DISC_CREATOR;
                case "minecraft:creator_music_box" -> Items.MUSIC_DISC_CREATOR_MUSIC_BOX;
                default -> Items.AIR;
            });
        }

        records.add(record);
    }

}
