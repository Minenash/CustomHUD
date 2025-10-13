package com.minenash.customhud.mixin.accessors;

import com.minenash.customhud.gui.profiles_widget.LineEntry;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(EntryListWidget.class)
public interface EntryListWidgetAccessor {

    @Accessor List<LineEntry> getChildren();
    @Invoker void callRecalculateAllChildrenPositions();

}
