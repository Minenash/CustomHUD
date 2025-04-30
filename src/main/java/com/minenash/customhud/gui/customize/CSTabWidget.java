package com.minenash.customhud.gui.customize;

import net.minecraft.client.gui.widget.ClickableWidget;

import java.util.function.Consumer;

public interface CSTabWidget {

    void updateSize(int width, int height);
    void updateChildren();
    void forEachChild(Consumer<ClickableWidget> consumer);

}
