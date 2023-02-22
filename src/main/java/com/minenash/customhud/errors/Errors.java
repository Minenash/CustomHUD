package com.minenash.customhud.errors;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    public record Error(int line, String source, ErrorType type, String context) {}

    private static final List<Error>[] errors = new ArrayList[3];
    static {
        for (int i = 0; i < 3; i++)
            errors[i] = new ArrayList<>();
    }

    public static List<Error> getErrors(int profile) {
        return errors[profile];
    }

    public static void clearErrors(int profile) {
        errors[profile].clear();
    }

    public static void addError(int profile, int line, String source, ErrorType type, String context) {
        errors[profile].add(new Error(line, source, type, context));
    }


}
