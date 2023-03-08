package com.minenash.customhud.HudElements;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.HudElements.supplier.*;
import com.minenash.customhud.errors.ErrorType;
import com.minenash.customhud.mixin.GameOptionsAccessor;
import com.minenash.customhud.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import net.minecraft.util.TranslatableOption;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SettingsElement {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static boolean initialized = false;

    //Boolean, Integer, Double
    private static final Map<String, SimpleOption<?>> simpleOptions = new HashMap<>();
    private static final Map<String, Integer> staticIntOptions = new HashMap<>();

    private static void init() {
        ((GameOptionsAccessor)MinecraftClient.getInstance().options).invokeAccept(new GameOptions.Visitor() {
            @Override
            public <T> void accept(String key, SimpleOption<T> option) {
                simpleOptions.put(key.toLowerCase(), option);
            }

            @Override
            public int visitInt(String key, int current) {
                staticIntOptions.put(key.toLowerCase(), current); return current;
            }

            @Override
            public boolean visitBoolean(String key, boolean current) {
                return current;
            }

            @Override
            public String visitString(String key, String current) {
                return current;
            }

            @Override
            public float visitFloat(String key, float current) {
                return current;
            }

            @Override
            public <T> T visitObject(String key, T current, Function<String, T> decoder, Function<T, String> encoder) {
                return current;
            }
        });
    }

    public static Pair<HudElement,Pair<ErrorType,String>> create(String setting, Flags flags) {
        if (!initialized)
            init();
        initialized = true;
        setting = setting.toLowerCase();

        if (setting.equals("max_fps"))
            return new Pair<>(new NumberSupplierElement(IntegerSuppliers.MAX_FPS, flags.scale, flags.precision), null);

        if (setting.startsWith("lang")) {
            HudElement element = switch (setting.substring(4)) {
                case "" -> new StringSupplierElement(() -> client.getLanguageManager().getLanguage().getName());
                case "_region" -> new StringSupplierElement(() -> client.getLanguageManager().getLanguage().getRegion());
                case "_code" -> new StringSupplierElement(() -> client.getLanguageManager().getLanguage().getCode());
                default -> null;
            };
            if (element == null)
                return new Pair<>(null, new Pair<>(ErrorType.UNKNOWN_SETTING, setting));
            return new Pair<>(flags.anyTextUsed() ? new FormattedElement(element, flags) : element, null);
        }

        if (setting.startsWith("key."))
            setting = "key_" + setting;
        else if (setting.startsWith("sound_"))
            setting = "soundcategory_" + setting.substring(6);

        GameOptions options = MinecraftClient.getInstance().options;
        if (setting.startsWith("key_")) {
            String key = setting.substring(4);
            for (KeyBinding binding : options.allKeys)
                if (binding.getTranslationKey().equalsIgnoreCase(key))
                    return new Pair<>(new SpecialSupplierElement(SpecialSupplierElement.of(
                            () -> binding.getBoundKeyLocalizedText().getString(),
                            () -> ((KeyBindingAccessor) binding).getBoundKey().getCode(),
                            () -> !binding.isUnbound()
                    )), null);
            return new Pair<>(null, new Pair<>(ErrorType.UNKNOWN_KEYBOARD_KEY, key));
        }

        if (setting.startsWith("soundcategory_")) {
            String cat = setting.substring(14);
            for (SoundCategory soundCategory : SoundCategory.values())
                if (soundCategory.getName().equalsIgnoreCase(cat))
                    return new Pair<>(new NumberSupplierElement(NumberSupplierElement.of(
                            () -> ((GameOptionsAccessor)options).getSoundVolumeLevels().get(soundCategory) * 100,
                            flags.precision != -1 ? flags.precision : 0), flags.scale), null);
            return new Pair<>(null,new Pair<>(ErrorType.UNKNOWN_SOUND_CATEGORY, cat));
        }

        SimpleOption<?> option = simpleOptions.get(setting);
        if (option != null)
            return new Pair<>(getSimpleOptionElement(option, flags), null);

        if (staticIntOptions.containsKey(setting)) {
            int value = staticIntOptions.get(setting);
            return new Pair<>(new NumberSupplierElement(() -> value, flags.scale, 0),null);
        }

        return new Pair<>(null, new Pair<>(ErrorType.UNKNOWN_SETTING, setting));

    }

    private static HudElement getSimpleOptionElement(SimpleOption<?> option, Flags flags) {
        System.out.println("Option: " + option.toString() + " | " + option.getValue().getClass().getName());
        if (option.getValue() instanceof Boolean)
            return new BooleanSupplierElement(() -> (Boolean) option.getValue());
        if (option.getValue() instanceof Number)
            return new NumberSupplierElement(() -> (Number) option.getValue(), flags.scale, flags.precision != -1 ? flags.precision : option.getValue() instanceof Integer ? 0 : 1);
        if (option.getValue() instanceof String) {
            HudElement element = new StringSupplierElement(() -> ((String)option.getValue()).isEmpty() ? "Default" : (String)option.getValue());
            return flags.anyTextUsed() ? new FormattedElement(element, flags) : element;
        }
        if (option.getValue() instanceof TranslatableOption) {
            final int falseValue = getFalseValue((TranslatableOption) option.getValue());
            return new SpecialSupplierElement(SpecialSupplierElement.of(
                    () -> ((TranslatableOption)option.getValue()).getText().getString(),
                    ((TranslatableOption) option.getValue())::getId,
                    () -> ((TranslatableOption)option.getValue()).getId() != falseValue
            ));
        }
        if (option.getValue() instanceof NarratorMode)
            return new SpecialSupplierElement(SpecialSupplierElement.of(
                    () -> ((NarratorMode) option.getValue()).getName().getString(),
                    () -> ((NarratorMode) option.getValue()).getId(),
                    () -> ((NarratorMode) option.getValue()).getId() != 0
            ));
        return null;
    }

    private static int getFalseValue(TranslatableOption option) {
        if (option instanceof ParticlesMode || option instanceof ChatVisibility)
            return 2;
        if (option instanceof Arm)
            return 1;
        return 0; // GraphicsMode, AoMode, ChunkBuilderMode, CloudRenderMode, AttackIndicator

    }

}
