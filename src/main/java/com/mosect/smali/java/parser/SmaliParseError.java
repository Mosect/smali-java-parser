package com.mosect.smali.java.parser;

public class SmaliParseError {

    public final static int CODE_INVALID_STRING = 1;

    private final int code;
    private final String message;
    private int line = -1;
    private int lineOffset = -1;
    private int offset = -1;

    public SmaliParseError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SmaliParseError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", line=" + line +
                ", lineOffset=" + lineOffset +
                ", offset=" + offset +
                '}';
    }
}
