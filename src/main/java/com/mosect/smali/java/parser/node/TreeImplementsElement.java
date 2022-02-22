package com.mosect.smali.java.parser.node;

public class TreeImplementsElement extends SmaliTree {

    TreeImplementsElement() {
        super(SmaliNodeType.TREE_IMPLEMENTS_ELEMENT);
    }

    @Override
    protected TreeImplementsElement emptyChildrenTree() {
        return new TreeImplementsElement();
    }
}
