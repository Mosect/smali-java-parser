package com.mosect.smali.java.parser.node;

public class SmaliLinefeed extends SmaliTokenNode {

    SmaliLinefeed(String text) {
        super(SmaliNodeType.LINEFEED, text);
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
