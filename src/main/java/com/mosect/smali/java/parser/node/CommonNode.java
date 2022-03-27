package com.mosect.smali.java.parser.node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用节点
 */
public class CommonNode implements Node {

    private final NodeType type;
    private List<Node> children;

    public CommonNode(NodeType type) {
        this.type = type;
    }

    @Override
    public NodeType getType() {
        return type;
    }

    @Override
    public int getChildCount() {
        if (null != children) return children.size();
        return 0;
    }

    @Override
    public List<Node> getChildren() {
        if (null == children) children = new ArrayList<>();
        return children;
    }

    @Override
    public void append(Appendable target) throws IOException {
        if (getChildCount() > 0) {
            for (Node child : getChildren()) {
                child.append(target);
            }
        }
    }

    @Override
    public String toString() {
        return "CommonNode{" +
                "type=" + type +
                ", childCount=" + getChildCount() +
                '}';
    }
}
