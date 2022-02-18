package com.mosect.smali.java.parser;

import java.util.ArrayList;
import java.util.List;

public class SmaliParser {

    private final static String[] MULTI_CHARS_SYMBOL = {
            "->", ".."
    };

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
                try {
                    token = nextChar(text, offset);
                } catch (SmaliException e) {
                    SmaliParseError error = new SmaliParseError(SmaliParseError.CODE_INVALID_CHAR, e.getMessage());
                    error.setOffset(offset);
                    result.getErrors().add(error);
                    // stop parse
                    break;
                }
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
                    case ':':
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
                        for (String symbol : MULTI_CHARS_SYMBOL) {
                            if (match(text, offset, symbol)) {
                                token = new SmaliToken(SmaliTokenType.SYMBOL, symbol);
                                break;
                            }
                        }
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
            SmaliParseError.initError(text, error, error.getOffset());
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

    private static SmaliToken nextChar(CharSequence text, int offset) throws SmaliException {
        return parseString(SmaliTokenType.CHAR, text, offset, '\'', 1, 1);
    }

    private static SmaliToken nextString(CharSequence text, int offset) throws SmaliException {
        return parseString(SmaliTokenType.STRING, text, offset, '"', 0, Integer.MAX_VALUE);
    }

    private static SmaliToken parseString(SmaliTokenType type, CharSequence text, int offset, char bound, int minLen, int maxLen) throws SmaliException {
        int mode = 0;
        int size = 0;
        int len = 0;
        for (int i = offset; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == '\r' || ch == '\n') {
                throw new SmaliException("Invalid string char \\r or \\n", i);
            }
            switch (mode) {
                case 0:
                    if (ch != bound) {
                        throw new SmaliException("Missing string start symbol " + charStr(bound), i);
                    }
                    mode = 1;
                    break;
                case 1: // normal mode
                    if (ch == bound) {
                        // end string
                        String str = text.subSequence(offset, i + 1).toString();
                        if (len >= minLen && len <= maxLen) {
                            return new SmaliToken(type, str);
                        } else {
                            throw new SmaliException(String.format("String length %s over [%s,%s]", len, minLen, maxLen), offset);
                        }
                    }
                    if (ch == '\\') {
                        // start escaping
                        mode = 2;
                    } else {
                        ++len;
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
                            ++len;
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
                        ++len;
                    }
                    break;
                case 4: // oct unicode mode
                    boolean isOct = ch >= '0' && ch <= '7';
                    if (isOct) {
                        if (++size == 3) {
                            // end oct
                            mode = 1;
                            ++len;
                        }
                    } else {
                        // end oct
                        mode = 1;
                        ++len;
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
        char first = text.charAt(start);
        SmaliToken token = null;
        if (first >= '0' && first <= '9') {
            // number token
            try {
                token = nextNumber(text, start, end);
            } catch (SmaliException e) {
                SmaliParseError error = new SmaliParseError(SmaliParseError.CODE_INVALID_NUMBER, e.getMessage());
                error.setOffset(start);
                result.getErrors().add(error);
            }
        } else if (first == '.') {
            // element
            try {
                token = nextElement(text, start, end);
            } catch (SmaliException e) {
                SmaliParseError error = new SmaliParseError(SmaliParseError.CODE_INVALID_ELEMENT, e.getMessage());
                error.setOffset(start);
                result.getErrors().add(error);
            }
        }
        if (null == token) {
            String str = text.subSequence(start, end).toString();
            token = new SmaliToken(SmaliTokenType.UNKNOWN, str);
        }
        result.getResult().add(token);
    }

    private static SmaliToken nextNumber(CharSequence text, int start, int end) throws SmaliException {
        int mode = 0;
        for (int i = start; i < end; i++) {
            char ch = text.charAt(i);
            switch (mode) {
                case 0: // none
                    if (ch == '0') {
                        mode = 1; // 0 mode
                    } else if (isDec(ch)) {
                        mode = 2; // dec mode
                    } else {
                        // invalid number
                        throw new SmaliException("Invalid number " + text.subSequence(start, end), start);
                    }
                    break;
                case 1: // 0 mode
                    if (ch == 'x') {
                        mode = 4; // hex mode
                    } else if (ch == '.') {
                        mode = 3; // point mode
                    } else if (isDec(ch)) {
                        mode = 5; // oct mode
                    } else if (ch == 'f' || ch == 'L') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid number " + text.subSequence(start, end), start);
                    }
                    break;
                case 2: // dec mode
                    if (ch == '.') {
                        mode = 3; // point mode
                    } else if (isDec(ch)) {
                        // valid char
                    } else if (ch == 'f' || ch == 'L' || ch == 't' || ch == 's') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid dec number " + text.subSequence(start, end), start);
                    }
                    break;
                case 3: // point mode
                    if (ch >= '0' && ch <= '9') {
                        mode = 7; // tail mode
                    } else {
                        throw new SmaliException("Invalid tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 4: // hex mode
                    if (isHex(ch)) {
                        // valid char
                    } else if (ch == 'L' || ch == 't' || ch == 's') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid hex number " + text.subSequence(start, end), start);
                    }
                    break;
                case 5: // oct mode
                    if (isOct(ch)) {
                        // valid char
                    } else if (ch == 'L') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid oct number " + text.subSequence(start, end), start);
                    }
                    break;
                case 6: // end mode
                    throw new SmaliException("Invalid number " + text.subSequence(start, end), start);
                case 7: // tail mode
                    if (ch == 'E' || ch == 'e') {
                        mode = 8; // exponential mode
                    } else if (isDec(ch)) {
                        // valid char
                    } else if (ch == 'f') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 8: // exponential mode
                    if (ch == '-') {
                        mode = 9; // negative exponential mode
                    } else if (isDec(ch)) {
                        // valid char
                        mode = 10; // end exponential mode
                    } else {
                        throw new SmaliException("Invalid exponential tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 9: // negative exponential mode
                    if (isDec(ch)) {
                        // valid char
                        mode = 10; // end exponential mode
                    } else {
                        throw new SmaliException("Invalid exponential tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 10: // end exponential mode
                    if (ch == 'f') {
                        mode = 6; // end mode
                    } else if (isDec(ch)) {
                        // valid char
                    } else {
                        throw new SmaliException("Invalid exponential tail number " + text.subSequence(start, end), start);
                    }
                    break;
            }
        }
        switch (mode) {
            case 1: // 0 mode
            case 2: // dec mode
            case 4: // hex mode
            case 5: // oct mode
            case 6: // end mode
            case 7: // tail mode
            case 10: // positive exponential mode
                return new SmaliToken(SmaliTokenType.NUMBER, text.subSequence(start, end).toString());
            default:
                throw new SmaliException("Invalid number " + text.subSequence(start, end), start);
        }
    }

    private static SmaliToken nextElement(CharSequence text, int start, int end) throws SmaliException {
        String str = text.subSequence(start, end).toString();
        switch (str) {
            case ".class":
            case ".super":
            case ".annotation":
            case ".subannotation":
            case ".source":
            case ".method":
            case ".line":
            case ".field":
            case ".locals":
            case ".enum":
            case ".implements":
            case ".catch":
            case ".catchall":
            case ".param":
            case ".array-data":
            case ".sparse-switch":
            case ".packed-switch":
            case ".end":
                return new SmaliToken(SmaliTokenType.ELEMENT, str);
            default:
                throw new SmaliException("Invalid element " + str, start);
        }
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


    private static boolean match(CharSequence text, int start, int end, String tag) {
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

    private static boolean isWhitespace(char ch) {
        switch (ch) {
            case ' ':
            case '\t':
                return true;
            default:
                return false;
        }
    }

    private static boolean isOct(char ch) {
        return ch >= '0' && ch <= '7';
    }

    private static boolean isDec(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isHex(char ch) {
        return isDec(ch) || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
    }
}
