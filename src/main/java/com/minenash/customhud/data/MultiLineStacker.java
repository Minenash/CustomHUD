package com.minenash.customhud.data;

import com.minenash.customhud.HudElements.ConditionalElement;
import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.list.ListElement;
import com.minenash.customhud.HudElements.list.ListProvider;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.conditionals.ExpressionParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.errors.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MultiLineStacker {

    private final List<HudElement> base = new ArrayList<>();
    private final Stack<Object> stack = new Stack<>();
    private final Stack<ListProvider> listProviders = new Stack<>();

    public void startIf(String cond, String profileName, int line, String source, ComplexData.Enabled enabled) {
        Operation op = ExpressionParser.parseExpression(cond, source, profileName, line+1, enabled, getProvider());
        stack.push(new ConditionalElement.MultiLineBuilder(op));
    }

    public void elseIf(String cond, String profileName, int line, String source, ComplexData.Enabled enabled) {
        if (stack.isEmpty())
            Errors.addError(profileName, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "else if");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(ExpressionParser.parseExpression(cond, source, profileName, line + 1, enabled, getProvider()));
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileName, line, false) );
                    break;
                }
            }
            ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(ExpressionParser.parseExpression(cond, source, profileName, line + 1, enabled, getProvider()));
        }
    }

    public void else1(String profileName, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profileName, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "=else=");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(new Operation.Literal(1));
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileName, line, false) );
                    break;
                }
            }
            ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(new Operation.Literal(1));
        }

    }

    public void endIf(String profileName, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profileName, line+1, source, ErrorType.CONDITIONAL_NOT_STARTED, "end");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
            HudElement element = mlb.build();
            stack.pop();
            addElement(element);
        }
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileName, line, false) );
                    break;
                }
            }
            HudElement element = ( (ConditionalElement.MultiLineBuilder)stack.peek() ).build();
            stack.pop();
            addElement(element);
        }
    }

    public void startFor(String list, String profileName, int line, ComplexData.Enabled enabled, String source) {
        ListProvider provider = VariableParser.getListProvider(list, profileName, line, enabled, source, listProviders.isEmpty() ? null : listProviders.peek());
        listProviders.push(provider);
        stack.push( new ListElement.MultiLineBuilder(provider) );
    }

    public void endFor(String profileName, int line, String source) {
        if (stack.isEmpty()) {
            Errors.addError(profileName, line + 1, source, ErrorType.LIST_NOT_STARTED, "");
            return;
        }
        if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
            HudElement element = leb.build();
            stack.pop();
            addElement(element);
        }
        else {
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profileName, line, false) );
                    break;
                }
            }
            HudElement element = ( (ListElement.MultiLineBuilder)stack.peek() ).build();
            stack.pop();
            addElement(element);
        }
        listProviders.pop();
    }

    public void addElement(HudElement element) {
        if (stack.empty())
            base.add(element);
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.add(element);
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb)
            leb.add(element);
    }

    public void addElements(String source, String profileName, int line, ComplexData.Enabled enabled) {
        List<HudElement> elements = VariableParser.addElements(source, profileName, line + 1, enabled, true, getProvider());
        if (stack.empty())
            base.addAll(elements);
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.addAll(elements);
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb)
            leb.addAll(elements);
    }

    public List<HudElement> finish(int endSize, String profileName, int endLine, boolean endOfFile) {
        while (stack.size() > endSize) {
            if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
                stack.pop();
                addElement(mlb.build());
                Errors.addError(profileName, endLine+1, endOfFile ? "end of profile" : "end of section", ErrorType.CONDITIONAL_NOT_ENDED, "");
            }
            else if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
                stack.pop();
                addElement(leb.build());
                Errors.addError(profileName, endLine+1, endOfFile ? "end of profile" : "end of section", ErrorType.LIST_NOT_STARTED, "");
            }

        }
        return base;
    }

    public ListProvider getProvider() {
        for (int i = stack.size()-1; i >= 0; i--)
            if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb)
                return mlb.provider;
        return null;
    }

}
