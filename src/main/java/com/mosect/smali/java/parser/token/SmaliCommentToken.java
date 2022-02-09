package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenException;
import com.mosect.smali.java.parser.SmaliTokenType;

public class SmaliCommentToken extends SmaliToken {

    public static SmaliCommentToken parse(String text) throws SmaliTokenException {
        SmaliCommentToken token = new SmaliCommentToken(text);
        token.check();
        return token;
    }

    public SmaliCommentToken(String text) {
        super(SmaliTokenType.COMMENT, text);
    }

    @Override
    public void check() throws SmaliTokenException {
        super.check();
        if (!getText().startsWith("#")) {
            throw new SmaliTokenException("Invalid comment text", 0);
        }
    }

    public String getComment() {
        return getText().substring(1);
    }
}
