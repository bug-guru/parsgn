package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingContext;
import net.developithecus.parser.ParsingException;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class OptionalGroupExpression extends GroupExpression {
    private static final Logger logger = Logger.getLogger(OptionalGroupExpression.class.getName());

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {

        @Override
        public Expression next() {
            List<Expression> expressions = getExpressions();
            if (expressions.size() == 1) {
                return expressions.get(0);
            } else {
                SequentialGroupExpression seq = new SequentialGroupExpression();
                seq.addAll(getExpressions());
                return seq;
            }
        }

        @Override
        public void check() throws ParsingException {
            ParsingContext ctx = getCtx();
            logger.entering("OptionalGroupExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                    ctx.markForCommit();
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    ctx.markForRollbackOptional();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + getCtx().getResult());
            }
            logger.exiting("OptionalGroupExpression.Checker", "check", ctx);
        }

        @Override
        protected String getName() {
            return "opt";
        }
    }
}
