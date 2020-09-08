package com.minenash.customhud.HudElements;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RealTimeElement implements HudElement{

    private final SimpleDateFormat formatter;

    public RealTimeElement(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    @Override
    public String getString() {
        return formatter.format(new Date());
    }
}
