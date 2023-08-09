package com.minenash.customhud.core.editor;


import com.minenash.customhud.core.elements.FormattedElement;
import com.minenash.customhud.core.registry.MetaData;
import org.fife.ui.autocomplete.*;

public class CHCompletion extends AbstractCompletion  {

    public final MetaData meta;
    private final String infobox;
    private final String textForSearch;
    private final boolean isInConditional;

    protected CHCompletion(CompletionProvider provider, MetaData meta, boolean isInConditional) {
        super(provider);
        this.meta = meta;
        this.infobox = infobox();
        this.textForSearch = String.join(" ", meta.vars());
        this.isInConditional = isInConditional;
    }

    @Override public String getReplacementText() { return isInConditional ? meta.vars()[0] : "{" + meta.vars()[0] + "}"; }
    @Override public String getInputText() { return textForSearch; }
    @Override public String getSummary() { return infobox; }
    @Override public String toString() { return meta.name(); }

    private String infobox() {
        return "<b>" + FormattedElement.smallcaps(meta.cat().toLowerCase()) + "</b>"
                + "<h2 style='padding-bottom: 0; margin-bottom: 4px;'>" + meta.name() + "</h2>"
                + "<b>ᴠᴀʀs: </b>{" + String.join("}, {", meta.vars() ) + "}"
                + "<br><b>ꜰʟᴀɢs: </b><a href=''>" + meta.flags() + "</a>"
                + "<br><b style='display:absolute; top: 0;'>ᴀᴅᴅᴇᴅ: </b>" + meta.versionAdded()
                + "<br><p>" + meta.desc() + "</p>";
    }
}
