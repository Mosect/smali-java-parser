package com.mosect.smali.java.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class SmaliTree extends SmaliNode {

    private final List<SmaliNode> children = new ArrayList<>();

    public int getChildCount() {
        return children.size();
    }

    public void addChild(int index, SmaliNode node) {
        if (null != node.getParent()) {
            throw new IllegalArgumentException("Node's parent must be null");
        }
        children.add(index, node);
        node.setParent(this);
    }

    public void addChild(SmaliNode node) {
        addChild(getChildCount(), node);
    }

    public void removeChild(SmaliNode node) {
        if (children.remove(node)) {
            node.setParent(null);
        }
    }

    public void removeChild(int index) {
        SmaliNode old = children.remove(index);
        old.setParent(null);
    }

    public SmaliNode setChild(int index, SmaliNode node) {
        SmaliNode old = children.set(index, node);
        old.setParent(null);
        return old;
    }

    public SmaliNode getChild(int index) {
        return children.get(index);
    }

    public int getChildIndex(SmaliNode node) {
        return children.indexOf(node);
    }

    public void clearChildren() {
        for (SmaliNode node : children) {
            node.setParent(null);
        }
        children.clear();
    }

    @Override
    public void write(StringBuilder out) {
        for (SmaliNode node : children) {
            node.write(out);
        }
    }
}
