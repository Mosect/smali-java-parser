package com.mosect.smali.java.parser.node;

public class TokenWhitespace extends SmaliTokenNode {

    TokenWhitespace(String text) {
        super(SmaliNodeType.TOKEN_WHITESPACE, text);
    }
}
