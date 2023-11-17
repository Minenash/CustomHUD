package com.minenash.customhud.HudElements;

public class FrequencyElement implements HudElement {

    private final HudElement element;
    private final int interval;

    private long lastMS = System.currentTimeMillis();
    String strValue;
    Number numValue;
    boolean boolValue;

    public FrequencyElement(HudElement element, int interval) {
        this.element = element;
        this.interval = interval;
    }

    private void update() {
        long currentMs = System.currentTimeMillis();
        long diff = currentMs - lastMS;
        if (diff >= interval || strValue == null) {
            lastMS = currentMs;
            strValue = element.getString();
            numValue = element.getNumber();
            boolValue = element.getBoolean();
        }
    }


    @Override
    public String getString() {
        update();
        return strValue;
    }

    @Override
    public Number getNumber() {
        update();
        return numValue;
    }

    @Override
    public boolean getBoolean() {
        update();
        return boolValue;
    }
}
