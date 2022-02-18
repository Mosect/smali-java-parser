package com.mosect.smali.java.parser;

public class SmaliParseError {

    public final static int CODE_INVALID_STRING = 1;
    public final static int CODE_INVALID_CHAR = 2;
    public final static int CODE_INVALID_NUMBER = 3;
    public final static int CODE_INVALID_ELEMENT = 4;

    public final static int CODE_UNEXPECTED_TOKEN = 101;
    public final static int CODE_UNSUPPORTED_ELEMENT = 102;

    public static void initError(CharSequence text, SmaliParseError error, int offset) {
        if (null != text) {
            int lineNum = 0;
            boolean _r = false;
            int lineOffset = 0;
            int end = Math.min(offset, text.length() - 1);
            for (int i = 0; i <= end; i++) {
                char ch = text.charAt(i);
                if (ch == '\r') {
                    ++lineNum;
                    lineOffset = 0;
                    _r = true;
                } else if (ch == '\n') {
                    if (_r) {
                        _r = false;
                    } else {
                        ++lineNum;
                        lineOffset = 0;
                    }
                } else {
                    if (_r) {
                        _r = false;
                    }
                    ++lineOffset;
                }
            }
            error.setLine(lineNum);
            error.setLineOffset(lineOffset);
        } else {
            error.setLine(-1);
            error.setLineOffset(-1);
        }
    }

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
