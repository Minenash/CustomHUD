package com.minenash.customhud.core.errors;

public class ErrorException extends Exception {

    public final ErrorType type;
    public final String context;

    public ErrorException(ErrorType type, String context) {
        this.type = type;
        this.context = context;
    }

}
