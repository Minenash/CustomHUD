package com.minenash.customhud.NewHudElements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTimeElement2 implements HudElement2 {

    private final SimpleDateFormat formatter;

    public RealTimeElement2(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getString() {
        return formatter.format(new Date());
    }

    @Override
    public Number getNumber() {
        return new Date().getTime();
    }

    @Override
    public boolean getBoolean() {
        return true;
    }
}
