package com.minenash.customhud.data;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.HudElements.ConditionalElement;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.HudElements.functional.FunctionalElement;
import com.minenash.customhud.HudElements.list.Attributers;
import com.minenash.customhud.HudElements.list.ListElement;
import com.minenash.customhud.HudElements.list.ListProvider;
import com.minenash.customhud.HudElements.list.ListProviderSet;
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
    public final ListProviderSet listProviders = new ListProviderSet();

    public void startIf(String cond, Profile profile, int line, String source, ComplexData.Enabled enabled) {
        Operation op = ExpressionParser.parseExpression(cond, source, profile, line, enabled, listProviders, true);
        stack.push(new ConditionalElement.MultiLineBuilder(op));
    }

    public void elseIf(String cond, Profile profile, int line, String source, ComplexData.Enabled enabled) {
        if (stack.isEmpty())
            Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "elseif");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(ExpressionParser.parseExpression(cond, source, profile, line, enabled, listProviders, true));
        else {
            boolean success = false;
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profile, line, false) );
                    ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(ExpressionParser.parseExpression(cond, source, profile, line, enabled, listProviders, true));
                    success = true;
                    break;
                }
            }
            if (!success) {
                Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "elseif");
            }
        }
    }

    public void else1(Profile profile, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "else");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.setConditional(new Operation.Literal(1));
        else {
            boolean success = false;
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profile, line, false) );
                    ( (ConditionalElement.MultiLineBuilder)stack.peek() ).setConditional(new Operation.Literal(1));
                    success = true;
                    break;
                }
            }
            if (!success) {
                Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "else");
            }
        }

    }

    public void endIf(Profile profile, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "end");
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
            HudElement element = mlb.build();
            stack.pop();
            addElement(element);
        }
        else {
            boolean success = false;
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ConditionalElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profile, line, false) );
                    HudElement element = ( (ConditionalElement.MultiLineBuilder)stack.peek() ).build();
                    stack.pop();
                    addElement(element);
                    success = true;
                    break;
                }
            }
            if (!success) {
                Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "end");
            }

        }
    }

    public void startFor(String list, Profile profile, int line, ComplexData.Enabled enabled, String source) {
        List<String> parts = VariableParser.partitionConditional(list);
        list = parts.get(0);
        boolean reverse = false;

        ListProviderSet.Entry provider = VariableParser.getListProvider(list, profile, line, enabled, source, listProviders);
        if (provider != null && provider.provider() == ListProvider.REGUIRES_MODMENU) {
            Errors.addError(profile.name, line, source, ErrorType.REQUIRES_MODMENU, "");
            listProviders.push(null);
            stack.push( new ListElement.MultiLineBuilder(null, null, null, false) );
            return;
        }

        if (provider == null) {
            HudElement e = VariableParser.getAttributeElement(list, profile, line, enabled, source);
            if (e instanceof FunctionalElement.IgnoreErrorElement) {
                listProviders.push(null);
                stack.push( new ListElement.MultiLineBuilder(null, null, null, false) );
                return;
            }
            if (e instanceof FunctionalElement.CreateListElement cle)
                provider = cle.entry;
        }

        if (provider == null && !listProviders.isEmpty()) {
            String[] nameParts = list.split(" ");
            String first = nameParts[0];
            Flags flags = Flags.parse(profile.name, line, nameParts);
            HudElement e = Attributers.getFromPrefix(listProviders, first, flags, profile, line);
            if (e == null)
                e = Attributers.get(listProviders, first, flags, profile, line);
            if (e instanceof FunctionalElement.CreateListElement cle) {
                provider = cle.entry;
            }
            reverse = flags.reverseList;

        }

        listProviders.push(provider);

        if (provider == null) {
            Errors.addError(profile.name, line, source, ErrorType.UNKNOWN_LIST, list);
            stack.push( new ListElement.MultiLineBuilder(null, null, null, false) );
            return;
        }

        Operation filter = null;
        if (parts.size() > 1) {
            filter = ExpressionParser.parseExpression(parts.get(1), source, profile, line, enabled, listProviders, true);
            CustomHud.logInDebugMode("Filter:");
            filter.printTree(2);
        }

        stack.push( new ListElement.MultiLineBuilder(provider.provider(), provider.id(), filter, reverse) );
    }

    public void forSeparator(Profile profile, int line, String source) {
        if (stack.isEmpty())
            Errors.addError(profile.name, line, source, ErrorType.LIST_NOT_STARTED, "=separator=");
        else if (stack.peek() instanceof ListElement.MultiLineBuilder mlb)
            mlb.separatorMode();
        else {
            boolean success = false;
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profile, line, false) );
                    ( (ListElement.MultiLineBuilder)stack.peek() ).separatorMode();
                    success = true;
                    break;
                }
            }
            if (!success) {
                Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "separator");
            }
        }
    }

    public void endFor(Profile profile, int line, String source) {
        if (stack.isEmpty()) {
            Errors.addError(profile.name, line, source, ErrorType.LIST_NOT_STARTED, "");
            return;
        }
        if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
            HudElement element = leb.build();
            stack.pop();
            addElement(element);
        }
        else {
            boolean success = false;
            for (int i = stack.size()-1; i >= 0; i--) {
                if (stack.get(i) instanceof ListElement.MultiLineBuilder mlb) {
                    mlb.addAll( finish(i+1, profile, line, false) );
                    HudElement element = ( (ListElement.MultiLineBuilder)stack.peek() ).build();
                    stack.pop();
                    addElement(element);
                    success = true;
                    break;
                }
            }
            if (!success) {
                Errors.addError(profile.name, line, source, ErrorType.CONDITIONAL_NOT_STARTED, "end");
            }
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

    public void addElements(String source, Profile profile, int line, ComplexData.Enabled enabled) {
        List<HudElement> elements = VariableParser.addElements(source, profile, line, enabled, true, listProviders);
        if (stack.empty())
            base.addAll(elements);
        else if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb)
            mlb.addAll(elements);
        else if (stack.peek() instanceof ListElement.MultiLineBuilder leb)
            leb.addAll(elements);
    }

    public List<HudElement> finish(int endSize, Profile profile, int endLine, boolean endOfFile) {
        while (stack.size() > endSize) {
            if (stack.peek() instanceof ConditionalElement.MultiLineBuilder mlb) {
                stack.pop();
                addElement(mlb.build());
                Errors.addError(profile.name, endLine, endOfFile ? "end of profile" : "end of section", ErrorType.CONDITIONAL_NOT_ENDED, "");
            }
            else if (stack.peek() instanceof ListElement.MultiLineBuilder leb) {
                stack.pop();
                addElement(leb.build());
                Errors.addError(profile.name, endLine, endOfFile ? "end of profile" : "end of section", ErrorType.LIST_NOT_STARTED, "");
            }

        }
        return base;
    }

}
