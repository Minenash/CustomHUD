package com.minenash.customhud;

import com.minenash.customhud.data.Profile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileManager {

    private static Map<String, Profile> nameToProfile = new HashMap<>();
    private static List<String> indexedNames = new ArrayList<>();
    public static boolean enabled = true;

    private static Profile active = null;

    public static Profile getActive() {
        return enabled ? active : null;
    }

    public static Profile setActive(Profile active) {
        return ProfileManager.active = active;
    }

    public static Profile get(String name) {
        return nameToProfile.get(name);
    }

    public static String getName(int index) {
        return indexedNames.get(index);
    }

    public static void add(Profile profile) {
        nameToProfile.put(profile.name, profile);
        indexedNames.add(profile.name);
    }

    public static void remove(Profile profile) {
        if (active == profile)
            active = null;
        nameToProfile.remove(profile.name);
        indexedNames.remove(profile.name);
    }

    public static boolean exists(String profileName) {
        return nameToProfile.containsKey(profileName);
    }

    public static void fallback() {
        if (active == null)
            active = nameToProfile.get(indexedNames.get(0));
    }

    public static Path getPath(Profile profile) {
        return CustomHud.PROFILE_FOLDER.resolve(profile.name);
    }

    public static Path getPath(String profileName) {
        return CustomHud.PROFILE_FOLDER.resolve(profileName);
    }

    public static List<Profile> getProfiles() {
        List<Profile> profiles = new ArrayList<>(indexedNames.size());
        for (String profileName : indexedNames) {
            profiles.add(nameToProfile.get(profileName));
        }
        return profiles;
    }

    public static List<String> getProfileNames() {
        return indexedNames;
    }

    public static void reorder(List<String> order) {
        for (String name : indexedNames)
            if (!order.contains(name))
                order.add(name);
        indexedNames = order;

    }

}
