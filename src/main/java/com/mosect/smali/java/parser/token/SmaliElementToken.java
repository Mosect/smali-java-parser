package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenException;
import com.mosect.smali.java.parser.SmaliTokenType;

public class SmaliElementToken extends SmaliToken {

    public static SmaliElementToken parse(String text) throws SmaliTokenException {
        SmaliElementToken token = new SmaliElementToken(text);
        token.check();
        return token;
    }

    public SmaliElementToken(String text) {
        super(SmaliTokenType.ELEMENT, text);
    }
}
