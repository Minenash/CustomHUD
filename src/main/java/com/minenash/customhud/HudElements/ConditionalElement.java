package com.minenash.customhud.HudElements;

import java.util.List;

public class ConditionalElement implements HudElement {

    private final HudElement conditional;
    private final List<HudElement> positive;
    private final List<HudElement> negative;

    public ConditionalElement(HudElement conditional, List<HudElement> positive, List<HudElement> negative) {
        this.conditional = conditional;
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public String getString() {
        StringBuilder builder = new StringBuilder();
        (conditional.getBoolean() ? positive : negative).forEach(e -> builder.append(e.getString()));
        return builder.toString();
    }

    @Override
    public Number getNumber() {
        return conditional.getBoolean() ? 1 : 0;
    }

    @Override
    public boolean getBoolean() {
        return conditional.getBoolean();
    }
}
