package com.mosect.smali.java.parser.node;

public class TreeAnnotationElement extends SmaliTree {

    TreeAnnotationElement() {
        super(SmaliNodeType.TREE_ANNOTATION_ELEMENT);
    }

    @Override
    protected TreeAnnotationElement emptyChildrenTree() {
        return new TreeAnnotationElement();
    }
}
