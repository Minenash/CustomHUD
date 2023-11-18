import com.minenash.customhud.gui.NewConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NewConfigScreen::new;
    }

}
