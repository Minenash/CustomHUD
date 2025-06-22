package com.minenash.customhud.data;

import java.util.EnumSet;

public enum DisableElement {
    STATUS_BARS, HOTBAR, ITEM_TOOLTIP, XP, HEALTH, HUNGER, ARMOR, AIR, SCOREBOARD, BOSSBARS, SUBTITLES, STATUS_EFFECTS, CHAT,
    HORSE_JUMP, HORSE_HEALTH, HORSE_ARMOR, HORSE, TITLES, ACTIONBAR, LOCATOR;

    private static final EnumSet<DisableElement> STATUS = EnumSet.of(HEALTH, HUNGER, ARMOR, AIR, HORSE_HEALTH);
    private static final EnumSet<DisableElement> LOWER = EnumSet.of(HEALTH, HUNGER, ARMOR, AIR, XP, HOTBAR, ITEM_TOOLTIP, HORSE_HEALTH, HORSE_JUMP, LOCATOR);
    private static final EnumSet<DisableElement> HORSE_SET = EnumSet.of(HORSE_HEALTH, HORSE_JUMP);

    public static boolean add(EnumSet<DisableElement> elements, String name) {
        switch (name) {
            case "status", "status_bars" -> elements.addAll(STATUS);
            case "lower" -> elements.addAll(LOWER);
            case "horse" -> elements.addAll(HORSE_SET);
            case "hotbar" -> { elements.add(HOTBAR); elements.add(ITEM_TOOLTIP); }
            default -> {
                DisableElement element = switch (name) {
                    case "scoreboard" -> SCOREBOARD;
                    case "bossbar", "bossbars" -> BOSSBARS;
                    case "subtitle", "subtitles" -> SUBTITLES;
                    case "status_effects", "effects" -> STATUS_EFFECTS; //Done
                    case "chat" -> CHAT;

                    case "item_tooltip" -> ITEM_TOOLTIP;
                    case "xp" -> XP;

                    case "health" -> HEALTH;
                    case "air" -> AIR;
                    case "hunger", "food" -> HUNGER;
                    case "armor", "armour" -> ARMOR;

                    case "horse_jump" -> HORSE_JUMP;
                    case "horse_health" -> HORSE_HEALTH;

                    case "titles" -> TITLES;
                    case "actionbar" -> ACTIONBAR;

                    case "locator" -> LOCATOR;

                    default -> null;
                };
                if (elements != null)
                    elements.add(element);
                else
                    return false;
            }
        }
        return true;
    }
}
