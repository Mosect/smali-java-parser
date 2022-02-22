package com.mosect.smali.java.parser.node;

public class TreeSuperElement extends SmaliTree {

    TreeSuperElement() {
        super(SmaliNodeType.TREE_SUPER_ELEMENT);
    }

    @Override
    protected TreeSuperElement emptyChildrenTree() {
        return new TreeSuperElement();
    }
}
