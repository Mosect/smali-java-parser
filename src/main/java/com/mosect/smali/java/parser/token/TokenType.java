package com.mosect.smali.java.parser.token;

/**
 * token类型
 */
public enum TokenType {

    /**
     * 未知，需要根据上下文再次解析
     */
    UNKNOWN,
    /**
     * 注释
     */
    COMMENT,
    /**
     * 字符串
     */
    STRING,
    /**
     * 字符
     */
    CHAR,
    /**
     * 数字
     */
    NUMBER,
    /**
     * 符号
     */
    SYMBOL,
    /**
     * 关键字
     */
    KEYWORD,
    /**
     * 节点
     */
    NODE,
    /**
     * 类路径
     */
    CLASSPATH,
    /**
     * 原始类型
     */
    PRIMITIVE,
    /**
     * 命名
     */
    NAMED,
}
