package com.minenash.customhud.errors;

import com.minenash.customhud.errors.ErrorType;

public class ErrorException extends Exception{

    public final ErrorType type;
    public final String context;

    public ErrorException(ErrorType type, String context) {
        this.type = type;
        this.context = context;
    }

}
