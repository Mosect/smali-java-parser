package com.mosect.smali.java.parser.node;

public class TreeFieldElement extends SmaliTree {

    TreeFieldElement() {
        super(SmaliNodeType.TREE_FIELD_ELEMENT);
    }

    @Override
    protected TreeFieldElement emptyChildrenTree() {
        return new TreeFieldElement();
    }
}
