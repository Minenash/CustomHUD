package com.minenash.customhud.HudElements;

import java.util.List;
import java.util.function.Supplier;

public class ConditionalElement implements HudElement{

    private final Supplier<String> conditional;
    private final List<HudElement> positive;
    private final List<HudElement> negative;

    public ConditionalElement(Supplier<String> conditional, List<HudElement> positive, List<HudElement> negative) {
        this.conditional = conditional;
        this.positive = positive;
        this.negative = negative;
    }

    @Override
    public String getString() {
        StringBuilder builder = new StringBuilder();
        String res = conditional.get();
        if (res.isEmpty() || res.equals("?") || res.equals("false"))
            negative.forEach( e -> builder.append(e.getString()));
        else
            positive.forEach( e -> builder.append(e.getString()));
        return builder.toString();
    }
}
