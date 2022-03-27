package com.mosect.smali.java.parser.token;

/**
 * 字符串解析器
 */
public class StringParser extends TokenParser {

    protected StringBuilder stringBuilder = new StringBuilder(512);
    protected EscapeChar escapeChar = new EscapeChar();

    @Override
    protected void onParse() {
        if (source.match("\"", false)) {
            // 字符串开始
            int end = -1;
            boolean endChar = false;
            int offset = source.getOffset() + 1;
            boolean loop = true;
            while (offset < source.length() && loop) {
                char ch = source.charAt(offset);
                switch (ch) {
                    case '\r':
                    case '\n':
                        end = offset;
                        loop = false;
                        break;
                    case '"': // 字符串结束
                        end = offset + 1;
                        loop = false;
                        endChar = true;
                        break;
                    default:
                        // 解析转义字符
                        if (escapeChar.parse(source.getText(), offset)) {
                            // 转义字符
                            if (null == escapeChar.getErrorId()) {
                                // 无错误
                                stringBuilder.append(escapeChar.getValue());
                            } else {
                                // 有错误
                                putError(escapeChar.getErrorId(), "Invalid escape char: " + escapeChar.getErrorId(), escapeChar.getStart());
                            }
                            offset = escapeChar.getEnd();
                        } else {
                            // 普通字符
                            stringBuilder.append(ch);
                            ++offset;
                        }
                        break;
                }
            }
            if (!endChar) {
                // 没有找到结束字符
                putError("STRING_MISSING_END", "Missing string end char", end);
            }
            putToken(new StringToken(source.getOffset(), end, getString()));
        }
    }

    @Override
    protected void onClear() {
        super.onClear();
        if (stringBuilder.length() > 0) {
            stringBuilder.delete(0, stringBuilder.length());
        }
    }

    public String getString() {
        return stringBuilder.toString();
    }
}
