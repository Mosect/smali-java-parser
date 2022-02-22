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

    void setOffset(int offset) {
        this.offset = offset;
    }

    void next() {
        ++offset;
    }

    SmaliToken current() {
        return tokens.get(offset);
    }

    boolean hasCurrent() {
        return offset < tokens.size();
    }

    int getCharOffset() {
        int offset = 0;
        for (int i = 0; i < this.offset; i++) {
            SmaliToken token = tokens.get(i);
            offset += token.length();
        }
        return offset;
    }
}
