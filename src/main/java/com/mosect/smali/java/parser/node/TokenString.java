package com.mosect.smali.java.parser.node;

public class TokenString extends SmaliTokenNode {

    private String content;

    TokenString(String text) {
        super(SmaliNodeType.TOKEN_STRING, text);
    }

    public String getContent() {
        return content;
    }
}
