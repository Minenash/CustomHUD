package com.minenash.customhud.core.editor;

import org.fife.ui.autocomplete.AbstractCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.*;
import java.util.List;

public class CHCompletionProvider extends DefaultCompletionProvider {

    Map<CompletionContext, List<Completion>> contextCompletions = new HashMap<>();

    public CHCompletionProvider() {
        setListCellRenderer(new CHListCellRenderer());
        setAutoActivationRules(true, "{ }=:");
    }

    public void add(Completion completion, CompletionContext... contexts) {
        for (CompletionContext context : contexts) {
            contextCompletions.putIfAbsent(context, new ArrayList<>());
            contextCompletions.get(context).add(completion);
        }
    }

    @Override
    protected boolean isValidChar(char ch) {
        return super.isValidChar(ch) || ch == '{' || ch == '}';
    }

    @Override
    protected List<Completion> getCompletionsImpl(JTextComponent comp) {
        List<Completion> retVal = new ArrayList<>();
        String text = getAlreadyEnteredText(comp);
        if (text == null) return new ArrayList<>();

        text = text.toLowerCase();
        if (text.startsWith("{"))
            text = text.substring(1);

        for (Completion ce : contextCompletions.getOrDefault(CompletionContext.getContextAtCursor(comp, seg), Collections.emptyList()))
            if (ce.getInputText().toLowerCase().contains(text)) {
                retVal.add(ce);
                if (ce instanceof AbstractCompletion ac)
                    ac.setRelevance( getRelevance(ce, text) );
            }

        return retVal;
    }

    private static int getRelevance(Completion completion, String prefix) {
        int closeness = Integer.MAX_VALUE;
        for (String var : completion.getInputText().split(" ")) {
            String[] parts = var.split("_");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].startsWith(prefix)) {
                    int c = i * 1000;
                    if (c < closeness)
                        closeness = c;
                }
                int c = var.compareToIgnoreCase(prefix) + (1000*(10+i));
                if (c < closeness)
                    closeness = c;
            }
        }
        return Integer.MAX_VALUE - closeness - (completion instanceof CHCompletion cc && cc.meta.cat().equals("Deprecated") ? 100000 : 0);
    }

    public static class CHListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel label && value instanceof CHCompletion comp && comp.meta.cat().equals("Deprecated")) {
                Map attribute = label.getFont().getAttributes();
                attribute.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                label.setFont(label.getFont().deriveFont(attribute));
            }
            return c;
        }
    }

}
