package com.mosect.smali.java.parser;

import java.util.ArrayList;
import java.util.List;

public class SmaliParser {

    public SmaliParseResult<List<SmaliToken>> parseTokens(CharSequence text) {
        SmaliParseResult<List<SmaliToken>> result = new SmaliParseResult<>();
        List<SmaliToken> tokens = new ArrayList<>();
        result.setResult(tokens);

        int offset = 0;
        int codeStart = -1;
        while (offset < text.length()) {
            char ch = text.charAt(offset);
            SmaliToken token = null;
            if (isWhitespace(ch)) {
                // whitespace
                token = nextWhitespace(text, offset);
            } else if (ch == '#') {
                // comment
                token = nextComment(text, offset);
            } else if (ch == '\'') {
                // char
                
            } else if (ch == '"') {
                // string
                try {
                    token = nextString(text, offset);
                } catch (SmaliException e) {
                    SmaliParseError error = new SmaliParseError(SmaliParseError.CODE_INVALID_STRING, e.getMessage());
                    error.setOffset(offset);
                    result.getErrors().add(error);
                    // stop parse
                    break;
                }
            } else {
                switch (ch) {
                    case '{':
                    case '}':
                    case ',':
                    case '(':
                    case ')':
                    case '0':
                        token = new SmaliToken(SmaliTokenType.SYMBOL, String.valueOf(ch));
                        break;
                    case '\r':
                        if (match(text, offset, "\r\n")) {
                            token = new SmaliToken(SmaliTokenType.LINEFEED, "\r\n");
                        } else {
                            token = new SmaliToken(SmaliTokenType.LINEFEED, "\r");
                        }
                        break;
                    case '\n':
                        token = new SmaliToken(SmaliTokenType.LINEFEED, "\n");
                        break;
                    default: // other is code char
                        break;
                }
            }
            if (null == token) {
                // code
                if (codeStart < 0) codeStart = offset;
                ++offset;
            } else {
                // end code
                if (codeStart >= 0) {
                    parseCode(text, codeStart, offset, result);
                    codeStart = -1;
                }

                tokens.add(token);
                offset += token.length();
            }
        }
        if (codeStart >= 0) parseCode(text, codeStart, text.length(), result);
        result.setText(text);
        for (SmaliParseError error : result.getErrors()) {
            result.initError(error, error.getOffset());
        }
        return result;
    }

    private static SmaliToken nextWhitespace(CharSequence text, int offset) {
        int end = text.length();
        for (int i = offset; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!isWhitespace(ch)) {
                end = i;
                break;
            }
        }
        String str = text.subSequence(offset, end).toString();
        return new SmaliToken(SmaliTokenType.WHITESPACE, str);
    }

    private static SmaliToken nextComment(CharSequence text, int offset) {
        int end = text.length();
        for (int i = offset; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\r' || ch == '\n') {
                end = i;
                break;
            }
        }
        String str = text.subSequence(offset, end).toString();
        return new SmaliToken(SmaliTokenType.COMMENT, str);
    }

    private static SmaliToken nextString(CharSequence text, int offset) throws SmaliException {
        int mode = 0;
        int size = 0;
        for (int i = offset; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\r' || ch == '\n') {
                throw new SmaliException("Invalid string char \\r or \\n", i);
            }
            switch (mode) {
                case 0:
                    if (ch != '"') {
                        throw new SmaliException("Missing string start symbol \"", i);
                    }
                    mode = 1;
                    break;
                case 1: // normal mode
                    if (ch == '"') {
                        // end string
                        String str = text.subSequence(offset, i + 1).toString();
                        return new SmaliToken(SmaliTokenType.STRING, str);
                    }
                    if (ch == '\\') {
                        // start escaping
                        mode = 2;
                    }
                    break;
                case 2: // escape mode
                    switch (ch) {
                        case 't': // \t
                        case 'r': // \r
                        case 'n': // \n
                        case '0': // \0
                        case 'b': // \b
                        case 'f': // \f
                        case '"': // \"
                        case '\'': // \'
                        case '\\': // \\
                            mode = 1; // end escape mode
                            break;
                        case 'u': // \uffff
                            mode = 3; // hex unicode mode
                            size = 0;
                            break;
                        default:
                            if (ch >= '0' && ch <= '7') {
                                // \xxx
                                // oct unicode mode
                                mode = 4;
                                size = 1;
                            } else {
                                throw new SmaliException("Invalid char after \\ is " + charStr(ch), i);
                            }
                            break;
                    }
                    break;
                case 3: // hex unicode mode
                    boolean isHex = (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
                    if (!isHex) {
                        throw new SmaliException("Invalid char after \\u is " + charStr(ch), i);
                    }
                    if (++size == 4) {
                        // end hex
                        mode = 1;
                    }
                    break;
                case 4: // oct unicode mode
                    boolean isOct = ch >= '0' && ch <= '7';
                    if (isOct) {
                        if (++size == 3) {
                            // end oct
                            mode = 1;
                        }
                    } else {
                        // end oct
                        mode = 1;
                    }
                    break;
            }
        }
        throw new SmaliException("Missing string end symbol \"", text.length());
    }

    private static String charStr(char ch) {
        return String.format("[%s,%02x]", ch, (int) ch);
    }

    private static void parseCode(CharSequence text, int start, int end, SmaliParseResult<List<SmaliToken>> result) {
        String str = text.subSequence(start, end).toString();
        SmaliToken token = new SmaliToken(SmaliTokenType.UNKNOWN, str);
        result.getResult().add(token);
    }

    private static boolean match(CharSequence text, int offset, String tag) {
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

    private static boolean isWhitespace(char ch) {
        switch (ch) {
            case ' ':
            case '\t':
                return true;
            default:
                return false;
        }
    }
}
