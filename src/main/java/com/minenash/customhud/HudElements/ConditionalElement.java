package com.minenash.customhud.HudElements;

import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.conditionals.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConditionalElement implements HudElement {

    public record ConditionalPair(Operation conditional, List<HudElement> ifTrue) {}

    private final List<ConditionalPair> pairs;

    public ConditionalElement(List<ConditionalPair> pairs) {
        this.pairs = pairs;
    }

    public List<HudElement> get() {
        for (ConditionalPair pair : pairs) {
            if (pair.conditional.getValue() != 0) {
                return pair.ifTrue;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getString() {
        List<HudElement> elements = get();

        StringBuilder builder = new StringBuilder();
        for (HudElement element : elements)
            builder.append(element.getString());

        return builder.toString();
    }

    @Override
    public Number getNumber() {
        for (int i = 0; i < pairs.size(); i++)
            if (pairs.get(i).conditional.getValue() != 0)
                return i+1;
        return 0;
    }

    @Override
    public boolean getBoolean() {
        for (ConditionalPair pair : pairs)
            if (pair.conditional.getValue() != 0)
                return true;
        return false;
    }

    public static class MultiLineBuilder {
        private final List<ConditionalPair> pairs = new ArrayList<>();

        private Operation conditional = null;
        private List<HudElement> elements = new ArrayList<>();

        public MultiLineBuilder(Operation conditional) {
            setConditional(conditional);
        }

        public void setConditional(Operation conditional) {
            if (this.conditional != null) {
                pairs.add(new ConditionalPair(this.conditional, elements));
                elements = new ArrayList<>();
            }
            this.conditional = conditional;
        }

        public void add(HudElement element) {
            this.elements.add(element);
        }

        public void addAll(List<HudElement> elements) {
            this.elements.addAll(elements);
        }

        public ConditionalElement build() {
            pairs.add(new ConditionalPair(conditional, elements));
            return new ConditionalElement(pairs);
        }

    }
}
