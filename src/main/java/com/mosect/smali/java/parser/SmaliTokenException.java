package com.mosect.smali.java.parser;

public class SmaliTokenException extends Exception {

    private final int offset;

    public SmaliTokenException(String message, int offset) {
        super(message);
        this.offset = offset;
    }

    public SmaliTokenException(String message, Throwable cause, int offset) {
        super(message, cause);
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
