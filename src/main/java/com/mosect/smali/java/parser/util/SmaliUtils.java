package com.mosect.smali.java.parser.util;

public final class SmaliUtils {
    private SmaliUtils() {
    }

    public static boolean match(CharSequence text, int offset, String tag) {
        if (text.length() - offset >= tag.length()) {
            if (tag.length() > 1) {
                for (int i = 0; i < tag.length(); i++) {
                    char ch1 = tag.charAt(i);
                    char ch2 = text.charAt(offset + i);
                    if (ch1 != ch2) return false;
                }
                return true;
            } else {
                return text.charAt(offset) == tag.charAt(0);
            }
        }
        return false;
    }

    public static boolean match(CharSequence text, int start, int end, String tag) {
        if (end - start == tag.length()) {
            for (int i = 0; i < tag.length(); i++) {
                char ch1 = tag.charAt(i);
                char ch2 = text.charAt(start + i);
                if (ch1 != ch2) return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isWhitespace(char ch) {
        switch (ch) {
            case ' ':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    public static boolean isOct(char ch) {
        return ch >= '0' && ch <= '7';
    }

    public static boolean isDec(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isHex(char ch) {
        return isDec(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }

    public static boolean isUnicodeSymbol(char ch) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION ||
                ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
                ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                ub == Character.UnicodeBlock.CJK_COMPATIBILITY ||
                ub == Character.UnicodeBlock.VERTICAL_FORMS;
    }

    public static boolean isValidName(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || isDec(ch) || ch == '_' || ch == '$' || !isUnicodeSymbol(ch);
    }

    public static String charStr(char ch) {
        return String.format("[%s,%02x]", ch, (int) ch);
    }
}
