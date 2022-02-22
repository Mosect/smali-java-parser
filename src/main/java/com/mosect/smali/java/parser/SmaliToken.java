package com.mosect.smali.java.parser;

public class SmaliToken {

    private final SmaliTokenType type;
    private final String text;

    SmaliToken(SmaliTokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public SmaliTokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public int length() {
        return text.length();
    }

    @Override
    public String toString() {
        return "SmaliToken{" +
                "type=" + type +
                ", text='" + text + '\'' +
                '}';
    }
}
