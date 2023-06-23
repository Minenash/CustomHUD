package com.minenash.customhud.core.registry;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.data.Flags;

public record VariableParseContext(
    String raw,
    String[] parts,

    String base,
    Flags flags,
    Enabled enabled,

    int profile,
    int line

) {

    public boolean startsWith(String str) {
        return base.startsWith(str);
    }

}
