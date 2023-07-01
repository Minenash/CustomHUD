package com.minenash.customhud.core.elements;

import com.minenash.customhud.core.ProfileHandler;

public class ToggleElement extends SupplierElements.Bool {
    public ToggleElement(int keycode) {
        super(() -> ProfileHandler.getActiveProfile().toggles.getOrDefault(keycode, false));
    }
}
