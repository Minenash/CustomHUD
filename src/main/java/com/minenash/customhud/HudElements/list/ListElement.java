package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.MultiElement;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.icon.IconElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListElement implements HudElement, MultiElement {

    private static final HudElement POP_LIST_ELEMENT = new FunctionalElement.PopList();
    private static final HudElement ADVANCE_LIST_ELEMENT = new FunctionalElement.AdvanceList();

    private final ListProvider provider;
    private final List<HudElement> elements;
    private final boolean removeLastNewLine;

    public ListElement(ListProvider provider, List<HudElement> format) {
        this.provider = provider;
        this.elements = format;
        this.removeLastNewLine = elements.get(elements.size()-1) instanceof FunctionalElement.NewLine;
        this.elements.add(ADVANCE_LIST_ELEMENT);
    }

    public List<HudElement> expand() {
        if (elements == null)
            return List.of(this);
        List<?> values = provider.get();
        if (values.isEmpty())
            return Collections.emptyList();

        List<HudElement> expanded = new ArrayList<>();
        expanded.add(new FunctionalElement.PushList(values));

        for (HudElement element : elements)
            if (element instanceof IconElement ie)
                ie.setList(values);

        for (int i = 0; i < values.size(); i++)
            expanded.addAll(elements);
        expanded.set(expanded.size()-1, POP_LIST_ELEMENT);
        if (removeLastNewLine)
            expanded.remove(expanded.size()-2);
        return expanded;
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

    public static class MultiLineBuilder {
        private static final ListProvider EMPTY = () -> Collections.EMPTY_LIST;

        public final ListProvider provider;
        private final List<HudElement> elements = new ArrayList<>();

        public MultiLineBuilder(ListProvider provider) {
            this.provider = provider == null ? EMPTY : provider;
        }

        public void add(HudElement element) {
            this.elements.add(element);
        }

        public void addAll(List<HudElement> elements) {
            this.elements.addAll(elements);
        }

        public ListElement build() {
            return new ListElement(provider, elements);
        }

    }



}
