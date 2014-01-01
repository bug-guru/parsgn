package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public abstract class ResultBuilder<T> {
    private Deque<Holder> stack = new LinkedList<>();
    private boolean finished = false;

    protected abstract T createNode(String name, Position beginPosition, int length);

    protected abstract T createNode(String name, Position beginPosition, int length, String value);

    protected abstract T createNode(String name, Position beginPosition, int length, List<T> children);

    protected abstract void committedRoot(T root);

    public void appendValue(String value) {
        stack.peek().appendValue(value);
    }

    public void appendValue(int codePoint) {
        stack.peek().appendValue(codePoint);
    }

    public void addNode(String nodeName, Position beginPosition, int length) {
        T node = createNode(nodeName, beginPosition, length);
        stack.peek().appendNode(node);
    }

    public void addNodeWithValue(String nodeName, Position beginPosition, int length) {
        Holder holder = stack.peek();
        T node;
        if (holder.committedValue == null || holder.committedValue.length() == 0) {
            node = createNode(nodeName, beginPosition, length);
        } else {
            node = createNode(nodeName, beginPosition, length, holder.committedValue.toString());
        }
        holder.appendNode(node);
        holder.committedValue = null;
    }

    public void addNodeWithChildren(String nodeName, Position beginPosition, int length) {
        Holder holder = stack.peek();
        T node;
        if (holder.committedNodes == null || holder.committedNodes.isEmpty()) {
            node = createNode(nodeName, beginPosition, length);
        } else {
            node = createNode(nodeName, beginPosition, length, holder.committedNodes);
        }
        holder.committedNodes = null;
        holder.appendNode(node);
    }

    public void mergeValue() {
        Holder oldTop = stack.pop();
        if (oldTop.isEmptyValue()) {
            return;
        }
        Holder top = stack.peek();
        if (top.committedValue == null || top.committedValue.length() == 0) {
            top.committedValue = oldTop.committedValue;
        } else {
            top.committedValue.append(oldTop.committedValue);
        }
    }

    public void mergeNodes() {
        Holder oldTop = stack.pop();
        if (oldTop.isEmptyNodes()) {
            return;
        }
        Holder top = stack.peek();
        if (top == null) {
            finished = true;
            committedRoot(oldTop.committedNodes.get(0));
            return;
        }
        if (top.committedNodes == null || top.committedNodes.isEmpty()) {
            top.committedNodes = oldTop.committedNodes;
        } else {
            top.committedNodes.addAll(oldTop.committedNodes);
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void removeHead() {
        stack.pop();
    }

    public void push() {
        stack.push(new Holder());
    }

    private class Holder {
        List<T> committedNodes;
        StringBuilder committedValue;

        public void initValue() {
            if (committedValue == null) {
                committedValue = new StringBuilder();
            }
        }

        public void initNodes() {
            if (committedNodes == null) {
                committedNodes = new ArrayList<>();
            }
        }

        public void appendValue(String value) {
            initValue();
            committedValue.append(value);
        }

        public void appendValue(int codePoint) {
            initValue();
            committedValue.appendCodePoint(codePoint);
        }

        public void appendNode(T node) {
            initNodes();
            committedNodes.add(node);
        }

        public boolean isEmptyNodes() {
            return committedNodes == null || committedNodes.isEmpty();
        }

        public boolean isEmptyValue() {
            return committedValue == null || committedValue.length() == 0;
        }
    }
}
