package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.1.1
 * @since 1.0
 */
public abstract class ParentExpressionChecker implements ExpressionChecker {

    public abstract ResultType checkChildCommit() throws ParsingException;

    public abstract ResultType checkChildOptionalRollback() throws ParsingException;

    public abstract ResultType checkChildRollback() throws ParsingException;

    public abstract CheckResult check(ResultType childResult) throws ParsingException;

    @Override
    public CheckResult check(int codePoint, ResultType prevResult) throws ParsingException {
        ResultType childResult;
        switch (prevResult) {
            case COMMIT:
                childResult = checkChildCommit();
                break;
            case ROLLBACK_OPTIONAL:
                childResult = checkChildOptionalRollback();
                break;
            case ROLLBACK:
                childResult = checkChildRollback();
                break;
            default:
                throw new InternalParsingException("unknown result " + prevResult);
        }
        return check(childResult);
    }
}
