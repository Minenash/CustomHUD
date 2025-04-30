package com.minenash.customhud.mixin;

import com.minenash.customhud.gui.customize.CustomizeScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.TabButtonWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.minenash.customhud.CustomHud.CLIENT;
import static com.minenash.customhud.gui.customize.ToggleListWidget.INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
import static com.minenash.customhud.gui.customize.ToggleListWidget.MENU_LIST_BACKGROUND_TEXTURE;

@Mixin(TabButtonWidget.class)
public class TabButtonWidgetMixin {

    @Shadow @Final private Tab tab;

    @Inject(method = "renderBackgroundTexture", at = @At(value = "TAIL"))
    public void renderBackgroundTexture(DrawContext context, int left, int top, int right, int bottom, CallbackInfo ci) {
        if (tab instanceof CustomizeScreen.CSTab cst && cst.darkenTabButton) {
            var texture =  CLIENT.world == null ? MENU_LIST_BACKGROUND_TEXTURE : INWORLD_MENU_LIST_BACKGROUND_TEXTURE;
            Screen.renderBackgroundTexture(context, texture, left, top, 0.0F, 0.0F, right - left, bottom - top);
        }
    }
}
