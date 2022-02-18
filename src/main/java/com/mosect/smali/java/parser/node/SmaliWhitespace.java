package com.mosect.smali.java.parser.node;

public class SmaliWhitespace extends SmaliTokenNode {

    SmaliWhitespace(String text) {
        super(SmaliNodeType.WHITESPACE, text);
    }
}
