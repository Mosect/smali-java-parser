package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.util.CharUtils;

/**
 * 转义字符
 */
public class EscapeChar {

    protected int start;
    protected int end;
    protected String errorId;
    protected char value;

    public boolean parse(CharSequence text, int offset) {
        start = offset;
        end = start;
        errorId = null;
        value = 0;

        if (text.charAt(offset) == '\\') {
            // 开始转义
            parseFirst(text, offset + 1);
            return true;
        }
        return false;
    }

    protected void parseFirst(CharSequence text, int offset) {
        if (offset < text.length()) {
            char ch = text.charAt(offset);
            switch (ch) {
                case 'r':
                    value = '\r';
                    end = offset + 1;
                    break;
                case 'n':
                    value = '\n';
                    end = offset + 1;
                    break;
                case 't':
                    value = '\t';
                    end = offset + 1;
                    break;
                case 'f':
                    value = '\f';
                    end = offset + 1;
                    break;
                case 'b':
                    value = '\b';
                    end = offset + 1;
                    break;
                case '\\':
                    value = '\\';
                    end = offset + 1;
                    break;
                case '\'':
                    value = '\'';
                    end = offset + 1;
                    break;
                case '"':
                    value = '"';
                    end = offset + 1;
                    break;
                case 'u': // 十六进制
                    parseHex(text, offset + 1);
                    break;
                default:
                    if (CharUtils.isOctChar(ch)) {
                        parseOct(text, offset);
                    } else {
                        value = 0;
                        end = offset + 1;
                        errorId = "ESCAPE_CHAR_UNSUPPORTED";
                    }
                    break;
            }
        } else {
            end = offset;
            errorId = "ESCAPE_CHAR_MISSING_CONTENT";
        }
    }

    protected void parseHex(CharSequence text, int offset) {
        int size = 0;
        for (int i = offset; i < text.length() && size < 4; i++) {
            char ch = text.charAt(i);
            if (CharUtils.isHexChar(ch)) {
                ++size;
            } else {
                break;
            }
        }
        end = offset + size;
        if (size == 0) {
            errorId = "ESCAPE_CHAR_MISSING_HEX_CONTENT";
            value = 0;
        } else if (size != 4) {
            errorId = "ESCAPE_CHAR_INVALID_HEX_CONTENT";
            value = 0;
        } else {
            String str = text.subSequence(offset, end).toString();
            value = (char) Integer.parseInt(str, 16);
        }
    }

    /**
     * 解析八进制字符
     *
     * @param text   文本
     * @param offset 偏移量
     */
    protected void parseOct(CharSequence text, int offset) {
        int size = 0;
        for (int i = offset; i < text.length() && size < 3; i++) {
            char ch = text.charAt(i);
            if (CharUtils.isOctChar(ch)) {
                ++size;
            } else {
                break;
            }
        }
        end = offset + size;
        if (size == 0) {
            errorId = "ESCAPE_CHAR_MISSING_OCT_CONTENT";
            value = 0;
        } else {
            String str = text.subSequence(offset, end).toString();
            value = (char) Integer.parseInt(str, 8);
        }
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getErrorId() {
        return errorId;
    }

    public char getValue() {
        return value;
    }
}
