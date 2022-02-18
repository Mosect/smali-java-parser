package com.mosect.smali.java.parser.node;

public class SmaliDocument extends SmaliTree {

    SmaliDocument() {
        super(SmaliNodeType.DOCUMENT);
    }

    @Override
    protected SmaliTree emptyChildrenTree() {
        return new SmaliDocument();
    }
}
