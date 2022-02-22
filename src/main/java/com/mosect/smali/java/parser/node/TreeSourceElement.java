package com.mosect.smali.java.parser.node;

public class TreeSourceElement extends SmaliTree {

    TreeSourceElement() {
        super(SmaliNodeType.TREE_SOURCE_ELEMENT);
    }

    @Override
    protected TreeSourceElement emptyChildrenTree() {
        return new TreeSourceElement();
    }
}
