package com.mosect.smali.java.parser.node;

public class TokenLinefeed extends SmaliTokenNode {

    TokenLinefeed(String text) {
        super(SmaliNodeType.TOKEN_LINEFEED, text);
    }

    public boolean isCr() {
        return "\r".equals(getText());
    }

    public boolean isLf() {
        return "\n".equals(getText());
    }

    public boolean isCrlf() {
        return "\r\n".equals(getText());
    }
}
