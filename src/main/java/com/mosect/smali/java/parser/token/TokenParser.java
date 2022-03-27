package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.ParseError;
import com.mosect.smali.java.parser.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Token解析器
 */
public class TokenParser {

    private final List<Token> tokens = new ArrayList<>();
    private final List<ParseError> errors = new ArrayList<>();
    protected Source source;

    public void parse(Source source) {
        onClear();
        this.source = source;
        onParse();
    }

    public boolean hasToken() {
        return tokens.size() > 0;
    }

    public boolean hasError() {
        return errors.size() > 0;
    }

    protected void putToken(Token token) {
        source.setOffset(token.getEnd());
        tokens.add(token);
    }

    protected void putError(ParseError error) {
        errors.add(error);
    }

    protected void putError(String id, String message, int position) {
        errors.add(new ParseError(id, message, position));
    }

    protected void onParse() {
    }

    protected void onClear() {
        tokens.clear();
        errors.clear();
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<ParseError> getErrors() {
        return errors;
    }
}
