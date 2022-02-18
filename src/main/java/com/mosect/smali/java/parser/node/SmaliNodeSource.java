package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.SmaliToken;

import java.util.List;

public class SmaliNodeSource {

    private CharSequence rawText;
    private final List<SmaliToken> tokens;
    private int offset;

    public SmaliNodeSource(CharSequence rawText, List<SmaliToken> tokens) {
        this.rawText = rawText;
        this.tokens = tokens;
    }

    public SmaliNodeSource(List<SmaliToken> tokens) {
        this.tokens = tokens;
    }

    public CharSequence getRawText() {
        if (null == this.rawText) {
            StringBuilder builder = new StringBuilder(64);
            for (SmaliToken token : tokens) {
                builder.append(token.getText());
            }
            this.rawText = builder.toString();
        }
        return this.rawText;
    }

    public List<SmaliToken> getTokens() {
        return tokens;
    }

    void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
