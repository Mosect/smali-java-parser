package com.mosect.smali.java.parser.node;

import java.io.IOException;
import java.util.List;

/**
 * 节点
 */
public interface Node {

    /**
     * 获取节点类型
     *
     * @return 节点类型
     */
    NodeType getType();

    /**
     * 获取子节点数量
     *
     * @return 子节点数量
     */
    int getChildCount();

    /**
     * 获取子节点列表
     *
     * @return 子节点列表
     */
    List<Node> getChildren();

    /**
     * 追加到目标
     *
     * @param target 目标
     * @throws IOException 读写异常
     */
    void append(Appendable target) throws IOException;
}
