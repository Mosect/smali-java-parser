package com.mosect.smali.java.parser.token;

public class NumberToken extends Token {

    private final Number value;

    public NumberToken(int start, int end, Number value) {
        super(TokenType.NUMBER, start, end);
        this.value = value;
    }

    public Number getValue() {
        return value;
    }
}
