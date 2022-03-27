package com.mosect.smali.java.parser.node;

import com.mosect.smali.java.parser.token.Token;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Token节点
 */
public class TokenNode implements Node {

    private final static List<Node> CHILDREN = Collections.emptyList();

    private final CharSequence source;
    private final Token token;
    private String text;

    public TokenNode(CharSequence source, Token token) {
        this.source = source;
        this.token = token;
    }

    /**
     * 获取源码
     *
     * @return 源码
     */
    public CharSequence getSource() {
        return source;
    }

    /**
     * 获取token
     *
     * @return token
     */
    public Token getToken() {
        return token;
    }

    /**
     * 获取文本
     *
     * @return 文本
     */
    public String getText() {
        if (null == text) {
            text = source.subSequence(token.getStart(), token.getEnd()).toString();
        }
        return text;
    }

    @Override
    public NodeType getType() {
        return NodeType.TOKEN;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public List<Node> getChildren() {
        return CHILDREN;
    }

    @Override
    public void append(Appendable target) throws IOException {
        target.append(source, token.getStart(), token.getEnd());
    }

    @Override
    public String toString() {
        return token.toString();
    }
}
