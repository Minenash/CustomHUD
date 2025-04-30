//package com.minenash.customhud.gui;
//
//import com.minenash.customhud.ConfigManager;
//import com.minenash.customhud.ProfileManager;
//import com.minenash.customhud.data.Profile;
//import com.minenash.customhud.gui.customize.ToggleListWidget;
//import net.minecraft.client.gui.DrawContext;
//import net.minecraft.client.gui.screen.Screen;
//import net.minecraft.client.gui.widget.ButtonWidget;
//import net.minecraft.client.option.KeyBinding;
//import net.minecraft.client.util.InputUtil;
//import net.minecraft.screen.ScreenTexts;
//import net.minecraft.text.Text;
//
//import static com.minenash.customhud.CustomHud.CLIENT;
//
//public class TogglesScreen extends Screen implements ToggleListWidget.TLWParent {
//
//    private ToggleListWidget listWidget = null;
//    private final Screen parent;
//    public Profile profile;
//    public KeyBinding selectedKeybind;
//
//    @Override public Profile profile() { return profile; }
//    @Override public KeyBinding selectedKeybind() { return selectedKeybind; }
//    @Override public void selectedKeybind(KeyBinding selectedKeybind) { this.selectedKeybind = selectedKeybind; }
//
//    public TogglesScreen(Screen parent, Profile profile) {
//        super(Text.literal("'" + profile.name + "' Profile Toggles"));
//        this.parent = parent;
//        this.profile = profile;
//    }
//
//
//    public void changeProfile(Profile profile) {
//        this.profile = profile;
//        init();
//    }
//
//    public void init() {
//        children().clear();
//        this.listWidget = new ToggleListWidget(this, width, height);
//        this.addSelectableChild(listWidget);
//
//        this.addDrawableChild( ButtonWidget.builder(Text.literal("Open Profile"), button -> ProfileManager.open(profile))
//                .position(this.width / 2 - 155, this.height - 26).size(150, 20)
//                .tooltip(ProfileManager.openTooltip).build() );
//
//        this.addDrawableChild( ButtonWidget.builder(ScreenTexts.DONE, button -> CLIENT.setScreen(parent))
//                .position(this.width / 2 - 155 + 160, this.height - 26).size(150, 20).build() );
//
//        super.init();
//    }
//
//    @Override
//    public void close() {
//        CLIENT.setScreen(parent);
//        ConfigManager.save();
//    }
//
//    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
//        super.render(context, mouseX, mouseY, delta);
//
//        this.listWidget.render(context, mouseX, mouseY, delta);
//        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 11, 16777215);
//    }
//
//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        if (selectedKeybind != null) {
//            selectedKeybind.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
//            selectedKeybind = null;
//            for (ToggleListWidget.TEntry e : listWidget.children())
//                e.update();
//            ConfigManager.save();
//            return true;
//        }
//        return super.mouseClicked(mouseX, mouseY, button);
//    }
//
//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        if (selectedKeybind != null) {
//            selectedKeybind.setBoundKey(keyCode == 256 ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode));
//            selectedKeybind = null;
//            for (ToggleListWidget.TEntry e : listWidget.children())
//                e.update();
//            ConfigManager.save();
//            return true;
//        }
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//
//}