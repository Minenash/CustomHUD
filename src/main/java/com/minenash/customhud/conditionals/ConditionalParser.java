package com.minenash.customhud.conditionals;

import com.minenash.customhud.ComplexData;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.StringElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.errors.ErrorException;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;

import java.util.ArrayList;
import java.util.List;

public class ConditionalParser {

    enum TokenType { START_PREN, END_PREN, FULL_PREN, AND, OR, MATH, COMPARISON, NUMBER, STRING, BOOLEAN, VARIABLE }
    enum Comparison { LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUALS, EQUALS, NOT_EQUALS }
    enum MathOperator { ADD, SUBTRACT, MULTIPLY, DIVIDE, MOD }

    record Token(TokenType type, Object value) {
        public String toString() {
            return type + (value == null ? "" : " (" + value + ")");
        }
    }

    public static Operation parseConditional(String input, String source, int profile, int debugLine, ComplexData.Enabled enabled) {
        if (input.isBlank() || input.equals(",") || input.equals(", "))
            return new Operation.Literal(1);
        try {
            List<Token> tokens = getTokens(input, profile, debugLine, enabled);
            Operation c = getConditional(tokens);
//            System.out.println("Tree for Conditional on line " + debugLine + ":");
//            c.printTree(0);
//            System.out.println();
            return c;
        }
        catch (ErrorException e) {
            Errors.addError(profile, debugLine, source, e.type, e.context);
            System.out.println("[Line: " + debugLine + "] Conditional Couldn't Be Parsed: " + e.getMessage());
            System.out.println("Input: \"" + input + "\"");
            return new Operation.Literal(1);
        }
    }

    private static List<Token> getTokens(String original, int profile, int debugLine, ComplexData.Enabled enabled) throws ErrorException {

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
                tokens.add(new Token(TokenType.VARIABLE, VariableParser.parseElement(builder.toString(), profile, debugLine, enabled)));
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
        if (tokens.size() == 1) {
            Token token = tokens.get(0);
            switch (token.type) {
                case FULL_PREN: return getConditional( (List<Token>) token.value());
                case BOOLEAN: return new Operation.Literal( (Integer) token.value());
                case VARIABLE: return new Operation.BooleanVariable( (HudElement) token.value());
            }
            throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(0).type().toString());
        }
        if (tokens.size() != 3 || tokens.get(1).type() != TokenType.COMPARISON) {
            if (tokens.size() != 3)
                throw new ErrorException(ErrorType.CONDITIONAL_WRONG_NUMBER_OF_TOKENS, "" + tokens.size());
            throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(1).type().toString());
        }

        boolean checkBool = false;
        boolean checkNum = false;
        HudElement left = switch (tokens.get(0).type()) {
            case VARIABLE -> (HudElement) tokens.get(0).value();
            case STRING -> new StringElement((String)tokens.get(0).value());
            case NUMBER -> { checkNum = true; yield new SudoHudElements.Num((Number)tokens.get(0).value()); }
            case BOOLEAN -> {checkBool = true; yield new SudoHudElements.Bool((Boolean)tokens.get(0).value());}
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(0).type().toString());
        };
        HudElement right = switch (tokens.get(2).type()) {
            case VARIABLE -> (HudElement) tokens.get(2).value();
            case STRING -> new StringElement((String)tokens.get(2).value());
            case NUMBER -> { checkNum = true; yield new SudoHudElements.Num((Number)tokens.get(2).value()); }
            case BOOLEAN -> {checkBool = true; yield new SudoHudElements.Bool((Boolean)tokens.get(2).value());}
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(2).type().toString());
        };

        return new Operation.Comparison(left, right, (Comparison) tokens.get(1).value(), checkBool, checkNum);
    }

    //TODO
    private static Operation getMathOperation(List<Token> tokens) throws ErrorException {
        if (tokens.size() == 1) {
            Token token = tokens.get(0);
            switch (token.type) {
                case FULL_PREN: return getConditional( (List<Token>) token.value());
                case BOOLEAN: return new Operation.Literal( (Integer) token.value());
                case VARIABLE: return new Operation.BooleanVariable( (HudElement) token.value());
            }
            throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(0).type().toString());
        }

        if (tokens.size() != 3 || tokens.get(1).type() != TokenType.COMPARISON) {
            if (tokens.size() != 3)
                throw new ErrorException(ErrorType.CONDITIONAL_WRONG_NUMBER_OF_TOKENS, "" + tokens.size());
            throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(1).type().toString());
        }

        boolean checkBool = false;
        boolean checkNum = false;
        HudElement left = switch (tokens.get(0).type()) {
            case VARIABLE -> (HudElement) tokens.get(0).value();
            case STRING -> new StringElement((String)tokens.get(0).value());
            case NUMBER -> { checkNum = true; yield new SudoHudElements.Num((Number)tokens.get(0).value()); }
            case BOOLEAN -> {checkBool = true; yield new SudoHudElements.Bool((Boolean)tokens.get(0).value());}
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(0).type().toString());
        };
        HudElement right = switch (tokens.get(2).type()) {
            case VARIABLE -> (HudElement) tokens.get(2).value();
            case STRING -> new StringElement((String)tokens.get(2).value());
            case NUMBER -> { checkNum = true; yield new SudoHudElements.Num((Number)tokens.get(2).value()); }
            case BOOLEAN -> {checkBool = true; yield new SudoHudElements.Bool((Boolean)tokens.get(2).value());}
            default -> throw new ErrorException(ErrorType.CONDITIONAL_UNEXPECTED_VALUE, tokens.get(2).type().toString());
        };

        return new Operation.Comparison(left, right, (Comparison) tokens.get(1).value(), checkBool, checkNum);
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



}