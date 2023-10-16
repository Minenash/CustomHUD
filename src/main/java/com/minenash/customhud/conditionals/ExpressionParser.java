package com.minenash.customhud.conditionals;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.errors.ErrorException;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;
import com.mojang.datafixers.util.Pair;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
public class ExpressionParser {

    enum TokenType { START_PREN, END_PREN, FULL_PREN, AND, OR, MATH, COMPARISON, NUMBER, STRING, BOOLEAN, VARIABLE }
    enum Comparison { LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUALS, EQUALS, NOT_EQUALS }
    enum MathOperator { ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD }

    record Token(TokenType type, Object value) {
        public String toString() {
            return type + (value == null ? "" : " (" + value + ")");
        }
    }

    public static Operation parseConditional(String input, String source, int profileNum, int debugLine, ComplexData.Enabled enabled) {
        if (input.isBlank() || input.equals(",") || input.equals(", "))
            return new Operation.Literal(1);
        try {
            List<Token> tokens = getTokens(input, profileNum, debugLine, enabled);
            Operation c = getConditional(tokens);
            System.out.println("Tree for Conditional on line " + debugLine + ":");
            c.printTree(0);
            System.out.println();
            return c;
        }
        catch (ErrorException e) {
            Errors.addError(profileNum, debugLine, source, e.type, e.context);
            System.out.println("[Line: " + debugLine + "] Conditional Couldn't Be Parsed: " + e.getMessage());
            System.out.println("Input: \"" + input + "\"");
            return new Operation.Literal(1);
        }
    }

    private static List<Token> getTokens(String original, int profileNum, int debugLine, ComplexData.Enabled enabled) throws ErrorException {

        List<Token> tokens = new ArrayList<>();
        char[] chars = original.toCharArray();

        for (int i = 0; i < chars.length;) {
            char c = chars[i];
            if (c == '(') tokens.add(new Token(TokenType.START_PREN, null));
            else if (c == ')') tokens.add(new Token(TokenType.END_PREN, null));
            else if (c == '|') tokens.add(new Token(TokenType.OR, null));
            else if (c == '&') tokens.add(new Token(TokenType.AND, null));
            else if (c == '=') tokens.add(new Token(TokenType.COMPARISON, Comparison.EQUALS));
            else if (c == '+') tokens.add(new Token(TokenType.MATH, MathOperator.ADD));
            else if (c == '*') tokens.add(new Token(TokenType.MATH, MathOperator.MULTIPLY));
            else if (c == '/') tokens.add(new Token(TokenType.MATH, MathOperator.DIVIDE));
            else if (c == '%') tokens.add(new Token(TokenType.MATH, MathOperator.MOD));
            else if (c == '-' && i+1 < chars.length && !isNum(chars[i+1])) tokens.add(new Token(TokenType.MATH, MathOperator.SUBTRACT));
            else if (c == '!') {
                if (i + 1 == chars.length || chars[i + 1] != '=')
                    throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, "!");
                tokens.add(new Token(TokenType.COMPARISON, Comparison.NOT_EQUALS));
                i += 2;
                continue;
            }
            else if (c == '>') {
                boolean hasEqual =  i + 1 != chars.length && chars[i + 1] == '=';
                tokens.add(new Token(TokenType.COMPARISON, hasEqual ? Comparison.GREATER_THAN_OR_EQUALS : Comparison.GREATER_THAN));
                i += hasEqual ? 2 : 1;
                continue;
            }
            else if (c == '<') {
                boolean hasEqual =  i + 1 != chars.length && chars[i + 1] == '=';
                tokens.add(new Token(TokenType.COMPARISON, hasEqual ? Comparison.LESS_THAN_OR_EQUAL : Comparison.LESS_THAN));
                i += hasEqual ? 2 : 1;
                continue;
            }
            else if (c == 'f' && i + 4 < chars.length && original.startsWith("false", i)) {
                tokens.add(new Token(TokenType.BOOLEAN, false));
                i+=5;
                continue;
            }
            else if (c == 't' && i + 3 < chars.length && original.startsWith("true", i)) {
                tokens.add(new Token(TokenType.BOOLEAN, true));
                i+=4;
                continue;
            }
            else if (c == '"') {
                StringBuilder builder = new StringBuilder();
                i++;
                while (i < chars.length && chars[i] != '"')
                    builder.append(chars[i++]);
                tokens.add(new Token(TokenType.STRING, builder.toString()));
            }
            else if (isNum(c) || c == '-') {
                StringBuilder builder = new StringBuilder();
                builder.append(chars[i++]);
                while (i < chars.length && isNum(chars[i]))
                    builder.append(chars[i++]);
                tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(builder.toString())));
                continue;
            }
            else if (isVar(c)) {
                StringBuilder builder = new StringBuilder();
                builder.append('{');
                while (i < chars.length && isVar(chars[i])) {
                    builder.append(chars[i]);
                    i++;
                }
                builder.append('}');
                tokens.add(new Token(TokenType.VARIABLE, VariableParser.parseElement(builder.toString(), profileNum, debugLine, enabled)));
                continue;
            }
            i++;

        }

//        for (Token token : tokens)
//            System.out.println("[A]" + token);

        int start = -1;
        for (int i = 0; i < tokens.size(); i++) {
            TokenType type = tokens.get(i).type();
            if (type == TokenType.START_PREN) {
                start = i;
            }
            else if (type == TokenType.END_PREN) {
                reduceList(tokens, start, i);
                start = -1;
                i = -1;
            }

        }

//        System.out.println("---------------");
//        for (Token token : tokens)
//            System.out.println("[B]" + token);
        return tokens;

    }

    private static boolean isNum(char c) {
        return c == '.' || (c >= '0' && c <= '9');
    }
    private static boolean isVar(char c) {
        return c == '.' || c == ':' || c == '_' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }
    private static void reduceList(List<Token> original, int start, int end) {
        original.set(start, new Token(TokenType.FULL_PREN, new ArrayList<>(original.subList(start+1, end))));
        for (; end > start; end--)
            original.remove(end);
    }

    private static Operation getConditional(List<Token> tokens) throws ErrorException {
        List<List<Token>> ors = split(tokens, TokenType.OR);
        List<Operation> conditionals = new ArrayList<>();
        for (var or : ors)
            conditionals.add(getAndConditional(or));

        return conditionals.size() == 1 ? conditionals.get(0) : new Operation.Or(conditionals);

    }

    private static Operation getAndConditional(List<Token> tokens) throws ErrorException {
        List<List<Token>> ands = split(tokens, TokenType.AND);
        List<Operation> conditionals = new ArrayList<>();
        for (var and : ands)
            conditionals.add(getComparisonOperation(and));

        return conditionals.size() == 1 ? conditionals.get(0) : new Operation.And(conditionals);
    }

    @SuppressWarnings("unchecked")
    private static Operation getComparisonOperation(List<Token> tokens) throws ErrorException {
        if (tokens.size() == 1)
            return getPrimitiveOperation(tokens.get(0));

        int comparatorIndex = -1;
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).type == TokenType.COMPARISON) {
                comparatorIndex = i;
                break;
            }
        }

        if (comparatorIndex == -1)
            return getMathOperation(tokens);
        else if (comparatorIndex == 0 )
            throw new ErrorException(ErrorType.CONDITIONAL_WRONG_NUMBER_OF_TOKENS, "No values on the left of comparison");
        else if (comparatorIndex == tokens.size()-1)
            throw new ErrorException(ErrorType.CONDITIONAL_WRONG_NUMBER_OF_TOKENS, "No values on the right of comparison");

        HudElement left = comparatorIndex == 1 ?
                getValueElement(tokens.get(0)) :
                new SudoElements.Op(getMathOperation(tokens.subList(0, comparatorIndex)));
        HudElement right = comparatorIndex == tokens.size()-2 ?
                getValueElement(tokens.get(tokens.size()-1)) :
                new SudoElements.Op(getMathOperation(tokens.subList(comparatorIndex+1, tokens.size())));

        return new Operation.Comparison(left, right, (Comparison) tokens.get(comparatorIndex).value());
    }

    //TODO
    private static Operation getMathOperation(List<Token> tokens) throws ErrorException {
        if (tokens.size() == 1)
            return getPrimitiveOperation(tokens.get(0));

        Pair<List<List<Token>>, List<MathOperator>> multiplyPairs = split(tokens, List.of(MathOperator.MULTIPLY, MathOperator.DIVIDE, MathOperator.MOD));
        List<Operation> ops = new ArrayList<>();

        for (var partTokens : multiplyPairs.getFirst()) {
            Pair<List<List<Token>>, List<MathOperator>> addingPairs = split(partTokens, List.of(MathOperator.ADD, MathOperator.SUBTRACT));
            List<HudElement> elements = new ArrayList<>();
            for (var partPartToken : addingPairs.getFirst()) {
                if (partPartToken.size() > 1)
                    throw new ErrorException(ErrorType.CONDITIONAL_WRONG_NUMBER_OF_TOKENS, "No operation between values");
                elements.add( getValueElement(partPartToken.get(0)) );
            }
            ops.add(new Operation.MathOperation(elements, addingPairs.getSecond()));
        }
        return new Operation.MathOperationOp(ops, multiplyPairs.getSecond());

    }

    @SuppressWarnings("unchecked")
    private static HudElement getValueElement(Token token) throws ErrorException {
        return switch (token.type()) {
            case VARIABLE -> (HudElement) token.value();
            case STRING -> new SudoElements.Str((String) token.value());
            case NUMBER -> new SudoElements.Num((Number) token.value());
            case BOOLEAN -> new SudoElements.Bool((Boolean) token.value());
            case FULL_PREN -> new SudoElements.Op(getConditional((List<Token>) token.value()));
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, token.type().toString());
        };
    }

    @SuppressWarnings("unchecked")
    private static Operation getPrimitiveOperation(Token token) throws ErrorException {
        return switch (token.type) {
            case FULL_PREN -> getConditional((List<Token>) token.value());
            case BOOLEAN -> new Operation.Literal((Integer) token.value());
            case NUMBER -> new Operation.Literal((Double) token.value());
            case VARIABLE -> new Operation.BooleanVariable((HudElement) token.value());
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, token.type().toString());
        };
    }

    private static List<List<Token>> split(List<Token> tokens, TokenType type) {
        List<List<Token>> sections = new ArrayList<>();
        List<Token> current = new ArrayList<>();

        for (Token token : tokens) {
            if (token.type() == type) {
                sections.add(current);
                current = new ArrayList<>();
            }
            else
                current.add(token);
        }
        sections.add(current);
        return sections;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private static Pair<List<List<Token>>, List<MathOperator>> split(List<Token> tokens, List<MathOperator> ops) {
        List<List<Token>> sections = new ArrayList<>();
        List<MathOperator> operators = new ArrayList<>();
        List<Token> current = new ArrayList<>();

        for (Token token : tokens) {
            if ( token.type == TokenType.MATH && ops.contains(token.value())) {
                sections.add(current);
                operators.add((MathOperator) token.value());
                current = new ArrayList<>();
            }
            else
                current.add(token);
        }
        sections.add(current);
        return Pair.of(sections, operators);
    }



}