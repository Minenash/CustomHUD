package com.minenash.customhud.conditionals;

import com.minenash.customhud.HudElements.HudElement;

import java.util.List;

public interface Operation {

    double getValue();
    void printTree(int indent);

    record Or(List<Operation> elements) implements Operation {
        public double getValue() {
            for (Operation element : elements)
                if (element.getValue() > 0)
                    return 1;
            return 0;
        }

        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- Or: ");
            for (Operation elem : elements)
                elem.printTree(indent + 2);
        }
    }

    record And(List<Operation> elements) implements Operation {
        public double getValue() {
            for (Operation element : elements)
                if (element.getValue() == 0)
                    return 0;
            return 1;
        }

        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- And:");
            for (Operation elem : elements)
                elem.printTree(indent+2);
        }
    }

    class Comparison implements Operation {
        public final HudElement left, right;
        public final boolean checkBool, checkNum;
        public final ExpressionParser.Comparison comparison;

        Comparison(HudElement left, HudElement right, ExpressionParser.Comparison comparison) {
            this.left = left;
            this.right = right;
            this.comparison = comparison;
            this.checkBool = left instanceof SudoElements.Bool || right instanceof SudoElements.Bool;
            this.checkNum = left instanceof SudoElements.Num || right instanceof SudoElements.Num;
        }

        public double getValue() {
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
            String bool = (comparison == ExpressionParser.Comparison.EQUALS || comparison == ExpressionParser.Comparison.NOT_EQUALS) && checkBool ? "BOOL_" : "";
            System.out.println(indent(indent) + "- Conditional(" + bool + comparison + "): " + left.getString() + ", " + right.getString());
            if (left instanceof SudoElements.Op op) {
                op.op().printTree(indent + 2);
            }
            if (right instanceof SudoElements.Op op) {
                op.op().printTree(indent + 2);
            }
        }
    }

    record MathOperation(List<HudElement> elements, List<ExpressionParser.MathOperator> operations) implements Operation {
        public double getValue() {
            if (elements.isEmpty()) return 0;
            double value = elements.get(0).getNumber().doubleValue();
            for (int i = 1; i < elements.size(); i++)
                value = apply(value, elements.get(i).getNumber().doubleValue(), operations.get(i-1));
            return value;
        }

        public static double apply(double value, double second, ExpressionParser.MathOperator op) {
            return switch (op) {
                case ADD -> value + second;
                case SUBTRACT -> value - second;
                case MULTIPLY -> value * second;
                case DIVIDE -> value / second;
                case MOD -> value % second;
            };
        }


        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- Operations: " + operations.toString());
            for (HudElement element : elements) {
                System.out.println(indent(indent) + "- " + element.getString());
            }
        }
    }

    record MathOperationOp(List<Operation> elements, List<ExpressionParser.MathOperator> operations) implements Operation {
        public double getValue() {
            double value = elements().isEmpty() ? 0 : elements.get(0).getValue();
            for (int i = 1; i < elements.size()-1; i++)
                value = MathOperation.apply(value, elements.get(i).getValue(), operations.get(i-1));
            return value;
        }


        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- Operations: " + operations.toString());
            for (Operation op : elements)
                op.printTree(indent + 2);
        }
    }

    record Literal(double value) implements Operation {
        public double getValue() {
            return value;
        }

        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- Literal: " + value);
        }
    }
    record BooleanVariable(HudElement variable) implements Operation {
        public double getValue() {
            return variable == null ? 0 : variable.getBoolean() ? 1 : 0;
        }

        @Override
        public void printTree(int indent) {
            System.out.println(indent(indent) + "- BooleanVariable");
        }
    }

    static String indent(int indent) {
        return " ".repeat(indent);
    }

}