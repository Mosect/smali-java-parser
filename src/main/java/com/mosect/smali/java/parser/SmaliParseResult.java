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
}
