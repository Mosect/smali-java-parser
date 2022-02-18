package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.SmaliParseError;
import com.mosect.smali.java.parser.SmaliParseResult;
import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenType;

import java.util.List;

public class SmaliNodeFactory {

    public SmaliNodeFactory() {
    }

    public SmaliDocument createDocument() {
        return new SmaliDocument();
    }

    public SmaliComment createComment(String comment) {
        for (int i = 0; i < comment.length(); i++) {
            char ch = comment.charAt(i);
            if (ch == '\r' || ch == '\n') {
                throw new IllegalArgumentException("Comment contains \\r or \\n");
            }
        }
        return new SmaliComment("#" + comment);
    }

    public SmaliParseResult<SmaliDocument> parseDocument(SmaliNodeSource source) {
        SmaliParseResult<SmaliDocument> result = new SmaliParseResult<>();
        SmaliDocument document = createDocument();
        result.setResult(document);
        List<SmaliToken> tokens = source.getTokens();
        while (source.getOffset() < tokens.size()) {
            parseElement(source, document, result.getErrors());
        }
        return null;
    }

    public void parseElement(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
        List<SmaliToken> tokens = source.getTokens();
        skipWhitespaceAndComment(source, parent);
        if (source.getOffset() < tokens.size()) {
            SmaliToken token = tokens.get(source.getOffset());
            if (token.getType() == SmaliTokenType.ELEMENT) {
                switch (token.getText()) {
                    case ".class":
                    case ".source":
                    case ".super":
                    case ".field":
                    case ".method":
                    case ".annotation":
                    case ".implements":
                        break;
                    default:
                        makeError(source, SmaliParseError.CODE_UNSUPPORTED_ELEMENT, "Unsupported element " + token, outErrors);
                        break;
                }
            } else {
                makeError(source, SmaliParseError.CODE_UNEXPECTED_TOKEN, "Unexpected token " + token, outErrors);
            }
        }
    }

    public void parseInstruction(SmaliNodeSource source, SmaliTree parent, List<SmaliParseError> outErrors) {
    }

    private void skipWhitespaceAndComment(SmaliNodeSource source, SmaliTree parent) {
        List<SmaliToken> tokens = source.getTokens();
        while (source.getOffset() < tokens.size()) {
            SmaliToken token = tokens.get(source.getOffset());
            switch (token.getType()) {
                case COMMENT:
                    parent.addChild(new SmaliComment(token.getText()));
                    break;
                case WHITESPACE:
                    parent.addChild(new SmaliWhitespace(token.getText()));
                    break;
                case LINEFEED:
                    parent.addChild(new SmaliLinefeed(token.getText()));
                    break;
                default:
                    return;
            }
            source.setOffset(source.getOffset() + 1);
        }
    }

    private void makeError(SmaliNodeSource source, int code, String message, List<SmaliParseError> outErrors) {
        int offset = 0;
        List<SmaliToken> tokens = source.getTokens();
        for (int i = 0; i < source.getOffset(); i++) {
            offset += tokens.get(i).length();
        }
        SmaliParseError error = new SmaliParseError(code, message);
        outErrors.add(error);
    }
}
