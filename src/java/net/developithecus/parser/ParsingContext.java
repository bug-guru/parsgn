package net.developithecus.parser;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public class ParsingContext {
    private int index;
    private int codePoint;
    private int nextIndex;
    private ResultType result;
    private List<Node> committed;

    void reset(int index, int codePoint) {
        this.index = index;
        this.codePoint = codePoint;
        this.nextIndex = index + 1;
        this.result = ResultType.CONTINUE;
        clearCommitted();
    }

    public int getIndex() {
        return index;
    }

    public int getCodePoint() {
        return codePoint;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public ResultType getResult() {
        return result;
    }

    public void setResult(ResultType result) {
        this.result = result;
    }

    public Node getSingleCommittedNode() {
        if (committed.size() != 1) {
            throw new IllegalStateException("Unexpected number of roots: " + committed.size());
        }
        return committed.get(0);
    }

    public void commitSingleNode(Node node) {
        committed = Collections.singletonList(node);
    }

    public void commitNodes(List<Node> nodes) {
        committed = nodes;
        this.result = ResultType.COMMIT;
    }

    public void clearCommitted() {
        committed = Collections.emptyList();
    }

    public List<Node> takeAndClearCommitted() {
        List<Node> result = committed;
        clearCommitted();
        return result;
    }

    public void rollback(int nextIndex) {
        this.result = ResultType.ROLLBACK;
        this.nextIndex = nextIndex;
        clearCommitted();
    }
}
