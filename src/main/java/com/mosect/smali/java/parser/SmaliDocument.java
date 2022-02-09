package com.mosect.smali.java.parser;

public class SmaliDocument extends SmaliTree {

    @Override
    public SmaliNodeType getType() {
        return SmaliNodeType.DOCUMENT;
    }
}
