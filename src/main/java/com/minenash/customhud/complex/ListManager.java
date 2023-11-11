package com.minenash.customhud.complex;

import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;

public class ListManager {

    private static final Stack<Integer> index = new Stack<>();
    private static final Stack<List<?>> values = new Stack<>();

    public static void push(List<?> values) {
        ListManager.index.push(0);
        ListManager.values.push(values);
    }

    public static void pop() {
        ListManager.index.pop();
        ListManager.values.pop();
    }

    public static void advance() {
        ListManager.index.push(ListManager.index.pop()+1);
    }

    public static int getCount() {
        return values.peek().size();
    }

    public static int getIndex() {
        return index.peek();
    }

    public static Object getValue() {
        return values.peek().get(index.peek());
    }
    public static Object getValue(int index) {
        return values.peek().get(index);
    }

    public static final Supplier<?> SUPPLIER = ListManager::getValue;

}
