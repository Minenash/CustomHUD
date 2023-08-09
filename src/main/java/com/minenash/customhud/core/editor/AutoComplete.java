package com.minenash.customhud.core.editor;

import com.minenash.customhud.core.registry.MetaData;
import com.minenash.customhud.core.registry.VariableRegistry;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import static com.minenash.customhud.core.editor.CompletionContext.*;

public class AutoComplete {

    public static void createCompletionProvider(RSyntaxTextArea area) {

        CHCompletionProvider provider = new CHCompletionProvider();

        provider.add(new TemplateCompletion(provider, "conditional", "Conditional", "{{${cond}, \"${true}\", \"${false}\"}}${cursor}"),
                START, NORMAL);

        for (MetaData meta : VariableRegistry.metadata.values().stream().distinct().toList()) {
            provider.add(new CHCompletion(provider, meta, false), START, NORMAL, VARIABLE);
            provider.add(new CHCompletion(provider, meta, true), CONDITIONAL);
        }


        provider.add(new TemplateCompletion(provider, "formatted", "Formatted", "-f${cursor}"), FLAGS);

        provider.add(new TemplateCompletion(provider, "uppercase", "Uppercase", "-uc${cursor}"), FLAGS);
        provider.add(new TemplateCompletion(provider, "lowercase", "Lowercase", "-lc${cursor}"), FLAGS);
        provider.add(new TemplateCompletion(provider, "titlecase", "Titlecase", "-tc${cursor}"), FLAGS);
        provider.add(new TemplateCompletion(provider, "smallcaps", "Small Caps", "-sc${cursor}"), FLAGS);
        provider.add(new TemplateCompletion(provider, "nodashes", "Remove Dashes", "-nd${cursor}"), FLAGS);

        provider.add(new TemplateCompletion(provider, "precision", "Change Precision", "-p${precision}${cursor}"), FLAGS);
        provider.add(new TemplateCompletion(provider, "scale", "Change Scale", "-s${scale}${cursor}"), FLAGS);

        AutoCompletion ac = new AutoCompletion(provider);
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(1);
        ac.setParameterAssistanceEnabled(true);
        ac.setAutoCompleteSingleChoices(false);
        ac.setShowDescWindow(true);
        ac.install(area);
    }



}
