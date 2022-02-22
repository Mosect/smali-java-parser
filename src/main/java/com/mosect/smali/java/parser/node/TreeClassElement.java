package com.mosect.smali.java.parser.node;

public class TreeClassElement extends SmaliTree {

    TreeClassElement() {
        super(SmaliNodeType.TREE_CLASS_ELEMENT);
    }

    @Override
    protected TreeClassElement emptyChildrenTree() {
        return new TreeClassElement();
    }
}
