package com.minenash.customhud.HudElements.supplier;

import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.complex.ListManager;

import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ListAttributeSuppliers {

    public static final Supplier<Number> INDEX = ListManager::getIndex;
    public static final Supplier<String> RAW = () -> ListManager.getValue().toString();

}
