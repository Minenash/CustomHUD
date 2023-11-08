package com.minenash.customhud.mod_compat;

//import com.minenash.customhud.mixin.mod_compat.iris.ShadowRenderAccessor;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.NumberSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
import com.minenash.customhud.data.Flags;
import net.coderbot.iris.Iris;
import net.coderbot.iris.shaderpack.ShaderPack;

import java.util.Optional;
import java.util.function.Supplier;

import static com.minenash.customhud.mod_compat.CustomHudRegistry.registerComplexData;
import static com.minenash.customhud.mod_compat.CustomHudRegistry.registerElement;

public class IrisCompat {

    private static final String[] shaderPackInfo = new String[2];

    public static final Supplier<String> VERSION = Iris::getVersion;
    public static final Supplier<Boolean> ENABLED = () -> !off();
    public static final Supplier<String> SHADERPACK = () -> {
        if (off()) return null;
        String name = Iris.getCurrentPackName();
        name = name.replace('_', ' ');
        return name.endsWith(".zip") ? name.substring(0, name.length()-4) : name;
    };
    public static final Supplier<String> SHADERPACK_PROFILE = () -> shaderPackInfo[0];
    public static final Supplier<Number> SHADERPACK_CHANGES = () -> Integer.parseInt(shaderPackInfo[1]);


    public static boolean off() {
        return !Iris.getIrisConfig().areShadersEnabled();
    }

//    public static ShadowRenderAccessor shadow() {
//        return (ShadowRenderAccessor)Iris.getPipelineManager().getPipeline().get();
//    }

    public static void registerCompat() {

        registerElement("iris_version", (_str) -> new StringSupplierElement(VERSION));
        registerElement("iris_enabled", (_str) -> new BooleanSupplierElement(ENABLED));
        registerElement("iris_shaderpack", (_str) -> new StringSupplierElement(SHADERPACK));
        registerElement("iris_shaderpack_profile", (_str) -> new StringSupplierElement(SHADERPACK_PROFILE));
        registerElement("iris_shaderpack_changes", (_str) -> new NumberSupplierElement(SHADERPACK_CHANGES, 1, 0));

        registerComplexData(() -> {
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
