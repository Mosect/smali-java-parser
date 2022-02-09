package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenType;

/**
 * 换行
 */
public class SmaliLinefeedToken extends SmaliToken {

    public static SmaliLinefeedToken cr() {
        SmaliLinefeedToken token = new SmaliLinefeedToken();
        token.setCr();
        return token;
    }

    public static SmaliLinefeedToken crlf() {
        SmaliLinefeedToken token = new SmaliLinefeedToken();
        token.setCrlf();
        return token;
    }

    public SmaliLinefeedToken() {
        super(SmaliTokenType.LINEFEED, "\n");
    }

    public void setCr() {
        setText("\r");
    }

    public void setLf() {
        setText("\n");
    }

    public void setCrlf() {
        setText("\r\n");
    }

    public boolean isCr() {
        return "\r".equals(getText());
    }

    public boolean isLf() {
        return "\n".equals(getText());
    }

    public boolean isCrlf() {
        return "\r\n".equals(getText());
    }
}
