package com.mosect.smali.java.parser.node;

public class TokenClassPath extends SmaliTokenNode {

    private String path;
    private String className;

    TokenClassPath(String text) {
        super(SmaliNodeType.TOKEN_CLASS_PATH, text);
    }

    public String getPath() {
        if (null == path) {
            String text = getText();
            path = text.substring(1, text.length() - 1);
        }
        return path;
    }

    public String getClassName() {
        if (null == className) {
            className = getPath().replace("/", ".").replace("$", ".");
        }
        return className;
    }
}
