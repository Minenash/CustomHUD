package com.minenash.customhud.v5;

import com.minenash.customhud.CustomHud;
import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.minenash.customhud.v5.AttrList.Entry.of;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ElementTypes {

    public static Map<Class, ElementType> elementTypes = new HashMap<>();

    public static void register(Class c, ElementType e) {
        elementTypes.put(c, e);
    }

    public static <T> void register(Class<T> c, BiFunction<T,Flags,String> str,
                                                Function<T,Number> num,
                                                Function<T,Boolean> bool,
                                                BiConsumer<DrawContext, RenderPiece> renderer,
                                                Map<String,Attribute<T>> attr) {
        elementTypes.put(c, new ElementType(str, num, bool, renderer, attr));
    }

    public static String getString(Object value, Flags flags) {
        if (value == null) return "-";
        ElementType he = elementTypes.get(value.getClass());
        if (he == null) return "[!Register HElement!]";
        try {
            return he.str(value, flags);
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
        ElementType he = elementTypes.get(value.getClass());
        if (he == null) return Double.NaN;

        try { return he.num(value); }
        catch (Exception e) { return Double.NaN; }
    }

    public static boolean getBoolean(Object value) {
        if (value == null) return false;
        ElementType he = elementTypes.get(value.getClass());
        if (he == null) return false;

        try { return he.bool(value); }
        catch (Exception e) { return false; }
    }

    public static Attribute getAttribute(Object value, String name) {
        if (value == null || name == null || name.isBlank()) return null;
        ElementType he = elementTypes.get(value.getClass());
        if (he == null) return null;
        try { return he.attr(name); }
        catch (Exception e) { return null; }
    }


    private static final Function DIRECT = x -> x;
    private static <O> String directToString(O value, Flags flags) {
        return value.toString();
    }


    public static final ElementType STR = new ElementType<String>(
            ElementTypes::directToString,
            String::length,
            String::isBlank,
            null,
            AttrList.build(
                    of("length", Integer.class, String::length),
                    of("isEmpty", Boolean.class, String::isEmpty),
                    of("isBlank", Boolean.class, String::isBlank)
            )
    );

    static {
        register(String.class,
                ElementTypes::directToString,
                String::length,
                String::isBlank,
                null,
                AttrList.build(
                        of("length", Integer.class, String::length),
                        of("isEmpty", Boolean.class, String::isEmpty),
                        of("isBlank", Boolean.class, String::isBlank)
                )
        );
        register(Integer.class,
            ElementTypes::directToString,
            DIRECT,
            num -> num > 0,
            null,
            AttrList.build(
                of("length", Integer.class, num -> num.toString().length())
            )
        );

    }

}
