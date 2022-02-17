package com.mosect.smali.java.parser;

public class SmaliException extends Exception {

    private final int offset;

    public SmaliException(String message, int offset) {
        super(message);
        this.offset = offset;
    }

    public SmaliException(String message, Throwable cause, int offset) {
        super(message, cause);
        this.offset = offset;
    }

    public SmaliException(Throwable cause, int offset) {
        super(cause);
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
