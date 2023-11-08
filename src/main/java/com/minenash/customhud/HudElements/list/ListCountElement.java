package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.HudElement;

public class ListCountElement implements HudElement {
    private final ListProvider provider;

    public ListCountElement(ListProvider provider) {
        this.provider = provider;
    }

    @Override
    public String getString() {
        return getNumber().toString();
    }

    @Override
    public Number getNumber() {
        return provider.get().size();
    }

    @Override
    public boolean getBoolean() {
        return provider.get().isEmpty();
    }

}
