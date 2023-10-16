package com.minenash.customhud.HudElements;

import com.minenash.customhud.conditionals.Operation;

import java.util.function.Supplier;

public class ExpressionElement implements HudElement {

    private final Operation expression;
    private final int precision;

    public ExpressionElement(Operation expression, int precision) {
        this.expression = expression;
        this.precision = precision;
    }


    @Override
    public String getString() {
        try {
            double num = expression.getValue();
            if (Double.isNaN(num))
                return "-";
            if (precision == -1)
                return num % 1 == 0 ? Integer.toString((int)num) : Double.toString(num);
            return String.format("%."+precision+"f", num);
        }
        catch (Exception _e) {
            return "-";
        }
    }

    @Override
    public Number getNumber() {
        return expression.getValue();
    }

    @Override
    public boolean getBoolean() {
        return expression.getValue() != 0;
    }

}
