package com.minenash.customhud.mc1_20.mod_compat;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.registry.MetaData;
import com.minenash.customhud.core.registry.VariableRegistry;
import net.coderbot.iris.Iris;
import net.coderbot.iris.shaderpack.ShaderPack;

import java.util.Optional;
import java.util.function.Supplier;

import static com.minenash.customhud.core.data.Enabled.*;
import static com.minenash.customhud.core.registry.MetaData.DefaultCategories.FROM_MODS;
import static com.minenash.customhud.core.registry.VariableRegistry.SupplierEntryType.*;
import static com.minenash.customhud.core.registry.VariableRegistry.register;

public class IrisCompat {

    private static final String[] shaderPackInfo = new String[2];

    public static final Supplier<String> VERSION = Iris::getVersion;
    public static final Supplier<Boolean> ENABLED = () -> !off();
    public static final Supplier<String> SHADERPACK = () -> off() ? null : Iris.getCurrentPackName();
    public static final Supplier<String> SHADERPACK_PROFILE = () -> shaderPackInfo[0];
    public static final Supplier<String> SHADERPACK_CHANGES = () -> shaderPackInfo[1];

    public static boolean off() {
        return !Iris.getIrisConfig().areShadersEnabled();
    }

    public static void registerCompat() {

        register(NONE, STRING, VERSION, "iris_version").meta(FROM_MODS, "v2.0", "Iris Version", "");
        register(NONE, BOOLEAN, ENABLED, "iris_enabled").meta(FROM_MODS, "v2.0", "Iris Enabled", "If Iris's shaders are enabled"); //TODO: Add to docs
        register(NONE, STRING, SHADERPACK, "iris_shaderpack").meta(FROM_MODS, "v2.0", "Iris Shaderpack", "Shaderpack file loaded");
        register(NONE, STRING, SHADERPACK_PROFILE, "iris_shaderpack_profile").meta(FROM_MODS, "v2.0", "Iris Shaderpack Profile", "Shaderpack profile loaded");
        register(NONE, STRING, SHADERPACK_CHANGES, "iris_shaderpack_changes").meta(FROM_MODS, "v2.0", "Iris Shaderpack Changes", "How many changes the user made to the profile");

        VariableRegistry.registerComplexData("iris:get_pack_info", () -> {
            Optional<ShaderPack> pack = Iris.getCurrentPack();
            if (!off() && pack.isPresent()) {
                String info = pack.get().getProfileInfo();
                int indexOfPParenthesis = info.indexOf("(+");
                shaderPackInfo[0] = info.substring(9, indexOfPParenthesis);
                shaderPackInfo[1] = info.substring(indexOfPParenthesis + 2, info.indexOf(" option"));
            }
            else {
                shaderPackInfo[0] = null;
                shaderPackInfo[1] = null;
            }

        });

    }



}
