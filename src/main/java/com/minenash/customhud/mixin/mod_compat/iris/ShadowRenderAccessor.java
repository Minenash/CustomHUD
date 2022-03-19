//package com.minenash.customhud.mixin.mod_compat.iris;
//
//import net.coderbot.iris.mixin.LevelRendererAccessor;
//import net.coderbot.iris.pipeline.ShadowRenderer;
//import net.minecraft.client.render.Camera;
//import net.minecraft.client.render.WorldRenderer;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.gen.Accessor;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(ShadowRenderer.class)
//public abstract class ShadowRenderAccessor {
//
//    @Accessor abstract boolean getShouldRenderTerrain();
//    @Accessor abstract boolean getShouldRenderTranslucent();
//    @Accessor abstract boolean getShouldRenderEntities();
//    @Accessor abstract boolean getShouldRenderBlockEntities();
//    @Accessor abstract int getRenderedShadowEntities();
//    @Accessor abstract int getRenderedShadowBlockEntities();
//    @Accessor abstract int getResolution();
//    @Accessor abstract float getHalfPlaneLength();
//
//    static WorldRenderer renderer = null;
//
//    @Inject(method = "renderShadows", at = @At("HEAD"))
//    private void customhud$getWorldRenderer(LevelRendererAccessor levelRenderer, Camera _camera, CallbackInfo _callback) {
//        renderer = (WorldRenderer) levelRenderer;
//        renderer.getChunksDebugString();
//    }
//
//
//}
