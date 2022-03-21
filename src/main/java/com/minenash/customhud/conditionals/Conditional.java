package com.minenash.customhud.conditionals;

import com.minenash.customhud.HudElements.HudElement;

import java.util.List;

public interface Conditional {

    boolean getValue();
    void printTree(int indent);

    record Or(List<Conditional> elements) implements Conditional {
        public boolean getValue() {
            for (Conditional element : elements)
                if (element.getValue())
                    return true;
            return false;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- Or: ");
            for (Conditional elem : elements)
                elem.printTree(indent + 2);
        }
    }

    record And(List<Conditional> elements) implements Conditional {
        public boolean getValue() {
            for (Conditional element : elements)
                if (!element.getValue())
                    return false;
            return true;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- And:");
            for (Conditional elem : elements)
                elem.printTree(indent+2);
        }
    }
    record Comparison(HudElement left, HudElement right, ConditionalParser.Conditionals comparison, boolean checkBool) implements Conditional {
        public boolean getValue() {
            return switch (comparison) {
                case EQUALS -> checkBool ? left.getBoolean() == right.getBoolean() : left.getString().equals(right.getString());
                case NOT_EQUALS -> checkBool ? left.getBoolean() != right.getBoolean() : !left.getString().equals(right.getString());

                case LESS_THAN -> left.getNumber().doubleValue() < right.getNumber().doubleValue();
                case GREATER_THAN -> left.getNumber().doubleValue() > right.getNumber().doubleValue();
                case LESS_THAN_OR_EQUAL -> left.getNumber().doubleValue() <= right.getNumber().doubleValue();
                case GREATER_THAN_OR_EQUALS -> left.getNumber().doubleValue() >= right.getNumber().doubleValue();
            };
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            String bool = (comparison == ConditionalParser.Conditionals.EQUALS || comparison == ConditionalParser.Conditionals.NOT_EQUALS) && checkBool ? "BOOL_" : "";
            System.out.println("- Conditional(" + bool + comparison + "): " + left.getString() + ", " + right.getString());
        }
    }
    record Literal(boolean value) implements Conditional {
        public boolean getValue() {
            return value;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- Literal: " + value);
        }
    }
    record BooleanVariable(HudElement variable) implements Conditional {
        public boolean getValue() {
            return variable.getBoolean();
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- BooleanVariable: " + variable.getString());
        }
    }

}
