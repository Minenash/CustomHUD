package com.minenash.customhud.gui.customize;

import com.minenash.customhud.ConfigManager;
import com.minenash.customhud.ProfileManager;
import com.minenash.customhud.data.Profile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Consumer;

import static com.minenash.customhud.CustomHud.CLIENT;
import static net.minecraft.client.gui.widget.ButtonWidget.builder;

@Environment(EnvType.CLIENT)
public class CustomizeScreen extends Screen {
    public static final Identifier TAB_HEADER_BACKGROUND_TEXTURE = Identifier.ofVanilla("textures/gui/tab_header_background.png");
    private final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
    private final TabManager tabManager = new TabManager(this::addDrawableChild, this::remove);
    private TabNavigationWidget tabNavigation;

    private final String initialTab;
    private final Text profileName;

    public final Screen parent;
    public Profile profile;
    public KeyBinding selectedKeybind;


    public CustomizeScreen(Screen parent, @NotNull Profile profile, String initialTab) {
        super(Text.literal("CustomHud Customize"));
        this.parent = parent;
        this.initialTab = initialTab;
        this.profile = profile;
        this.profileName = Text.literal(profile.name).setStyle(Style.EMPTY.withUnderline(true));
    }

    public void refresh() { changeProfile(profile); }
    public void changeProfile(Profile profile) {
        CLIENT.setScreen( new CustomizeScreen(parent, profile, ((CSTab)tabManager.getCurrentTab()).id) );
    }

    @Override
    public void init() {
        var tabs = new ArrayList<CSTab>();
        if (!profile.options.isEmpty())
            tabs.add( new CSTab("options", new OptionListWidget(CustomizeScreen.this, width, height), Text.literal("Options"), true) );
        if (!profile.toggles.isEmpty())
            tabs.add( new CSTab("toggles", new ToggleListWidget(CustomizeScreen.this, width, height), Text.literal("Toggles"), true) );

        this.tabNavigation = this.addDrawableChild(TabNavigationWidget.builder(this.tabManager, this.width).tabs(tabs.toArray(new Tab[0])).build());
        
        var footer = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        footer.add(builder(Text.literal("Edit Profile"), button -> ProfileManager.open(profile)).build());
        footer.add(builder(ScreenTexts.DONE, button -> close()).build());
        
        
        this.layout.forEachChild(child -> {
            child.setNavigationOrder(1);
            this.addDrawableChild(child);
        });

        boolean set = false;
        if (initialTab != null)
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i).id.equals(initialTab)) {
                    this.tabNavigation.selectTab(i, false);
                    set = true;
                }
            }
        if (!set)
            this.tabNavigation.selectTab(0, false);
        this.initTabNavigation();
    }

    @Override
    protected void setInitialFocus() {}

    @Override
    public void initTabNavigation() {
        if (this.tabNavigation != null) {
            this.tabNavigation.setWidth(this.width);
            this.tabNavigation.init();
            int i = this.tabNavigation.getNavigationFocus().getBottom();
            ScreenRect screenRect = new ScreenRect(0, i, this.width, this.height - this.layout.getFooterHeight() - i);
            this.tabManager.setTabArea(screenRect);
            this.layout.setHeaderHeight(i);
            this.layout.refreshPositions();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(keyCode == 256 ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode));
            selectedKeybind = null;
            ((CSTab)tabManager.getCurrentTab()).widget.updateChildren();
            ConfigManager.save();
            return true;
        }
        if (this.tabNavigation.trySwitchTabsWithKey(keyCode))
            return true;
        else return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedKeybind != null) {
            selectedKeybind.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
            selectedKeybind = null;
            ((CSTab)tabManager.getCurrentTab()).widget.updateChildren();
            ConfigManager.save();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
        ConfigManager.save();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        RenderSystem.enableBlend();
        context.drawTexture(Screen.FOOTER_SEPARATOR_TEXTURE, 0, this.height - this.layout.getFooterHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
        RenderSystem.disableBlend();

        var matrices = context.getMatrices();
        matrices.push();
        matrices.translate(8, height-layout.getFooterHeight() - 1 - 8 , 0);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotation((float) (Math.PI/2)));
        context.drawText(textRenderer, profileName, 0, 0, 0xFFFFFFFF, false);
        matrices.pop();


//        context.drawText(textRenderer, profile.name, 4, 8, 0xFFFFFFFF, true);
    }

    @Override
    protected void renderDarkening(DrawContext context) {
        context.drawTexture(TAB_HEADER_BACKGROUND_TEXTURE, 0, 0, 0.0F, 0.0F, this.width, this.layout.getHeaderHeight(), 16, 16);
        this.renderDarkening(context, 0, this.layout.getHeaderHeight(), this.width, this.height);
    }

    @Environment(EnvType.CLIENT)
    public class CSTab implements Tab {
        private final CSTabWidget widget;
        private final Text title;
        public final boolean darkenTabButton;
        public final String id;

        public CSTab(String id, CSTabWidget widget, Text title, boolean darken) {
            this.id = id;
            this.widget = widget;
            this.title = title;
            this.darkenTabButton = darken;
        }

        @Override
        public Text getTitle() {
            return title;
        }

        @Override
        public void forEachChild(Consumer<ClickableWidget> consumer) {
            widget.forEachChild(consumer);
        }

        @Override
        public void refreshGrid(ScreenRect tabArea) {
            widget.updateSize(width, height);
        }
    }
}
