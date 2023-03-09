package com.minenash.customhud.errors;

import java.util.ArrayList;
import java.util.List;

public class Errors {
    public record Error(String line, String source, ErrorType type, String context) {}

    private static final List<Error>[] errors = new ArrayList[3];
    static {
        for (int i = 0; i < 3; i++)
            errors[i] = new ArrayList<>();
    }

    public static List<Error> getErrors(int profile) {
        return errors[profile-1];
    }

    public static boolean hasErrors(int profile) {
        return !errors[profile-1].isEmpty();
    }

    public static void clearErrors(int profile) {
        errors[profile-1].clear();
    }

    public static void addError(int profile, int line, String source, ErrorType type, String context) {
        addError(profile, Integer.toString(line+1), source, type, context);
    }
    public static void addError(int profile, String line, String source, ErrorType type, String context) {
        errors[profile-1].add(new Error(line, source, type, context));
    }


}
