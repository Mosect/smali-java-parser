package com.mosect.smali.java.parser.util;

/**
 * 字符工具
 */
public final class CharUtils {

    private CharUtils() {
    }

    public static boolean isDecChar(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isHexChar(char ch) {
        return (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F') || isDecChar(ch);
    }

    public static boolean isOctChar(char ch) {
        return ch >= '0' && ch <= '7';
    }

    public static boolean isBinChar(char ch) {
        return ch == '0' || ch == '1';
    }

    public static boolean equalsChar(char ch1, char ch2, boolean ignoreCase) {
        if (ignoreCase) {
            return Character.toLowerCase(ch1) == Character.toLowerCase(ch2);
        }
        return ch1 == ch2;
    }

    public static int getCharMinRadix(char ch) {
        boolean hex = (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
        if (hex) return 16;
        if (ch <= '9') {
            if (ch > '7') return 10;
            if (ch > '1') return 8;
            return 2;
        }
        return 0;
    }

    /**
     * 判断是否匹配文本
     *
     * @param src        原文本
     * @param srcOffset  原文本偏移量
     * @param dst        目标文本
     * @param dstOffset  目标文本偏移量
     * @param ignoreCase 是否开启大小写无关选项
     * @return true，匹配；false，不匹配
     */
    public static boolean match(CharSequence src, int srcOffset, CharSequence dst, int dstOffset, boolean ignoreCase) {
        int matchCharCount = dst.length() - dstOffset;
        if (src.length() - srcOffset >= matchCharCount) {
            if (matchCharCount == 1) {
                return equalsChar(src.charAt(srcOffset), dst.charAt(dstOffset), ignoreCase);
            }
            for (int i = 0; i < matchCharCount; i++) {
                if (!equalsChar(src.charAt(srcOffset + i), dst.charAt(dstOffset + i), ignoreCase)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 判断是否匹配文本
     *
     * @param src       原文本
     * @param srcOffset 原文本偏移量
     * @param dst       目标文本
     * @return true，匹配；false，不匹配
     */
    public static boolean match(CharSequence src, int srcOffset, CharSequence dst, boolean ignoreCase) {
        return match(src, srcOffset, dst, 0, ignoreCase);
    }
}
