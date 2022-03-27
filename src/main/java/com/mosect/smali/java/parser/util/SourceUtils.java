package com.mosect.smali.java.parser.util;

import com.mosect.smali.java.parser.ParseError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 源码工具
 */
public final class SourceUtils {

    private SourceUtils() {
    }

    /**
     * 读取源码
     *
     * @param ins     输入流
     * @param charset 字符集
     * @return 源码
     * @throws IOException 读取异常
     */
    public CharSequence readSource(InputStream ins, Charset charset) throws IOException {
        return readSource(new InputStreamReader(ins, charset));
    }

    /**
     * 读取源码
     *
     * @param ins     输入流
     * @param charset 字符集
     * @return 源码
     * @throws IOException 读取异常
     */
    public CharSequence readSource(InputStream ins, String charset) throws IOException {
        return readSource(new InputStreamReader(ins, charset));
    }

    /**
     * 读取源码
     *
     * @param file    文件
     * @param charset 字符集
     * @return 源码
     * @throws IOException 读取异常
     */
    public CharSequence readSource(File file, String charset) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return readSource(new InputStreamReader(fis, charset));
        }
    }

    /**
     * 读取源码
     *
     * @param file    文件
     * @param charset 字符集
     * @return 源码
     * @throws IOException 读取异常
     */
    public CharSequence readSource(File file, Charset charset) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return readSource(new InputStreamReader(fis, charset));
        }
    }

    /**
     * 读取源码
     *
     * @return 源码
     * @throws IOException 读取异常
     */
    public CharSequence readSource(InputStreamReader isr) throws IOException {
        StringBuilder builder = new StringBuilder(512);
        int value;
        while ((value = isr.read()) >= 0) {
            builder.append((char) value);
        }
        return builder;
    }


    /**
     * 调整错误，主要是获取错误的行数、行偏移量
     *
     * @param text   文本
     * @param errors 错误列表
     */
    public static void adjustErrors(CharSequence text, List<ParseError> errors) {
        if (errors.isEmpty()) return;

        List<ParseError> safeErrors = new ArrayList<>(errors);
        safeErrors.sort(Comparator.comparingInt(ParseError::getPosition));
        int curPos = errors.get(0).getPosition();
        int curIndex = 0;

        int lineNumber = 0;
        int lineOffset = 0;
        int linePosition = 0;
        boolean _r = false;
        for (int charOffset = 0; charOffset < text.length() && curIndex < errors.size(); charOffset++) {
            char ch = text.charAt(charOffset);
            if (ch == '\r') {
                // \r换行
                ++lineNumber;
                linePosition = charOffset + 1;
                lineOffset = 0;
                _r = true;
            } else if (ch == '\n') {
                // \n换行
                if (!_r) {
                    // \n
                    ++lineNumber;
                    linePosition = charOffset + 1;
                    lineOffset = 0;
                } else {
                    // \r\n 忽略，无需增加lineNumber
                    _r = false;
                    ++linePosition;
                }
            } else {
                if (_r) _r = false;
                ++lineOffset;
            }

            if (charOffset == curPos) {
                for (int errorIndex = curIndex; errorIndex < safeErrors.size(); errorIndex++) {
                    ParseError error = safeErrors.get(errorIndex);
                    if (error.getPosition() == charOffset) {
                        error.setLineOffset(lineOffset);
                        error.setLineIndex(lineNumber);
                        error.setLinePosition(linePosition);
                        curIndex = errorIndex + 1;
                    } else {
                        curPos = error.getPosition();
                        curIndex = errorIndex;
                        break;
                    }
                }
            }
        }
        for (int i = curIndex; i < safeErrors.size(); i++) {
            ParseError error = safeErrors.get(i);
            error.setLineIndex(lineNumber);
            error.setLineOffset(lineOffset);
            error.setLinePosition(linePosition);
        }
    }
}
