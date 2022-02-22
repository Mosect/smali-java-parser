package com.mosect.smali.java.parser;

import java.util.List;

public class SmaliTokenTreeNode {

    private final SmaliToken token;
    private SmaliTokenTreeNode parent;
    private List<SmaliTokenTreeNode> children;
    private boolean containsEnd;

    public SmaliTokenTreeNode(SmaliToken token) {
        this.token = token;
    }

    public SmaliToken getToken() {
        return token;
    }

    public void setChildren(List<SmaliTokenTreeNode> children) {
        this.children = children;
    }

    public List<SmaliTokenTreeNode> getChildren() {
        return children;
    }

    public void setParent(SmaliTokenTreeNode parent) {
        this.parent = parent;
    }

    public SmaliTokenTreeNode getParent() {
        return parent;
    }

    public boolean isContainsEnd() {
        return containsEnd;
    }

    public void setContainsEnd(boolean containsEnd) {
        this.containsEnd = containsEnd;
    }

    @Override
    public String toString() {
        return "SmaliTokenTree{" +
                "token=" + token +
                ", parent=" + (null != parent) +
                ", children=" + (null == children ? 0 : children.size()) +
                '}';
    }
}
