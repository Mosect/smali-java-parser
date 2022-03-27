package com.mosect.smali.java.parser;

/**
 * 解析错误
 */
public class ParseError {

    private final String id;
    private final String message;
    private final int position;
    private int lineIndex;
    private int lineOffset;
    private int linePosition;

    public ParseError(String id, String message, int position) {
        this.id = id;
        this.message = message;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
    }

    public int getLinePosition() {
        return linePosition;
    }

    public void setLinePosition(int linePosition) {
        this.linePosition = linePosition;
    }

    @Override
    public String toString() {
        return "ParseError{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", position=" + position +
                ", lineIndex=" + lineIndex +
                ", lineOffset=" + lineOffset +
                ", linePosition=" + linePosition +
                '}';
    }
}
