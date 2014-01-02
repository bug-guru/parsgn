package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.1.1
 * @since 1.0
 */
public abstract class StringExpressionChecker implements ExpressionChecker {
    @Override
    public final Expression next() {
        return null;
    }

    protected abstract String getResult();

    protected abstract ResultType check(int codePoint) throws ParsingException;

    @Override
    public final CheckResult check(int codePoint, ResultType prevResult) throws ParsingException {
        ResultType result = check(codePoint);
        switch (result) {
            case COMMIT:
                return new CheckResult() {
                    @Override
                    public ResultType doAction(ParsingContext ctx) throws ParsingException {
                        String value = getResult();
                        ctx.pop();
                        if (value != null) {
                            ctx.peek().commitValue(value);
                        }
                        return ResultType.COMMIT;
                    }
                };
            case CONTINUE:
                return CheckResult.doContinue();
            case ROLLBACK_OPTIONAL:
                return CheckResult.doOptionalRollback();
            case ROLLBACK:
                return CheckResult.doRollback();
            default:
                throw new InternalParsingException("unknown result " + result);
        }
    }
}
