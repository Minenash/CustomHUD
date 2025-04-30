package com.minenash.customhud.data;

import com.google.gson.JsonObject;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.apache.commons.lang3.math.NumberUtils;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.UUID;

import static com.minenash.customhud.VariableParser.inQuotes;

public class ProfileOption {

    public enum OnOff {ON, OFF;
        public static OnOff toggle(Object onOff) { return onOff == ON ? OFF : ON; }
        public static OnOff parse(String str) { return str != null && (str.equalsIgnoreCase("on") || str.equalsIgnoreCase("true"))? ON : OFF; }
    }

    public final String id;
    public final String type;
    public final String name;
    public final Text tooltip;
    public Object value;

    public ProfileOption(String id, String type, String name, String tooltip, Object defaultValue) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.tooltip = tooltip == null ? null : Text.literal(tooltip);
        this.value = defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return (T) value;
    }

    public static ProfileOption fromProfile(String id, String name, String tooltip, String type, String value) {
        return new ProfileOption(id, type, name, tooltip, switch (type) {
            case "bool" -> Boolean.parseBoolean(value);
            case "onoff" -> ProfileOption.OnOff.parse(value);
            case "int" -> NumberUtils.toInt(value, 0);
            case "decimal" -> NumberUtils.toDouble(value, 0);
            case "color" -> new Color(HudTheme.parseColor(value, 0xFFFFFFFF));
            case "string" ->value == null ? "" : inQuotes(value) ? value.substring(1, value.length() - 1) : value;
            case "keybind" -> newKeybind();
            default -> null;
        });
    }

    public JsonObject serializePref(String profile) {
        JsonObject json = new JsonObject();
        json.addProperty("profile", profile);
        json.addProperty("id", id);
        json.addProperty("type", type);
        switch (type) {
            case "bool"    -> json.addProperty("value", (Boolean) value);
            case "onoff"   -> json.addProperty("value", value == OnOff.ON);
            case "int"     -> json.addProperty("value", (Integer) value);
            case "decimal" -> json.addProperty("value", (Double) value);
            case "color"   -> json.addProperty("value", ((Color) value).getRGB());
            case "string"  -> json.addProperty("value", (String) value);
            case "keybind" -> json.addProperty("value", ((KeyBinding) value).getBoundKeyTranslationKey());
        }
        return json;
    }

    public void applyPref(JsonObject json) {
        if (!type.equals(json.get("type").getAsString())) return;
        var pref = json.get("value");
        try {
            value = switch (type) {
                case "bool" -> pref.getAsBoolean();
                case "onoff" -> pref.getAsBoolean() ? OnOff.ON : OnOff.OFF;
                case "int" -> pref.getAsInt();
                case "decimal" -> pref.getAsDouble();
                case "color" -> new Color(pref.getAsInt());
                case "string" -> pref.getAsString();
                case "keybind" -> {
                    var keybind = newKeybind();
                    keybind.setBoundKey(InputUtil.fromTranslationKey(pref.getAsString()));
                    yield keybind;
                }
                default -> null;
            };
        }
        catch (Exception ignored) {}
    }

    private static KeyBinding newKeybind() {
        return new KeyBinding("customhud_option_" + UUID.randomUUID(), GLFW.GLFW_KEY_UNKNOWN, "customhud");
    }



}
