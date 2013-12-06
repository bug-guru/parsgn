package net.developithecus.parser2;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.7.12
 * @since 1.0
 */
public enum CheckerStatus {
    REQUIRED_MORE(false),
    OPTIONAL_MORE(false),
    MATCHED(true),
    MISMATCHED(true);

    private final boolean leaf;

    private CheckerStatus(boolean leaf) {
        this.leaf = leaf;
    }

    public boolean isLeaf() {
        return leaf;
    }
}
