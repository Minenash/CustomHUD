package com.minenash.customhud.HudElements.list;

import com.minenash.customhud.HudElements.HudElement;
import com.minenash.customhud.HudElements.icon.StatusEffectIconElement;
import com.minenash.customhud.HudElements.supplier.BooleanSupplierElement;
import com.minenash.customhud.HudElements.supplier.NumberSupplierElement;
import com.minenash.customhud.HudElements.supplier.SpecialSupplierElement;
import com.minenash.customhud.HudElements.supplier.StringSupplierElement;
import com.minenash.customhud.complex.ListManager;
import com.minenash.customhud.data.Flags;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatFormatter;
import org.apache.commons.lang3.text.WordUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.minenash.customhud.HudElements.supplier.NumberSupplierElement.of;

public abstract class ListAttributeSuppliers {

    private static StatusEffectInstance status() { return (StatusEffectInstance) ListManager.getValue(); }

    private static final StatFormatter HMS = ticks -> {
        int rawSeconds = ticks / 20;
        int seconds = rawSeconds % 60;
        int minutes = (rawSeconds / 60) % 60;
        int hours = (rawSeconds / 60 / 60);

        return hours > 0 ? String.format("%d:%02d:%02d", hours, minutes, seconds) : String.format("%d:%02d", minutes, seconds);
    };


    public static final Map<Supplier<?>, BiFunction<String,Flags,HudElement>> ATTRIBUTE_MAP = new HashMap<>();

    public static final Supplier<Number> COUNT = () -> ListManager.getCount();
    public static final Supplier<Number> INDEX = ListManager::getIndex;
    public static final Supplier<String> RAW = () -> ListManager.getValue().toString();


    public static final Supplier<String> STATUS_NAME = () -> I18n.translate(status().getTranslationKey());
    public static final Supplier<String> STATUS_ID = () -> Registries.STATUS_EFFECT.getId(status().getEffectType()).toString();
    public static final NumberSupplierElement.Entry STATUS_DURATION = of(() -> status().getDuration(), 0, HMS);
    public static final Supplier<Number> STATUS_AMPLIFICATION = () -> status().getAmplifier();

    public static final Supplier<Boolean> STATUS_AMBIENT = () -> status().isAmbient();
    public static final Supplier<Boolean> STATUS_SHOW_PARTICLES = () -> status().shouldShowParticles();
    public static final Supplier<Boolean> STATUS_SHOW_ICON = () -> status().shouldShowIcon();

    public static final SpecialSupplierElement.Entry STATUS_CATEGORY = new SpecialSupplierElement.Entry(
            () -> WordUtils.capitalize(status().getEffectType().getCategory().name().toLowerCase()),
            () -> status().getEffectType().getCategory().ordinal(),
            () -> status().getEffectType().getCategory().ordinal() != 1);


    static {
        ATTRIBUTE_MAP.put(ListSuppliers.STATUS_EFFECTS, (name, flags) -> switch (name) {
            case "name" -> new StringSupplierElement(STATUS_NAME);
            case "id" -> new StringSupplierElement(STATUS_ID);
            case "duration", "dur" -> new NumberSupplierElement(STATUS_DURATION, flags.scale, flags.precision, flags.formatted);
            case "amplification", "amp" -> new NumberSupplierElement(STATUS_AMPLIFICATION, flags.scale, flags.precision);
            case "ambient" -> new BooleanSupplierElement(STATUS_AMBIENT);
            case "show_particles", "particles" -> new BooleanSupplierElement(STATUS_SHOW_PARTICLES);
            case "show_icon" -> new BooleanSupplierElement(STATUS_SHOW_ICON);
            case "category", "cat" -> new SpecialSupplierElement(STATUS_CATEGORY);
            case "icon" -> new StatusEffectIconElement(flags, true);
            case "icon_no_bg" -> new StatusEffectIconElement(flags, false);
            default -> null;
        });
    }



}
