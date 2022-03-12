package com.minenash.customhud.NewHudElements;

import java.util.List;

public class ConditionalElement2 implements HudElement2 {

    private final HudElement2 conditional;
    private final List<HudElement2> positive;
    private final List<HudElement2> negative;

    public ConditionalElement2(HudElement2 conditional, List<HudElement2> positive, List<HudElement2> negative) {
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
