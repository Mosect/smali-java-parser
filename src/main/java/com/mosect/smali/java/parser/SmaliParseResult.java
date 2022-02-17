package com.mosect.smali.java.parser;

import java.util.ArrayList;
import java.util.List;

public class SmaliParseResult<T> {

    private final List<SmaliParseError> errors = new ArrayList<>();
    private T result;
    private CharSequence text;

    public boolean haveError() {
        return errors.size() > 0;
    }

    public List<SmaliParseError> getErrors() {
        return errors;
    }

    public T getResult() {
        return result;
    }

    public CharSequence getText() {
        return text;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public void initError(SmaliParseError error, int offset) {
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
}
