package com.minenash.customhud.core.registry;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.data.Flags;
import com.minenash.customhud.core.data.Profile;

public record VariableParseContext(
    String raw,
    String[] parts,

    String base,
    Flags flags,
    Enabled enabled,
    Profile profile,

    int profileNum,
    int line

) {

    public boolean startsWith(String str) {
        return base.startsWith(str);
    }

}
