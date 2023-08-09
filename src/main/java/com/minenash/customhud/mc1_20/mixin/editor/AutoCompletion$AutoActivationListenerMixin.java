package com.minenash.customhud.mc1_20.mixin.editor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.swing.event.DocumentEvent;

@Mixin(targets = "org/fife/ui/autocomplete/AutoCompletion$AutoActivationListener", remap = false)
public abstract class AutoCompletion$AutoActivationListenerMixin {

    @Shadow public abstract void insertUpdate(DocumentEvent e);

    /**
     * @author Jakob (Minenash)
     * @reason I'm the only one mixing into this, and an Inject cancel is wrong.
     *
     * This allows auto complete to update, not remove, on backspace
     */
    @Overwrite
    public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
    }

}
