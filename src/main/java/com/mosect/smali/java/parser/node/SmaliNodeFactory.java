package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.SmaliException;
import com.mosect.smali.java.parser.SmaliParseError;
import com.mosect.smali.java.parser.SmaliParseResult;
import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenType;
import com.mosect.smali.java.parser.util.SmaliUtils;

import java.util.List;

public class SmaliNodeFactory {

    public SmaliNodeFactory() {
    }

    public TreeDocument createDocument() {
        return new TreeDocument();
    }

    public TokenComment createComment(String comment) {
        for (int i = 0; i < comment.length(); i++) {
            char ch = comment.charAt(i);
            if (ch == '\r' || ch == '\n') {
                throw new IllegalArgumentException("Comment contains \\r or \\n");
            }
        }
        return new TokenComment("#" + comment);
    }

    public SmaliParseResult<TreeDocument> parseDocument(SmaliNodeSource source) {
        SmaliParseResult<TreeDocument> result = new SmaliParseResult<>();
        TreeDocument document = createDocument();
        result.setResult(document);

        return null;
    }

    public void parseElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        while (nextCodeTokens(source, parent, outErrors)) {
            SmaliToken token = source.current();
            switch (token.getText()) {
                case ".class": // class element
                    parseClassElement(source, addElement(source, parent, new TreeClassElement()), outErrors);
                    break;
                case ".source": // source element
                    parseSourceElement(source, addElement(source, parent, new TreeSourceElement()), outErrors);
                    break;
                case ".super": // super element
                    parseSuperElement(source, addElement(source, parent, new TreeSuperElement()), outErrors);
                    break;
                case ".field": // field element
                    parseFieldElement(source, addElement(source, parent, new TreeFieldElement()), outErrors);
                    break;
                case ".method": // method element
                    parseMethodElement(source, addElement(source, parent, new TreeMethodElement()), outErrors);
                    break;
                case ".annotation": // annotation element
                    parseAnnotationElement(source, addElement(source, parent, new TreeAnnotationElement()), outErrors);
                    break;
                case ".implements": // implements element
                    parseImplementsElement(source, addElement(source, parent, new TreeImplementsElement()), outErrors);
                    break;
                default:
                    makeError(source, SmaliParseError.CODE_UNSUPPORTED_ELEMENT, "Unsupported element " + token, outErrors);
                    break;
            }
            source.next();
        }
    }

    private <T extends SmaliTree> T addElement(SmaliNodeSource source, SmaliTree parent, T element) {
        element.addChild(new TokenElement(source.current().getText()));
        source.next();
        parent.addChild(element);
        return element;
    }

    private void parseClassElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        while (nextCodeTokens(source, parent, outErrors)) {
            SmaliToken token = source.current();
            if (token.getType() == SmaliTokenType.UNKNOWN) {
                try {
                    TokenClassPath classPath = getClassPath(token.getText());
                    if (null == classPath) {
                        TokenKeyword keyword = getKeyword(token.getText());
                        if (null == keyword) {
                            // not a class path and not a key word
                            makeError(source, SmaliParseError.CODE_UNEXPECTED_TOKEN, "Unexpected token", outErrors);
                            return;
                        } else {
                            // keyword
                            if (keyword.canModifyClass()) {
                                // valid keyword
                                parent.addChild(keyword);
                                source.next();
                            } else {
                                // unsupported keyword
                                makeError(source, SmaliParseError.CODE_UNSUPPORTED_TOKEN, "Unsupported token " + keyword.getText(), outErrors);
                                return;
                            }
                        }
                    } else {
                        // found class path
                        parent.addChild(classPath);
                        source.next();
                        return;
                    }
                } catch (SmaliException e) {
                    makeError(source, SmaliParseError.CODE_INVALID_CLASS_PATH, e.getMessage(), outErrors);
                    return;
                }
            }
        }
    }

    private void parseSourceElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        if (nextCodeTokens(source, parent, outErrors)) {
            SmaliToken token = source.current();
            if (token.getType() == SmaliTokenType.STRING) {
                parent.addChild(new TokenString(token.getText()));
                source.next();
            } else {
                makeError(source, SmaliParseError.CODE_UNEXPECTED_TOKEN, "Unexpected token", outErrors);
            }
        }
    }

    private void parseSuperElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        if (nextCodeTokens(source, parent, outErrors)) {
            SmaliToken token = source.current();
            if (token.getType() == SmaliTokenType.UNKNOWN) {
                try {
                    TokenClassPath classPath = getClassPath(token.getText());
                    if (null == classPath) {
                        // must be class path
                        makeError(source, SmaliParseError.CODE_UNEXPECTED_TOKEN, "Unexpected token", outErrors);
                    } else {
                        parent.addChild(classPath);
                        source.next();
                    }
                } catch (SmaliException e) {
                    makeError(source, SmaliParseError.CODE_INVALID_CLASS_PATH, e.getMessage(), outErrors);
                }
            }
        }
    }

    private void parseImplementsElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        // as same as parseSuperElement
        parseSuperElement(source, parent, outErrors);
    }

    private void parseFieldElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        while (nextCodeTokens(source, parent, outErrors)) {
        }
    }

    private void parseMethodElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
    }

    private void parseAnnotationElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
    }

    public void parseInstruction(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
    }

    private boolean nextCodeTokens(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        while (source.hasCurrent()) {
            SmaliToken token = source.current();
            switch (token.getType()) {
                case COMMENT:
                    parent.addChild(new TokenComment(token.getText()));
                    break;
                case WHITESPACE:
                    parent.addChild(new TokenWhitespace(token.getText()));
                    break;
                case LINEFEED:
                    parent.addChild(new TokenLinefeed(token.getText()));
                    break;
                default:
                    return true;
            }
            source.next();
        }
        return false;
    }

    private TokenClassPath getClassPath(String text) throws SmaliException {
        if (text.length() > 2 && text.startsWith("L") && text.endsWith(";")) {
            int mode = 0;
            for (int i = 1; i < text.length() - 1; i++) {
                char ch = text.charAt(i);
                switch (mode) {
                    case 0:
                        if (SmaliUtils.isDec(ch) || !SmaliUtils.isValidName(ch)) {
                            // Unsupported start with number
                            throw new SmaliException("Invalid path char " + SmaliUtils.charStr(ch), i);
                        }
                        mode = 1;
                        break;
                    case 1:
                        if (ch == '/') {
                            mode = 0;
                        } else if (!SmaliUtils.isValidName(ch)) {
                            throw new SmaliException("Invalid path char " + SmaliUtils.charStr(ch), i);
                        }
                        break;
                }
            }
            if (mode != 0) {
                throw new SmaliException("Invalid class path " + text, 0);
            }
            return new TokenClassPath(text);
        }
        return null;
    }

    private TokenKeyword getKeyword(String text) {
        TokenKeyword.Value value = TokenKeyword.valueOf(text);
        if (null != value) {
            return new TokenKeyword(value);
        }
        return null;
    }

    private void makeError(SmaliNodeSource source, int code, String message, List<SmaliParseError> outErrors) {
        int offset = source.getCharOffset();
        SmaliParseError error = new SmaliParseError(code, message);
        error.setOffset(offset);
        outErrors.add(error);
    }
}
