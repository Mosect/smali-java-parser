package com.mosect.smali.java.parser.token;

public class CharToken extends Token {

    private final char charValue;

    public CharToken(int start, int end, char charValue) {
        super(TokenType.CHAR, start, end);
        this.charValue = charValue;
    }

    public char getCharValue() {
        return charValue;
    }
}
