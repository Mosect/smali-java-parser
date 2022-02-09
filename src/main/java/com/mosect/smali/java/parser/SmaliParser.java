package com.mosect.smali.java.parser;

import com.mosect.smali.java.parser.token.SmaliCommentToken;
import com.mosect.smali.java.parser.token.SmaliElementToken;
import com.mosect.smali.java.parser.token.SmaliLinefeedToken;
import com.mosect.smali.java.parser.token.SmaliNumberToken;
import com.mosect.smali.java.parser.token.SmaliSymbolToken;
import com.mosect.smali.java.parser.token.SmaliWhitespaceToken;

import java.io.File;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class SmaliParser {

    public SmaliDocument parseDocument(File file) {
        return null;
    }

    public SmaliDocument parseDocument(CharSequence text) {
        return null;
    }

    public SmaliDocument parseDocument(Reader reader) {
        return null;
    }

    public SmaliNode parseNode(SmaliNodeType type, List<SmaliToken> tokens) {
        return null;
    }

    public List<SmaliToken> parseTokens(CharSequence text) throws SmaliTokenException {
        List<SmaliToken> tokens = new ArrayList<>(12);
        int mode = 0;
        int offset = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            boolean endMode = false;
            boolean linefeed = false;
            if (ch == '\r' || ch == '\n') {
                // end mode
                endMode = true;
                linefeed = true;
            } else { // if (ch == '\r' || ch == '\n')
                switch (mode) {
                    case 0: // nothing
                        if (Character.isWhitespace(ch)) {
                            // whitespace start
                            mode = 1;
                            offset = i;
                        } else {
                            switch (ch) {
                                case '#': // comment start
                                    mode = 2;
                                    offset = i;
                                    break;
                                case '"': // string start
                                    mode = 3;
                                    offset = i;
                                    break;
                                case '.': // element start
                                    mode = 4;
                                    offset = i;
                                    break;
                                case '-': // symbol -> start
                                    mode = 5;
                                    break;
                                case '{':
                                case '}':
                                case ',':
                                    // symbol
                                    tokens.add(new SmaliSymbolToken(String.valueOf(ch)));
                                    break;
                                default:
                                    if (ch >= '0' && ch <= '9') {
                                        // number start
                                        mode = 6;
                                        offset = i;
                                    } else {
                                        throw new SmaliTokenException("Syntax error", i);
                                    }
                                    break;
                            }
                        }
                        break; // end case 0
                    case 1: // whitespace
                        if (!Character.isWhitespace(ch)) {
                            // end whitespace
                            endMode = true;
                        }
                        break;
                    case 2: // comment

                } // switch mode

            } // end if (ch == '\r' || ch == '\n')
            if (endMode) {
                // end mode
                switch (mode) {
                    case 1: // end whitespace
                        SmaliWhitespaceToken whitespaceToken = new SmaliWhitespaceToken(
                                text.subSequence(offset, i).toString());
                        tokens.add(whitespaceToken);
                        break;
                    case 2: // end comment
                        SmaliCommentToken commentToken = new SmaliCommentToken(
                                text.subSequence(offset, i).toString());
                        tokens.add(commentToken);
                        break;
                    case 3: // end string
                        // Unsupported end string token by \r or \n
                        throw new SmaliTokenException("Missing symbol \"", i);
                    case 4: // end element
                        SmaliElementToken elementToken = new SmaliElementToken(
                                text.subSequence(offset, i).toString());
                        tokens.add(elementToken);
                        break;
                    case 5: // end symbol ->
                        // Unsupported end symbol -> token \r or \n
                        throw new SmaliTokenException("Missing symbol >", i);
                    case 6: // end number
                        SmaliNumberToken numberToken = new SmaliNumberToken(
                                text.subSequence(offset, i).toString());
                        // need check number
                        numberToken.check();
                        tokens.add(numberToken);
                        break;
                }
                // reset mode
                mode = 0;
            }
            if (linefeed) {
                // handle linefeed
                if (ch == '\r') {
                    // add cr linefeed token
                    tokens.add(SmaliLinefeedToken.cr());
                } else {
                    SmaliLinefeedToken cr = null;
                    // find end cr token
                    if (tokens.size() > 0) {
                        SmaliToken token = tokens.get(tokens.size() - 1);
                        if (token.getType() == SmaliTokenType.LINEFEED) {
                            SmaliLinefeedToken linefeedToken = (SmaliLinefeedToken) token;
                            if (linefeedToken.isCr()) {
                                cr = linefeedToken;
                            }
                        }
                    }
                    if (null != cr) {
                        // change cr to crlf
                        cr.setCrlf();
                    } else {
                        // add lf linefeed token
                        tokens.add(new SmaliLinefeedToken());
                    }
                }
            }
        }
        return tokens;
    }

    /**
     * parse SmaliWhitespaceToken and SmaliCommentToken
     *
     * @param text   text
     * @param offset offset
     * @param out    output tokens
     * @return next offset
     */
    private int parseWhitespaceAndComment(CharSequence text, int offset, List<SmaliToken> out) {
        int mode = 0;
        int currentOffset = offset;
        int start = 0;
        while (currentOffset < text.length()) {
            char ch = text.charAt(currentOffset);
            if (ch == 'r' || ch == '\n') {
                makeWhitespaceAndCommentToken(mode, text, start, currentOffset, out);
                return currentOffset;
            }
            if (mode == 0) {
                if (ch == '#') {
                    makeWhitespaceAndCommentToken(mode, text, start, currentOffset, out);
                    // comment start
                    mode = 1;
                    start = currentOffset;
                } else if (Character.isWhitespace(ch)) {
                    // whitespace start
                    mode = 2;
                    start = currentOffset;
                }
            } else if (mode == 2) {
                if (!Character.isWhitespace(ch)) {
                    makeWhitespaceAndCommentToken(mode, text, start, currentOffset, out);
                    return currentOffset;
                }
            }
            ++currentOffset;
        }
        makeWhitespaceAndCommentToken(mode, text, start, currentOffset, out);
        return currentOffset;
    }

    private void makeWhitespaceAndCommentToken(int mode, CharSequence text, int start, int end, List<SmaliToken> out) {
        if (mode == 1) {
            // make comment token
            String str = text.subSequence(start, end).toString();
            SmaliCommentToken token = new SmaliCommentToken(str);
            out.add(token);
        } else if (mode == 2) {
            // make space token
            String str = text.subSequence(start, end).toString();
            SmaliWhitespaceToken token = new SmaliWhitespaceToken(str);
            out.add(token);
        }
    }

    /**
     * parse SmaliLinefeedToken
     *
     * @param text   text
     * @param offset offset
     * @param out    output tokens
     * @return next offset
     */
    private int parseLinefeed(CharSequence text, int offset, List<SmaliToken> out) {
        SmaliLinefeedToken lastToken = null;
        int currentOffset = offset;
        while (currentOffset < text.length()) {
            char ch = text.charAt(currentOffset);
            if (ch == '\r') {
                lastToken = SmaliLinefeedToken.cr();
                out.add(lastToken);
                ++currentOffset;
            } else if (ch == '\n') {
                if (null != lastToken && lastToken.isCr()) {
                    lastToken.setCrlf();
                } else {
                    lastToken = new SmaliLinefeedToken();
                    out.add(lastToken);
                }
                ++currentOffset;
            } else {
                break;
            }
        }
        return currentOffset;
    }
}
