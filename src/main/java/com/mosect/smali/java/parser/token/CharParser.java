package com.mosect.smali.java.parser.token;

public class CharParser extends TokenParser {

    protected char charValue;
    protected EscapeChar escapeChar = new EscapeChar();

    @Override
    protected void onClear() {
        super.onClear();
        charValue = 0;
    }

    @Override
    protected void onParse() {
        if (source.match("'", false)) {
            // 字符开始
            int offset = source.getOffset() + 1;
            if (offset < source.length()) {
                char ch = source.charAt(offset);
                ++offset;
                if (ch == '\'') {
                    putError("CHAR_MISSING_CONTENT", "Missing char content", offset);
                } else {
                    if (escapeChar.parse(source.getText(), offset)) {
                        // 转义字符
                        charValue = escapeChar.getValue();
                        if (null != escapeChar.getErrorId()) {
                            putError(escapeChar.getErrorId(), "Invalid escape char: " + escapeChar.getErrorId(), offset);
                        }
                    } else {
                        // 非转义字符
                        charValue = ch;
                    }

                    if (offset < source.length()) {
                        ch = source.charAt(offset);
                        ++offset;
                        if (ch == '\'') {
                            putToken(new CharToken(source.getOffset(), offset, charValue));
                        } else {
                            putError("CHAR_INVALID_END", "Invalid char end", offset - 1);
                        }
                    } else {
                        putError("CHAR_MISSING_END", "Missing char end", offset);
                    }
                }
            } else {
                putError("CHAR_MISSING_CONTENT_AND_END", "Missing char content and end", offset);
            }

            if (!hasToken()) {
                putToken(new CharToken(source.getOffset(), offset, (char) 0));
            }
        }
    }

    public char getCharValue() {
        return charValue;
    }
}
