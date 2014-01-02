package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.1.1
 * @since 1.0
 */
public abstract class CheckResult {
    private static final CheckResult OPTIONAL_ROLLBACK = new CheckResult() {
        @Override
        public ResultType doAction(ParsingContext ctx) {
            ParsingContext.Holder holder = ctx.pop();
            ctx.setNextIndex(holder.beginIndex);
            return ResultType.ROLLBACK_OPTIONAL;
        }
    };
    private static final CheckResult ROLLBACK = new CheckResult() {
        @Override
        public ResultType doAction(ParsingContext ctx) {
            ParsingContext.Holder holder = ctx.pop();
            ctx.setNextIndex(holder.beginIndex);
            return ResultType.ROLLBACK;
        }
    };
    private static final CheckResult CONTINUE = new CheckResult() {
        @Override
        public ResultType doAction(ParsingContext ctx) {
            return ResultType.CONTINUE;
        }
    };

    public static CheckResult doOptionalRollback() {
        return OPTIONAL_ROLLBACK;
    }

    public static CheckResult doRollback() {
        return ROLLBACK;
    }

    public static CheckResult doContinue() {
        return CONTINUE;
    }

    public abstract ResultType doAction(ParsingContext ctx) throws ParsingException;
}
