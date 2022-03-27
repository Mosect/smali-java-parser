package com.mosect.smali.java.parser;

import com.mosect.smali.java.parser.token.Token;
import com.mosect.smali.java.parser.token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * smali解析器
 */
public class SmaliParser {

    protected final List<String> symbols = new ArrayList<>();

    public SmaliParser() {
        symbols.add("\r\n");
        symbols.add("\r");
        symbols.add("\n");
        symbols.add("\t");
        symbols.add(" ");
        symbols.add("{");
        symbols.add("}");
        symbols.add("..");
        symbols.add("=");
    }

    /**
     * 解析token
     *
     * @param source    源码
     * @param outTokens 输出的token
     * @param outErrors 输出的错误
     */
    public void parseTokens(Source source, List<Token> outTokens, List<ParseError> outErrors) {
        TokenParser[] tokenParsers = {
                this::parseComment,
                this::parseString,
                this::parseChar,
                this::parseNumber,
                this::parseSymbol
        };
        int unknownStart = source.getOffset();
        while (source.hasMore()) {
            Token token = null;
            for (TokenParser tokenParser : tokenParsers) {
                token = tokenParser.parseToken(source, outErrors);
                if (null != token) {
                    if (token.getStart() > unknownStart) {
                        outTokens.add(new Token(TokenType.UNKNOWN, unknownStart, token.getStart()));
                    }
                    outTokens.add(token);
                    unknownStart = token.getEnd();
                    break;
                }
            }
            if (null == token) {
                source.addOffsetOne();
            }
        }
        if (source.length() > unknownStart) {
            outTokens.add(new Token(TokenType.UNKNOWN, unknownStart, source.length()));
        }
    }

    protected Token parseComment(Source source, List<ParseError> outErrors) {
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
            source.setOffset(end);
            return new Token(TokenType.COMMENT, start, end);
        }
        return null;
    }

    protected Token parseString(Source source, List<ParseError> outErrors) {
        if (source.match("\"", false)) {
            int length = source.length();
            int end = source.length();
            boolean loop = true;
            int offset = source.getOffset();
            while (offset < length && loop) {
                switch (source.charAt(offset)) {
                    case '"':
                        end = offset + 1;
                        loop = false;
                        break;
                    case '\r':
                    case '\n':
                        end = offset;
                        loop = false;
                        outErrors.add(new ParseError("STRING_MISSING_END", "Missing string end before linefeed", offset - 1));
                        break;
                    default:
                }
            }
        }
        return null;
    }

    protected Token parseChar(Source source, List<ParseError> outErrors) {
        return null;
    }

    protected Token parseNumber(Source source, List<ParseError> outErrors) {
        return null;
    }

    protected Token parseSymbol(Source source, List<ParseError> outErrors) {
        return null;
    }

    protected interface TokenParser {
        Token parseToken(Source source, List<ParseError> outErrors);
    }
}
