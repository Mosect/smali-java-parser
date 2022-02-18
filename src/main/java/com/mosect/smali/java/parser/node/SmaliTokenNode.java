package com.mosect.smali.java.parser.node;

import java.io.IOException;
import java.io.Writer;

public class SmaliTokenNode extends SmaliNode {

    private final String text;

    SmaliTokenNode(SmaliNodeType type, String text) {
        super(type);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public SmaliTokenNode copy() {
        return new SmaliTokenNode(getType(), getText());
    }

    @Override
    public void write(Writer writer) throws IOException {
        writer.write(getText());
    }

    @Override
    public void append(StringBuilder builder) {
        builder.append(getText());
    }
}
