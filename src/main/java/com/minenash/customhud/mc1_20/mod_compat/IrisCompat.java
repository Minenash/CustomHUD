package com.minenash.customhud.mc1_20.mod_compat;

import com.minenash.customhud.core.data.Enabled;
import com.minenash.customhud.core.registry.VariableRegistry;
import net.coderbot.iris.Iris;
import net.coderbot.iris.shaderpack.ShaderPack;

import java.util.Optional;
import java.util.function.Supplier;

import static com.minenash.customhud.core.registry.VariableRegistry.SupplierEntryType.*;

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

        VariableRegistry.register(Enabled.NONE, STRING, VERSION, "iris_version");
        VariableRegistry.register(Enabled.NONE, BOOLEAN, ENABLED, "iris_version");
        VariableRegistry.register(Enabled.NONE, STRING, SHADERPACK, "iris_version");
        VariableRegistry.register(Enabled.NONE, STRING, SHADERPACK_PROFILE, "iris_version");
        VariableRegistry.register(Enabled.NONE, STRING, SHADERPACK_CHANGES, "iris_version");

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
