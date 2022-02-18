package com.mosect.smali.java.parser.node;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public abstract class SmaliTree extends SmaliNode {

    private List<SmaliNode> children;

    SmaliTree(SmaliNodeType type) {
        super(type);
    }

    public int getChildCount() {
        return null == children ? 0 : children.size();
    }

    public void addChild(int index, SmaliNode child) {
        if (null != child.getParent()) {
            throw new IllegalArgumentException("Child object exist parent");
        }
        if (null == children) {
            children = new ArrayList<>();
        }
        child.setParent(this);
        children.add(index, child);
    }

    public void addChild(SmaliNode child) {
        addChild(getChildCount(), child);
    }

    public boolean removeChild(SmaliNode child) {
        if (null != children) {
            return children.remove(child);
        }
        return false;
    }

    public SmaliNode removeChild(int index) {
        if (null != children) {
            SmaliNode old = children.remove(index);
            if (null != old) {
                old.setParent(null);
                return old;
            }
        }
        return null;
    }

    public SmaliNode setChild(int index, SmaliNode child) {
        if (null != child.getParent()) {
            throw new IllegalArgumentException("Child object exist parent");
        }
        if (null != children) {
            SmaliNode old = children.set(index, child);
            if (null != old) {
                old.setParent(null);
                return old;
            }
        }
        return null;
    }

    public SmaliNode getChild(int index) {
        if (null != children) {
            return children.get(index);
        }
        return null;
    }

    public int indexOfChild(SmaliNode child) {
        if (null != children) {
            return children.indexOf(child);
        }
        return -1;
    }

    public void clearChildren() {
        if (null != children) {
            children.clear();
        }
    }

    @Override
    public void write(Writer writer) throws IOException {
        beforeWriteChildren(writer);
        if (null != children) {
            for (SmaliNode child : children) {
                child.write(writer);
            }
        }
        afterWriteChildren(writer);
    }

    protected void beforeWriteChildren(Writer writer) throws IOException {
    }

    protected void afterWriteChildren(Writer writer) throws IOException {
    }

    @Override
    public void append(StringBuilder builder) {
        beforeAppendChildren(builder);
        if (null != children) {
            for (SmaliNode child : children) {
                child.append(builder);
            }
        }
        afterAppendChildren(builder);
    }

    protected void beforeAppendChildren(StringBuilder builder) {
    }

    protected void afterAppendChildren(StringBuilder builder) {
    }

    @Override
    public SmaliTree copy() {
        SmaliTree tree = emptyChildrenTree();
        if (null != children) {
            for (SmaliNode child : children) {
                tree.children = new ArrayList<>();
                tree.children.add(child.copy());
            }
        }
        return tree;
    }

    protected abstract SmaliTree emptyChildrenTree();
}
