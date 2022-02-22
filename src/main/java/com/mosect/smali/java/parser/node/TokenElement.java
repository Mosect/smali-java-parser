package com.mosect.smali.java.parser.node;

public class TokenElement extends SmaliTokenNode {

    TokenElement(String text) {
        super(SmaliNodeType.TOKEN_ELEMENT, text);
    }
}
