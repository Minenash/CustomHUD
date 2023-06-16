package com.minenash.customhud.core.conditionals;

import com.minenash.customhud.core.elements.HudElement;

import java.util.List;

public interface Operation {

    int getValue();
    void printTree(int indent);

    record Or(List<Operation> elements) implements Operation {
        public int getValue() {
            for (Operation element : elements)
                if (element.getValue() > 0)
                    return 1;
            return 0;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- Or: ");
            for (Operation elem : elements)
                elem.printTree(indent + 2);
        }
    }

    record And(List<Operation> elements) implements Operation {
        public int getValue() {
            for (Operation element : elements)
                if (element.getValue() == 0)
                    return 0;
            return 1;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- And:");
            for (Operation elem : elements)
                elem.printTree(indent+2);
        }
    }
    record Comparison(HudElement left, HudElement right, ConditionalParser.Comparison comparison, boolean checkBool, boolean checkNum) implements Operation {
        public int getValue() {
            return getValueInternal() ? 1 : 0;
        }

        public boolean getValueInternal() {
            if (left == null || right == null)
                return false;
            return switch (comparison) {
                case EQUALS -> checkBool ? left.getBoolean() == right.getBoolean() : checkNum ? left.getNumber().doubleValue() == right.getNumber().doubleValue() : left.getString().equals(right.getString());
                case NOT_EQUALS -> checkBool ? left.getBoolean() != right.getBoolean() : checkNum ? left.getNumber().doubleValue() != right.getNumber().doubleValue() : !left.getString().equals(right.getString());

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
            String bool = (comparison == ConditionalParser.Comparison.EQUALS || comparison == ConditionalParser.Comparison.NOT_EQUALS) && checkBool ? "BOOL_" : "";
            System.out.println("- Conditional(" + bool + comparison + "): " + left.getString() + ", " + right.getString());
        }
    }

    record MathOperation(HudElement left, HudElement right, ConditionalParser.MathOperator operation, boolean checkBool, boolean checkNum) implements Operation {
        public int getValue() {
            return switch (operation) {
                default -> 0;
            };
        }


        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            //TODO
        }
    }

    record Literal(int value) implements Operation {
        public int getValue() {
            return value;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- Literal: " + value);
        }
    }
    record BooleanVariable(HudElement variable) implements Operation {
        public int getValue() {
            return variable == null ? 0 : variable.getBoolean() ? 1 : 0;
        }

        @Override
        public void printTree(int indent) {
            for (int i = 0; i < indent; i++)
                System.out.print(" ");
            System.out.println("- BooleanVariable");
        }
    }

}
