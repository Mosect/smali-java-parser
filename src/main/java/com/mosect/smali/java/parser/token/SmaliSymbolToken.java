package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenException;
import com.mosect.smali.java.parser.SmaliTokenType;

public class SmaliSymbolToken extends SmaliToken {

    public static SmaliSymbolToken parse(String text) throws SmaliTokenException {
        SmaliSymbolToken token = new SmaliSymbolToken(text);
        token.check();
        return token;
    }

    public SmaliSymbolToken(String text) {
        super(SmaliTokenType.SYMBOL, text);
    }

    @Override
    public void check() throws SmaliTokenException {
        super.check();
        switch (getText()) {
            case "{":
            case "}":
            case ",":
            case "->":
                break;
            default:
                throw new SmaliTokenException("Unsupported symbol: " + getText(), 0);
        }
    }
}
