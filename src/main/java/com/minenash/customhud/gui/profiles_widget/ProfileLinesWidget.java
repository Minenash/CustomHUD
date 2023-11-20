package com.minenash.customhud.gui.profiles_widget;

import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.errors.Errors;
import com.minenash.customhud.gui.NewConfigScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileLinesWidget extends ElementListWidget<LineEntry> {



    public final NewConfigScreen screen;

    public ProfileLinesWidget(NewConfigScreen screen, int startY, int endY) {
        super(MinecraftClient.getInstance(), screen.width, endY - startY, startY, endY, 20);

        this.screen = screen;

        for (Profile p : ProfileManager.getProfiles()) {
            this.addEntry(new ProfileLineEntry(p, this));
        }
        this.addEntry(new LineEntry.NewProfile(this));
    }

    public void update() {
        for (LineEntry widget : children())
            widget.update();
    }

    public void newProfile() {
        Profile p = ProfileManager.createBlank();
        if (p != null)
            children().add(children().size()-1, new ProfileLineEntry(p, this));
    }

    public void move(ProfileLineEntry entry, int direction) {
        int index = MathHelper.clamp(children().indexOf(entry) + direction, 0, children().size()-1);
        children().remove(entry);
        children().add(index, entry);
    }

    public void doneMoving() {
        List<Profile> profiles = new ArrayList<>(children().size()-1);
        for (LineEntry e : children()) {
            if (e instanceof ProfileLineEntry ple)
                profiles.add(ple.profile);
        }
        ProfileManager.reorder(profiles, true);
    }

    public void deleteProfile(ProfileLineEntry entry) {
        ProfileManager.remove(entry.profile, true);
        children().remove(entry);
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 96 + 32;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 48 + 16;
    }


}
