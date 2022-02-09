package com.mosect.smali.java.parser;

public class SmaliToken {

    private final SmaliTokenType type;
    private String text;

    public SmaliToken(SmaliTokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    protected void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public SmaliTokenType getType() {
        return type;
    }

    public void check() throws SmaliTokenException {
    }

    @Override
    public String toString() {
        return "SmaliToken{" +
                "type=" + type +
                ", text='" + text + '\'' +
                '}';
    }
}
