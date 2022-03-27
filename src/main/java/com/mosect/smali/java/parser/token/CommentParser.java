package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.Source;

/**
 * 注释解析器
 */
public class CommentParser extends TokenParser {

    @Override
    protected void onParse() {
        if (source.match("#", false)) {
            // 注释开始
            int length = source.length();
            int start = source.getOffset();
            int end = length;
            for (int i = source.getOffset() + 1; i < length; i++) {
                char ch = source.getText().charAt(i);
                if (ch == '\r' || ch == '\n') {
                    end = i;
                    break;
                }
            }
            putToken(new Token(TokenType.COMMENT, start, end));
        }
    }
}
