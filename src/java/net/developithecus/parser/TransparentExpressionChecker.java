package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.1.1
 * @since 1.0
 */
public abstract class TransparentExpressionChecker extends ParentExpressionChecker {
    @Override
    public CheckResult check(ResultType childResult) throws ParsingException {
        switch (childResult) {
            case COMMIT:
                return new CheckResult() {
                    @Override
                    public ResultType doAction(ParsingContext ctx) throws ParsingException {
                        ParsingContext.Holder top = ctx.pop();
                        ctx.peek().merge(top);
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
                throw new InternalParsingException("unknown result " + childResult);
        }
    }
}
