package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.HudElement;

import java.util.List;
import java.util.function.Supplier;

public class ListCountElement implements HudElement {
    private final Supplier<List<Object>> supplier;

    public ListCountElement(Supplier<List<Object>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public String getString() {
        return getNumber().toString();
    }

    @Override
    public Number getNumber() {
        return supplier.get().size();
    }

    @Override
    public boolean getBoolean() {
        return supplier.get().isEmpty();
    }

}
