package com.mosect.smali.java.parser;

public abstract class SmaliNode {

    private SmaliTree parent;

    void setParent(SmaliTree parent) {
        this.parent = parent;
    }

    public SmaliTree getParent() {
        return parent;
    }

    public abstract SmaliNodeType getType();

    public abstract void write(StringBuilder out);
}
