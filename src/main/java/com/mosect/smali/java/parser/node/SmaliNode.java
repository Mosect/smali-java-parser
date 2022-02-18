package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.SmaliParseError;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public abstract class SmaliNode {

    private final SmaliNodeType type;
    private SmaliNode parent;

    SmaliNode(SmaliNodeType type) {
        this.type = type;
    }

    public void check(List<SmaliParseError> outErrors) {
    }

    public SmaliNodeType getType() {
        return type;
    }

    public SmaliNode getParent() {
        return parent;
    }

    protected void setParent(SmaliNode parent) {
        this.parent = parent;
    }

    public abstract SmaliNode copy();

    public abstract void write(Writer writer) throws IOException;

    public abstract void append(StringBuilder builder);
}
