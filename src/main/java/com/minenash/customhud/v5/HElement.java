package com.minenash.customhud.v5;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("rawtypes")
public class HElement<T> {

    final Function<T,String> str;
    final Function<T,Number> num;
    final Function<T,Boolean> bool;
    final Map<String,Attribute<T>> attr;

    public HElement(Function<T,String> str,
                    Function<T,Number> num,
                    Function<T,Boolean> bool) {
        this(str, num, bool, Map.of());
    }

    public HElement(Function<T,String> str,
                    Function<T,Number> num,
                    Function<T,Boolean> bool,
                    Map<String,Attribute<T>> attr) {
        this.str = str;
        this.num = num;
        this.bool = bool;
        this.attr = attr;
    }

    public String str(T value) { return str.apply(value); }
    public Number num(T value) { return num.apply(value); }
    public boolean bool(T value) { return bool.apply(value); }
    public Attribute<T> attr(String attr) {
        return this.attr.get(attr);
    }
}
