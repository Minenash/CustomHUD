package com.minenash.customhud.data;

import com.minenash.customhud.HudElements.ConditionalElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.list.ListElement;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.conditionals.ExpressionParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class MultiLineStacker {

    private final List<HudElement> base = new ArrayList<>();
    private final Stack<Object> stack = new Stack<>();

    public void startIf(String cond, int profileID, int line, String source, ComplexData.Enabled enabled) {
        Operation op = ExpressionParser.parseExpression(cond, source, profileID, line+1, enabled, getSuppler());
        stack.push(new ConditionalElement.MultiLineBuilder(op));
    }

    public void elseIf(String cond, int profileID, int line, String source, ComplexData.Enabled enabled) {
        if (stack.isEmpty())
            Errors.addError(profileID, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "else if");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(ExpressionParser.parseExpression(cond, source, profileID, line + 1, enabled, getSuppler()));
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileID, line, false) );
                    break;
                }
            }
            ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(ExpressionParser.parseExpression(cond, source, profileID, line + 1, enabled, getSuppler()));
        }
    }

    public void else1(int profileID, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profileID, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "=else=");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(new Operation.Literal(1));
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileID, line, false) );
                    break;
                }
            }
            ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(new Operation.Literal(1));
        }

    }

    public void endIf(int profileID, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profileID, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "end");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
            HudElement element = mlb.build();
            stack.pop();
            addElement(element);
        }
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileID, line, false) );
                    break;
                }
            }
            HudElement element = ( (ConditionalElement.MultiLineBuilder)stack.peek() ).build();
            stack.pop();
            addElement(element);
        }
    }

    public void startFor(String list, ComplexData.Enabled enabled) {
        stack.push( new ListElement.MultiLineBuilder(VariableParser.getListSupplier(list, enabled)) );
    }

    public void endFor(int profileID, int line, String source) {
        if (stack.isEmpty()) {
            Errors.addError(profileID, line + 1, source, ErrorType.LIST_NOT_STARTED, "");
        }
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
            HudElement element = leb.build();
            stack.pop();
            addElement(element);
        }
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileID, line, false) );
                    break;
                }
            }
            HudElement element = ( (ListElement.MultiLineBuilder)stack.peek() ).build();
            stack.pop();
            addElement(element);
        }
    }

    public void addElement(HudElement element) {
        if (stack.empty())
            base.add(element);
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.add(element);
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb)
            leb.add(element);
    }

    public void addElements(String source, int profileID, int line, ComplexData.Enabled enabled) {
        List<HudElement> elements = VariableParser.addElements(source, profileID, line + 1, enabled, true, getSuppler());
        if (stack.empty())
            base.addAll(elements);
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.addAll(elements);
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb)
            leb.addAll(elements);
    }

    public List<HudElement> finish(int endSize, int profileID, int endLine, boolean endOfFile) {
        while (stack.size() > endSize) {
            if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
                stack.pop();
                addElement(mlb.build());
                Errors.addError(profileID, endLine+1, endOfFile ? "end of profile" : "end of section", ErrorType.CONDITIONAL_NOT_ENDED, "");
            }
            else if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
                stack.pop();
                addElement(leb.build());
                Errors.addError(profileID, endLine+1, endOfFile ? "end of profile" : "end of section", ErrorType.LIST_NOT_STARTED, "");
            }

        }
        return base;
    }

    public Supplier<List<?>> getSuppler() {
        for (int i = stack.size()-1; i >= 0; i--)
            if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb)
                return mlb.supplier;
        return null;
    }

}
