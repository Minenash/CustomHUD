package com.minenash.customhud.HudElements;

import com.minenash.customhud.conditionals.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ConditionalElement implements HudElement {

    public record ConditionalPair(Operation conditional, List<HudElement> ifTrue) {}

    private final List<ConditionalPair> pairs;

    public ConditionalElement(List<ConditionalPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String getString() {
        List<HudElement> elements = null;
        for (ConditionalPair pair : pairs) {
            if (pair.conditional.getValue() != 0) {
                elements = pair.ifTrue;
                break;
            }
        }

        StringBuilder builder = new StringBuilder();
        if (elements != null)
            elements.forEach(e -> builder.append(e.getString()));
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

        public void setConditional(Operation conditional) {
            if (this.conditional != null) {
                pairs.add(new ConditionalPair(this.conditional, elements));
                elements = new ArrayList<>();
            }
            this.conditional = conditional;
        }

        public void add(List<HudElement> elements) {
            this.elements.addAll(elements);
            this.elements.add(new StringElement("\\n"));
        }

        public ConditionalElement build() {
            pairs.add(new ConditionalPair(conditional, elements));

            for (ConditionalPair pair : pairs)
                if (pair.ifTrue.size() > 0)
                    pair.ifTrue.remove(pair.ifTrue.size() - 1);

           return new ConditionalElement(pairs);
        }

    }
}
