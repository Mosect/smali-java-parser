package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.util.CharUtils;

import java.math.BigInteger;

public class NumberParser extends TokenParser {

    protected Number value;

    @Override
    protected void onClear() {
        super.onClear();
        value = null;
    }

    @Override
    protected void onParse() {
        if (source.match("0x", true)) {
            // 十六进制数
            parseHex(source.getOffset() + 2);
        } else if (source.match("0", false)) {
            // 八进制或者0
            parseOct(source.getOffset() + 1);
        } else if (source.hasMore()) {
            char ch = source.charAt(source.getOffset());
            if (CharUtils.isDecChar(ch)) {
                // 十进制
                parseDec(source.getOffset());
            }
        }
    }

    protected void parseHex(int offset) {
        int size = 0;
        char endChar = 0;
        for (int i = offset; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (CharUtils.isHexChar(ch)) {
                ++size;
            } else if (isEndChar(ch)) {
                endChar = ch;
                break;
            } else {
                break;
            }
        }
        int end = offset + size;
        if (size == 0) {
            putError("NUMBER_HEX_MISSING_CONTENT", "Missing hex number content", offset);
        } else {
            String str = source.subSequence(source.getOffset(), size).toString();
            value = new BigInteger(str, 16);
        }
        if (endChar != 0) {
            ++end;
        }
        putToken(new NumberToken(source.getOffset(), end, value));
    }

    protected void parseOct(int offset) {
        int size = 0;
        char endChar = 0;
        for (int i = offset; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (CharUtils.isOctChar(ch)) {
                ++size;
            } else if (isEndChar(ch)) {
                endChar = ch;
                break;
            } else {
                break;
            }
        }
        int end = offset + size;
        if (size == 0) {
            value = 0;
        } else {
            String str = source.subSequence(source.getOffset(), size).toString();
            value = new BigInteger(str, 8);
        }
        if (endChar != 0) {
            ++end;
        }
        putToken(new NumberToken(source.getOffset(), end, value));
    }

    protected void parseDec(int offset) {
        char endChar = 0;

        // 解析整数
        int curOffset = offset;
        boolean point = false;
        while (curOffset < source.length()) {
            char ch = source.charAt(curOffset++);
            if (CharUtils.isDecChar(ch)) {
            } else if (ch == '.') {
                // 小数点
                point = true;
                break;
            } else if (isEndChar(ch)) {
                endChar = ch;
                break;
            }
        }

        // 解析小数
        if (point && endChar == 0) {
            while (curOffset < source.length()) {

            }
        }
    }

    protected boolean isEndChar(char ch) {
        switch (ch) {
            case 'd':
            case 'D':
            case 'f':
            case 'F':
            case 'l':
            case 'L':
                return true;
            default:
                return false;
        }
    }

    public Number getValue() {
        return value;
    }
}
