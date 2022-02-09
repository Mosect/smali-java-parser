package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenException;
import com.mosect.smali.java.parser.SmaliTokenType;

public class SmaliWhitespaceToken extends SmaliToken {

    public static SmaliWhitespaceToken parse(String text) throws SmaliTokenException {
        SmaliWhitespaceToken token = new SmaliWhitespaceToken(text);
        token.check();
        return token;
    }

    public SmaliWhitespaceToken(String text) {
        super(SmaliTokenType.WHITESPACE, text);
    }

    @Override
    public void check() throws SmaliTokenException {
        super.check();
        String text = getText();
        if (text.length() == 0) {
            throw new SmaliTokenException("Empty whitespace text", 0);
        }
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (!Character.isWhitespace(ch)) {
                throw new SmaliTokenException("Invalid whitespace char: " + ch, i);
            }
        }
    }
}
