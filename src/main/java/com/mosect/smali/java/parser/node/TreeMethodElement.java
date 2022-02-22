package com.mosect.smali.java.parser.node;

public class TreeMethodElement extends SmaliTree {

    TreeMethodElement() {
        super(SmaliNodeType.TREE_METHOD_ELEMENT);
    }

    @Override
    protected TreeMethodElement emptyChildrenTree() {
        return new TreeMethodElement();
    }
}
