package com.minenash.customhud.v5;

import java.util.function.Function;

public record Attribute<T>(Class<?> clazz, NumberDefaultOptions numberDefaultOptions, Function<T,?> func) {



}
