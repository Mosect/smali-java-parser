package com.mosect.smali.java.parser;

import com.mosect.smali.java.parser.util.SmaliUtils;

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
            if (SmaliUtils.isWhitespace(ch)) {
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
                        if (SmaliUtils.match(text, offset, "\r\n")) {
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
                            if (SmaliUtils.match(text, offset, symbol)) {
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

    /**
     * parse token tree with tokens
     *
     * @param tokenResult tokens result
     * @return SmaliTokenTree
     */
    public SmaliParseResult<SmaliTokenTreeNode> parseTokenTree(SmaliParseResult<List<SmaliToken>> tokenResult) {
        return parseTokenTree(tokenResult.getText(), tokenResult.getResult());
    }

    /**
     * parse token tree with tokens
     *
     * @param rawText raw text
     * @param tokens  tokens
     * @return SmaliTokenTree
     */
    public SmaliParseResult<SmaliTokenTreeNode> parseTokenTree(CharSequence rawText, List<SmaliToken> tokens) {
        SmaliParseResult<SmaliTokenTreeNode> result = new SmaliParseResult<>();
        SmaliTokenTreeNode tokenTree = new SmaliTokenTreeNode(null);
        result.setText(rawText);
        result.setResult(tokenTree);

        int elementStart = 0;
        List<SmaliTokenTreeNode> list = new ArrayList<>(8);
        for (int i = 0; i < tokens.size(); ) {
            SmaliToken token = tokens.get(i);
            list.add(new SmaliTokenTreeNode(token));
            if (token.getType() == SmaliTokenType.ELEMENT) {
                if (".end".equals(token.getText())) {
                    // .end element
                    int endElementNameIndex = findEndElementName(tokens, i + 1);
                    if (endElementNameIndex >= 0) {
                        // found .end element name

                        // first: add .end tokens
                        for (int j = i + 1; j <= endElementNameIndex; j++) {
                            list.add(new SmaliTokenTreeNode(tokens.get(j)));
                        }

                        // second: findElementWithName
                        SmaliToken endElementName = tokens.get(endElementNameIndex);
                        int elementIndex = findElementWithName(list, endElementNameIndex, endElementName.getText());
                        if (elementIndex >= 0) {
                            // found match .end name element
                            handleTokenTree(list, elementStart, i);
                            handleTokenTree(list, elementIndex, endElementNameIndex + 1);
                            // contains .end
                            list.get(elementIndex).setContainsEnd(true);

                            elementStart = endElementNameIndex + 1;
                            i = endElementNameIndex + 1;
                        } else {
                            SmaliParseError error = new SmaliParseError(
                                    SmaliParseError.CODE_INVALID_END_ELEMENT,
                                    "Unsupported .end with name " + endElementName.getText()
                            );
                            error.setOffset(getTextOffset(tokens, i + 1));
                            result.getErrors().add(error);
                            break; // stop parse
                        }
                    } else {
                        // .end element name not found
                        SmaliParseError error = new SmaliParseError(
                                SmaliParseError.CODE_END_ELEMENT_NAME_NOT_FOUND,
                                "End element name not found"
                        );
                        error.setOffset(getTextOffset(tokens, i + 1));
                        result.getErrors().add(error);
                        break; // stop parse
                    }
                } else {
                    // other element
                    handleTokenTree(list, elementStart, i);
                    elementStart = i;
                    ++i;
                }
            } else {
                // not a element
                ++i;
            }
        }
        handleTokenTree(list, elementStart, list.size());

        tokenTree.setChildren(new ArrayList<>());
        for (SmaliTokenTreeNode tree : list) {
            if (null == tree.getParent()) {
                tree.setParent(tokenTree);
                tokenTree.getChildren().add(tree);
            }
        }

        for (SmaliParseError error : result.getErrors()) {
            SmaliParseError.initError(rawText, error, error.getOffset());
        }

        return result;
    }

    private static int findEndElementName(List<SmaliToken> list, int offset) {
        for (int i = offset; i < list.size(); i++) {
            SmaliToken token = list.get(i);
            switch (token.getType()) {
                case UNKNOWN:
                    switch (token.getText()) {
                        case "class":
                        case "super":
                        case "annotation":
                        case "subannotation":
                        case "source":
                        case "method":
                        case "line":
                        case "field":
                        case "locals":
                        case "enum":
                        case "implements":
                        case "catch":
                        case "catchall":
                        case "param":
                        case "array-data":
                        case "sparse-switch":
                        case "packed-switch":
                            return i;
                        default:
                            return -1;
                    }
                case WHITESPACE:
                case LINEFEED:
                case COMMENT:
                    break;
                default:
                    return -1;
            }
        }
        return -1;
    }

    private static int findElementWithName(List<SmaliTokenTreeNode> list, int end, String name) {
        String elementTag = "." + name;
        for (int i = end - 1; i >= 0; i--) {
            SmaliTokenTreeNode tree = list.get(i);
            if (!tree.isContainsEnd() && tree.getToken().getType() == SmaliTokenType.ELEMENT) {
                if (elementTag.equals(tree.getToken().getText())) {
                    // match .end name
                    return i;
                }
            }
        }
        return -1;
    }

    private static void handleTokenTree(List<SmaliTokenTreeNode> list, int start, int end) {
        if (end - start > 0) {
            SmaliTokenTreeNode parent = list.get(start);
            List<SmaliTokenTreeNode> children = parent.getChildren();
            if (null == children) {
                children = new ArrayList<>(8);
                parent.setChildren(children);
            }
            for (int i = start; i < end; i++) {
                SmaliTokenTreeNode child = list.get(i);
                if (null == child.getParent()) {
                    if (parent != child) {
                        child.setParent(parent);
                    }
                    children.add(child);
                }
            }
        }
    }

    private static SmaliToken nextWhitespace(CharSequence text, int offset) {
        int end = text.length();
        for (int i = offset; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!SmaliUtils.isWhitespace(ch)) {
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
                        throw new SmaliException("Missing string start symbol " + SmaliUtils.charStr(bound), i);
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
                            if (SmaliUtils.isOct(ch)) {
                                // \xxx
                                // oct unicode mode
                                mode = 4;
                                size = 1;
                            } else {
                                throw new SmaliException("Invalid char after \\ is " + SmaliUtils.charStr(ch), i);
                            }
                            break;
                    }
                    break;
                case 3: // hex unicode mode
                    if (!SmaliUtils.isHex(ch)) {
                        throw new SmaliException("Invalid char after \\u is " + SmaliUtils.charStr(ch), i);
                    }
                    if (++size == 4) {
                        // end hex
                        mode = 1;
                        ++len;
                    }
                    break;
                case 4: // oct unicode mode
                    if (SmaliUtils.isOct(ch)) {
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
                    } else if (SmaliUtils.isDec(ch)) {
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
                    } else if (SmaliUtils.isDec(ch)) {
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
                    } else if (SmaliUtils.isDec(ch)) {
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
                    if (SmaliUtils.isHex(ch)) {
                        // valid char
                    } else if (ch == 'L' || ch == 't' || ch == 's') {
                        mode = 6; // end mode
                    } else {
                        throw new SmaliException("Invalid hex number " + text.subSequence(start, end), start);
                    }
                    break;
                case 5: // oct mode
                    if (SmaliUtils.isOct(ch)) {
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
                    } else if (SmaliUtils.isDec(ch)) {
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
                    } else if (SmaliUtils.isDec(ch)) {
                        // valid char
                        mode = 10; // end exponential mode
                    } else {
                        throw new SmaliException("Invalid exponential tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 9: // negative exponential mode
                    if (SmaliUtils.isDec(ch)) {
                        // valid char
                        mode = 10; // end exponential mode
                    } else {
                        throw new SmaliException("Invalid exponential tail number " + text.subSequence(start, end), start);
                    }
                    break;
                case 10: // end exponential mode
                    if (ch == 'f') {
                        mode = 6; // end mode
                    } else if (SmaliUtils.isDec(ch)) {
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

    private static int getTextOffset(List<SmaliToken> tokens, int index) {
        int offset = 0;
        for (int i = 0; i < index; i++) {
            offset += tokens.get(i).length();
        }
        return offset;
    }
}
