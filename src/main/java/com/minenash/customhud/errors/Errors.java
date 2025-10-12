package com.minenash.customhud.errors;

import java.util.*;

public class Errors {
    public record Error(String line, String source, ErrorType type, String context) {}

    private static final Map<String,List<Error>> errors = new HashMap<>();

    public static List<Error> getErrors(String profileName) {
        return errors.computeIfAbsent(profileName, str -> new ArrayList<>());
    }

    public static boolean hasErrors(String profileName) {
        return !getErrors(profileName).isEmpty();
    }

    public static void clearErrors(String profileName) {
        getErrors(profileName).clear();
    }

    public static Error addError(String profileName, int line, String source, ErrorType type, String context) {
        return addError(profileName, Integer.toString(line+1), source, type, context);
    }
    public static Error addError(String profileName, String line, String source, ErrorType type, String context) {
        Error e = new Error(line, source, type, context == null ? "" : context);
        getErrors(profileName).add(e);
        return e;
    }
    public static Error addError(ErrorContext context, ErrorType type, String value) {
        return addError(context.profileName(), Integer.toString(context.line()), context.src(), type, value);
    }


}
