package com.mosect.smali.java.parser.node;

public class TreeDocument extends SmaliTree {

    TreeDocument() {
        super(SmaliNodeType.TREE_DOCUMENT);
    }

    @Override
    protected SmaliTree emptyChildrenTree() {
        return new TreeDocument();
    }
}
