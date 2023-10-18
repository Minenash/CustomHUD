package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.functional.FunctionalElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class ListElement implements HudElement, MultiElement {

    private static final HudElement POP_LIST_ELEMENT = new FunctionalElement.PopList();
    private static final HudElement ADVANCE_LIST_ELEMENT = new FunctionalElement.AdvanceList();

    private final Supplier<List<Object>> supplier;
    private final List<HudElement> elements;

    public ListElement(Supplier<List<Object>> supplier, List<HudElement> format) {
        this.supplier = supplier;
        this.elements = format;
    }

    public List<HudElement> expand() {
        List<Object> values = supplier.get();
        if (values.isEmpty())
            return Collections.emptyList();

        List<HudElement> expanded = new ArrayList<>();
        expanded.add(new FunctionalElement.PushList(values));
        for (int i = 0; i < values.size(); i++) {
            expanded.addAll(elements);
            expanded.add(ADVANCE_LIST_ELEMENT);
        }
        expanded.set(expanded.size()-1, POP_LIST_ELEMENT);
        return expanded;
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
