package com.mosect.smali.java.parser.node;

public class TokenKeyword extends SmaliTokenNode {

    public static Value valueOf(String text) {
        switch (text) {
            case "runtime":
                return Value.RUNTIME;
            case "constructor":
                return Value.CONSTRUCTOR;
            case "public":
                return Value.PUBLIC;
            case "protected":
                return Value.PROTECTED;
            case "private":
                return Value.PRIVATE;
            case "final":
                return Value.FINAL;
            default:
                return null;
        }
    }

    public enum Value {
        RUNTIME("runtime"),
        CONSTRUCTOR("constructor"),
        PUBLIC("public"),
        PROTECTED("protected"),
        PRIVATE("private"),
        FINAL("final"),

        ;

        private final String text;

        Value(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private final Value value;

    TokenKeyword(Value value) {
        super(SmaliNodeType.TOKEN_KEYWORD, value.getText());
        this.value = value;
    }

    public boolean canModifyClass() {
        switch (value) {
            case PUBLIC:
            case PRIVATE:
            case PROTECTED:
            case FINAL:
                return true;
            default:
                return false;
        }
    }

    public boolean canModifyMethod() {
        switch (value) {
            case CONSTRUCTOR:
            case PUBLIC:
            case PRIVATE:
            case PROTECTED:
            case FINAL:
                return true;
            default:
                return false;
        }
    }

    public boolean canModifyField() {
        switch (value) {
            case PUBLIC:
            case PRIVATE:
            case PROTECTED:
            case FINAL:
                return true;
            default:
                return false;
        }
    }

    public Value getValue() {
        return value;
    }
}
