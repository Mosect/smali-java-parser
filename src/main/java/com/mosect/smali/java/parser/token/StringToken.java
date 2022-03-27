package com.mosect.smali.java.parser.token;

public class StringToken extends Token {

    private final String string;

    public StringToken(int start, int end, String string) {
        super(TokenType.STRING, start, end);
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
