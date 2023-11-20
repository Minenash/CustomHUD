package com.minenash.customhud.data;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;

public class Toggle {

    public final String name;
    public final boolean direct;
    public final List<Integer> lines;
    public final KeyBinding keyBinding;

    public boolean inProfile;
    public boolean value;

    public Toggle(String name, boolean direct, int line, KeyBinding keyBinding, boolean inProfile) {
        this.name = name;
        this.direct = direct;
        this.lines = new ArrayList<>();
        this.lines.add(line);
        this.keyBinding = keyBinding;
        this.inProfile = inProfile;
    }

    public boolean getValue() {
        return value;
    }

    public void toggle() {
        value = !value;
    }

    public String getDisplayName() {
        if (!direct)
            return name;
        if (I18n.hasTranslation(name))
            return "Key: " + I18n.translate(name);
        if (name.startsWith("key.mouse."))
            return "Key: " + name.substring(10);
        //name.startsWith("key.keyboard.")
        return "Key: " + name.substring(13).toUpperCase();
    }

}
