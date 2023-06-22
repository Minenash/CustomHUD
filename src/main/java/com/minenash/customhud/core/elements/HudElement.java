package com.minenash.customhud.core.elements;

import java.util.function.Supplier;

public interface HudElement {

    default String getString() { return null; }
    default Number getNumber() { return null; }
    default boolean getBoolean() { return false; }

    default <T> T sanitize(Supplier<T> supplier, T onFail) {
        try {
            T value = supplier.get();
            return value == null? onFail : value;
        }
        catch(Exception _e) {
            return onFail;
        }
    }



}
