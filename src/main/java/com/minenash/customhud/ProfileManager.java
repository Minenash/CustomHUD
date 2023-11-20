package com.minenash.customhud;

import com.minenash.customhud.data.Profile;
import com.minenash.customhud.data.Toggle;
import net.minecraft.util.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private static List<Profile> profiles = new ArrayList<>();
    private static Profile active = null;
    public static boolean enabled = true;


    public static Profile getActive() {
        return enabled ? active : null;
    }

    public static Profile setActive(Profile active) {
        return ProfileManager.active = active;
    }

    public static void add(Profile profile) {
        profiles.add(profile);
    }

    public static void remove(Profile profile, boolean deleteFile) {
        if (active == profile)
            active = null;
        profiles.remove(profile);

        if (deleteFile) {
            try {
                Files.delete(CustomHud.PROFILE_FOLDER.resolve(profile.name + ".txt"));
            }
            catch (Exception e) {
                System.out.println("Couldn't delete profile, IO Error");
            }
        }
    }

    public static void replace(Profile profile) {
        for (int i = 0; i < profiles.size(); i++) {
            Profile p = profiles.get(i);
            if (p.name.equals(profile.name)) {
                profile.cycle = p.cycle;
                profile.keyBinding = p.keyBinding;

                for (var entry : p.toggles.entrySet()) {
                    Toggle t = entry.getValue();
                    if (!t.direct && !t.keyBinding.isUnbound() && !profile.toggles.containsKey(entry.getKey())) {
                        entry.getValue().inProfile = false;
                        profile.toggles.put(entry.getKey(), t);
                    }
                }

                profiles.set(i, profile);
                ConfigManager.save();
            }
            if (p == active)
                active = profile;
        }
    }

    public static void fallback() {
        if (active == null && !profiles.isEmpty())
            active = profiles.get(0);
    }

    public static void open(Profile profile) {
        if (profile != null)
            new Thread(() -> Util.getOperatingSystem().open(CustomHud.PROFILE_FOLDER.resolve(profile.name).toFile())).start();
    }

    public static List<Profile> getProfiles() {
        return profiles;
    }

    public static void reorder(List<Profile> order, boolean save) {
        for (Profile p : profiles)
            if (!order.contains(p))
                order.add(p);
        profiles = order;
        if (save)
            ConfigManager.save();
    }

    public static void rename(Profile profile, String name) {
        Path oldPath = CustomHud.PROFILE_FOLDER.resolve(profile.name + ".txt");
        profile.name = name;
        try {
            Path newPath = CustomHud.PROFILE_FOLDER.resolve(name + ".txt");
            Files.move(oldPath,newPath);
            ConfigManager.save();
        } catch (IOException e) {
            System.out.println("Can't rename profile, IO Exception");
            //TODO: GUI Errors
        }
    }

    public static Profile createBlank() {
        var profiles = ProfileManager.getProfiles().stream().map(p -> p.name).toList();
        String name;
        int index = 1;
        while (true) {
            name = "New Profile " + index;
            if (!profiles.contains(name))
                break;
            index++;
        }
        try {
            Files.createFile(CustomHud.PROFILE_FOLDER.resolve(name + ".txt"));
        }
        catch (Exception e) {
            System.out.println("Can't create profile file, IO Error");
            return null;
        }

        Profile p = Profile.create(name);
        ProfileManager.add(p);
        return p;
    }

}
