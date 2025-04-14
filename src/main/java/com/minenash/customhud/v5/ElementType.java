package com.minenash.customhud.v5;

import com.minenash.customhud.data.Flags;
import com.minenash.customhud.render.RenderPiece;
import net.minecraft.client.gui.DrawContext;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class ElementType<T> {

    final BiFunction<T,Flags,String> str;
    final Function<T,Number> num;
    final Function<T,Boolean> bool;
    final BiConsumer<DrawContext,RenderPiece> renderer;
    final Map<String,Attribute<T>> attr;

    public ElementType(BiFunction<T,Flags,String> str,
                       Function<T,Number> num,
                       Function<T,Boolean> bool) {
        this(str, num, bool, null, Map.of());
    }

    public ElementType(BiFunction<T,Flags,String> str,
                       Function<T,Number> num,
                       Function<T,Boolean> bool,
                       BiConsumer<DrawContext,RenderPiece> renderer,
                       Map<String,Attribute<T>> attr) {
        this.str = str;
        this.num = num;
        this.bool = bool;
        this.renderer = renderer;
        this.attr = attr;

    }

    public String str(T value, Flags flags) { return str.apply(value, flags); }
    public Number num(T value) { return num.apply(value); }
    public boolean bool(T value) { return bool.apply(value); }
    public Attribute<T> attr(String attr) {
        return this.attr.get(attr);
    }
}
