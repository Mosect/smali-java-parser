package com.mosect.smali.java.parser;

import com.mosect.smali.java.parser.util.CharUtils;

/**
 * 源码
 */
public class Source implements CharSequence {

    private final CharSequence text;
    private int offset;

    public Source(CharSequence text) {
        this.text = text;
        this.offset = 0;
    }

    public CharSequence getText() {
        return text;
    }

    @Override
    public int length() {
        return text.length();
    }

    @Override
    public char charAt(int index) {
        return text.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return text.subSequence(start, end);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean hasMore() {
        return offset < text.length();
    }

    public void addOffset(int value) {
        offset += value;
    }

    public void addOffsetOne() {
        ++offset;
    }

    public boolean match(String dst, boolean ignoreCase) {
        return CharUtils.match(text, offset, dst, ignoreCase);
    }
}
