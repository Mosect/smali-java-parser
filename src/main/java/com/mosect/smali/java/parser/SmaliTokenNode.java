package com.mosect.smali.java.parser;

public class SmaliTokenNode extends SmaliNode {

    private final SmaliToken token;

    public SmaliTokenNode(SmaliToken token) {
        this.token = token;
    }

    @Override
    public SmaliNodeType getType() {
        return SmaliNodeType.TOKEN;
    }

    public SmaliToken getToken() {
        return token;
    }

    @Override
    public void write(StringBuilder out) {
        out.append(token.getText());
    }
}
