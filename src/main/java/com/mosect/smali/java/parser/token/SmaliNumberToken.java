package com.mosect.smali.java.parser.token;

import com.mosect.smali.java.parser.SmaliToken;
import com.mosect.smali.java.parser.SmaliTokenException;
import com.mosect.smali.java.parser.SmaliTokenType;

public class SmaliNumberToken extends SmaliToken {

    public static SmaliNumberToken parse(String text) throws SmaliTokenException {
        SmaliNumberToken token = new SmaliNumberToken(text);
        token.check();
        return token;
    }

    public SmaliNumberToken(String text) {
        super(SmaliTokenType.NUMBER, text);
    }

    @Override
    public void check() throws SmaliTokenException {
        super.check();
        boolean valid = isIntegerHex() || isInteger() || isLongHex() || isLong() || isFloat() || isDouble();
        if (!valid) {
            throw new SmaliTokenException("Invalid number text: " + getText(), 0);
        }
    }

    public Number getNumber() {
        if (isInteger()) {
            return Integer.parseInt(getText());
        }
        if (isIntegerHex()) {
            return Integer.parseInt(getText().substring(2), 16);
        }
        if (isLong()) {
            return Long.parseLong(getText().substring(0, getText().length() - 1));
        }
        if (isLongHex()) {
            return Long.parseLong(getText().substring(2, getText().length() - 1), 16);
        }
        if (isFloat()) {
            return Float.parseFloat(getText().substring(0, getText().length() - 1));
        }
        if (isDouble()) {
            return Float.parseFloat(getText());
        }
        return null;
    }

    public boolean isIntegerHex() {
        return getText().matches("^0x[0-9a-fA-F]*[1-9a-fA-F]$");
    }

    public boolean isLongHex() {
        return getText().matches("^0x[0-9a-fA-F]*[1-9a-fA-F]L$");
    }

    public boolean isInteger() {
        return getText().matches("^[1-9]+[0-9]*$");
    }

    public boolean isLong() {
        return getText().matches("^([1-9]+[0-9]*L)|(0L)$");
    }

    public boolean isFloat() {
        return getText().matches("(^0f$)|(^0\\.[0-9]*[1-9]f$)|(^[1-9]+[0-9]*(\\.[0-9]*[1-9])?f$)");
    }

    public boolean isDouble() {
        return getText().matches("(^0$)|(^0\\.[0-9]*[1-9]$)|(^[1-9]+[0-9]*(\\.[0-9]*[1-9])?$)");
    }
}
