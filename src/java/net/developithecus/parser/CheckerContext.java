package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.27.12
 * @since 1.0
 */
public interface CheckerContext {
    public int getCodePoint();

    public ResultType getResult();

    public void markForContinue();

    public void markForRollbackOptional();

    public void markForRollback();

    public void markForCommit();

    public void markForCommitGroup(String groupNodeValue);

    public void markForCommit(String nodeValue);

    public boolean hasCommitted();
}
