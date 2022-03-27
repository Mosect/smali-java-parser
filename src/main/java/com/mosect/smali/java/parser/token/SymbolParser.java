package com.mosect.smali.java.parser.token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SymbolParser extends TokenParser {

    protected List<String> symbols = new ArrayList<>();

    public SymbolParser() {
        Collections.addAll(symbols,
                "\r\n", "\r", "\n", "\t", " ",
                "..", "{", "}", "=", "/", ";", "(", ")"
        );
    }

    @Override
    protected void onParse() {
        for (String symbol : symbols) {
            if (source.match(symbol, false)) {
                int end = source.getOffset() + symbol.length();
                putToken(new Token(TokenType.SYMBOL, source.getOffset(), end));
                break;
            }
        }
    }
}
