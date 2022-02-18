package com.mosect.smali.java.parser.node;

public class SmaliComment extends SmaliTokenNode {

    SmaliComment(String text) {
        super(SmaliNodeType.COMMENT, text);
    }

    public String getComment() {
        return getText().substring(1);
    }
}
