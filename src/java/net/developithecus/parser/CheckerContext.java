package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.27.12
 * @since 1.0
 */
public interface CheckerContext {
    public int getCodePoint();

    public ResultType getResult();

    public void markForContinue() throws ParsingException;

    public void markForRollbackOptional() throws ParsingException;

    public void markForRollback() throws ParsingException;

    public void markForCommit() throws ParsingException;

    public void markForCommitGroup(String groupNodeValue) throws ParsingException;

    public void markForCommit(String nodeValue) throws ParsingException;

    public void markForCommit(int codePoint) throws ParsingException;

    public boolean hasCommitted();
}
