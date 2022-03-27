package com.mosect.smali.java.parser.token;

import java.util.Objects;

public class Token {

    private final TokenType type;
    private final int start;
    private final int end;

    public Token(TokenType type, int start, int end) {
        this.type = type;
        this.start = start;
        this.end = end;
    }

    public TokenType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return start == token.start && end == token.end && type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, start, end);
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
