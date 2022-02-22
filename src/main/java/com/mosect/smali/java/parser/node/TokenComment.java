package com.mosect.smali.java.parser.node;

public class TokenComment extends SmaliTokenNode {

    TokenComment(String text) {
        super(SmaliNodeType.TOKEN_COMMENT, text);
    }

    public String getComment() {
        return getText().substring(1);
    }
}
