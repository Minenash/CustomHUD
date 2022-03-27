package com.minenash.customhud.mod_compat;

//import com.minenash.customhud.mixin.mod_compat.iris.ShadowRenderAccessor;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringIntSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
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
    public static final Supplier<String> SHADERPACK = () -> off() ? null : Iris.getCurrentPackName();
    public static final Supplier<String> SHADERPACK_PROFILE = () -> shaderPackInfo[0];
    public static final Supplier<String> SHADERPACK_CHANGES = () -> shaderPackInfo[1];
//    public static final Supplier<Number> SHADOW_HALF_PLANE = () -> off() ? null : shadow().getHalfPlaneLength();
//    public static final Supplier<Integer> SHADOW_RESOLUTION = () -> off() ? null : shadow().getResolution();
//    //Distance, Culling, terrain
//    public static final Supplier<Boolean> SHOULD_RENDER_TERRAIN = () -> off() ? null : shadow().getShouldRenderTerrain();
//    public static final Supplier<Boolean> SHOULD_RENDER_TRANSLUCENT = () -> off() ? null : shadow().getShouldRenderTranslucent();
//    public static final Supplier<Integer> SHADOW_ENTITIES = () -> off() || !shadow().getShouldRenderEntities() ? null : shadow().getRenderedShadowEntities();
//    public static final Supplier<Integer> SHADOW_BLOCK_ENTITIES = () -> off() || !shadow().getShouldRenderBlockEntities() ? null : shadow().getRenderedShadowBlockEntities();


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
        registerElement("iris_shaderpack_changes", (_str) -> new StringIntSupplierElement(SHADERPACK_CHANGES));

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
