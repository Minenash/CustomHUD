package com.minenash.customhud.v5;

import com.minenash.customhud.CustomHud;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.minenash.customhud.v5.AttrList.Entry.of;

@SuppressWarnings({"rawtypes", "unchecked"})
public class HElements {

    public static Map<Class,HElement> hElements = new HashMap<>();

    public static void register(Class c, HElement e) {
        hElements.put(c, e);
    }
    public static <T> void register(Class<T> c, Function<T,String> str,
                                                Function<T,Number> num,
                                                Function<T,Boolean> bool,
                                                Map<String,Attribute<T>> attr) {
        hElements.put(c, new HElement(str, num, bool, attr));
    }

    public static String getString(Object value) {
        if (value == null) return "-";
        HElement he = hElements.get(value.getClass());
        if (he == null) return "[!Register HElement!]";
        try {
            return he.str(value);
        }
        catch (Exception e) {
            if (CustomHud.DEBUG_MODE)
                return "[!" + e.getMessage() + "!]";
            else
                return "-";
        }
    }

    public static Number getNumber(Object value) {
        if (value == null) return Double.NaN;
        HElement he = hElements.get(value.getClass());
        if (he == null) return Double.NaN;

        try { return he.num(value); }
        catch (Exception e) { return Double.NaN; }
    }

    public static boolean getBoolean(Object value) {
        if (value == null) return false;
        HElement he = hElements.get(value.getClass());
        if (he == null) return false;

        try { return he.bool(value); }
        catch (Exception e) { return false; }
    }

    public static Attribute getAttribute(Object value, String name) {
        if (value == null || name == null || name.isBlank()) return null;
        HElement he = hElements.get(value.getClass());
        if (he == null) return null;
        try { return he.attr(name); }
        catch (Exception e) { return null; }
    }


    private static final Function DIRECT = x -> x;

    static {
        register(String.class,
            DIRECT,
            String::length,
            String::isBlank,
            AttrList.build(
                of("length", Integer.class, String::length),
                of("isEmpty", Boolean.class, String::isEmpty),
                of("isBlank", Boolean.class, String::isBlank)
            )
        );
        register(Integer.class,
            Object::toString,
            DIRECT,
            num -> num > 0,
            AttrList.build(
                of("length", Integer.class, num -> num.toString().length())
            )
        );
    }

}
