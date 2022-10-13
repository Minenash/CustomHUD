package com.minenash.customhud.HudElements;

import com.minenash.customhud.conditionals.Conditional;

import java.util.ArrayList;
import java.util.List;

public class ConditionalElement implements HudElement {

    private final Conditional conditional;
    private final List<HudElement> positive;
    private final List<HudElement> negative;

    public ConditionalElement(Conditional conditional, List<HudElement> positive, List<HudElement> negative) {
        this.conditional = conditional;
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public String getString() {
        StringBuilder builder = new StringBuilder();
        (conditional.getValue() ? positive : negative).forEach(e -> builder.append(e.getString()));
        return builder.toString();
    }

    @Override
    public Number getNumber() {
        return conditional.getValue() ? 1 : 0;
    }

    @Override
    public boolean getBoolean() {
        return conditional.getValue();
    }

    public static class MultiLineBuilder {
        private final Conditional conditional;
        private final List<HudElement> positive = new ArrayList<>();
        private final List<HudElement> negative = new ArrayList<>();

        private boolean elseSection = false;

        public MultiLineBuilder(Conditional conditional) {
            this.conditional = conditional;
        }

        public void add(List<HudElement> elements) {
            if (elseSection) {
                negative.addAll(elements);
                negative.add(new StringElement("\\n"));
            }
            else {
                positive.addAll(elements);
                positive.add(new StringElement("\\n"));
            }
        }

        public void elseSection() {
            elseSection = true;
        }

        public ConditionalElement build() {
            if (positive.size() > 0)
                positive.remove(positive.size()-1);
            if (negative.size() > 0)
                negative.remove(positive.size()-1);
            return new ConditionalElement(conditional, positive, negative);
        }

    }
}
