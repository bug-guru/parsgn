package net.developithecus.parser;

import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public abstract class ResultBuilder<T> {
    private boolean finished = false;

    protected abstract T createNode(String name, Position beginPosition, int length, String value, List<T> children);

    protected abstract void committedRoot(T root);

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
