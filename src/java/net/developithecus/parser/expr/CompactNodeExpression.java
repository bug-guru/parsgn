package net.developithecus.parser.expr;

import net.developithecus.parser.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.27.12
 * @since 1.0
 */
public class CompactNodeExpression extends GroupExpression {
    private static final Logger logger = Logger.getLogger(OptionalGroupExpression.class.getName());

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends GroupExpressionChecker {

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
            logger.entering("CompactNodeExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                    collectNodes();
                    StringBuilder compact = new StringBuilder(getNodes().size() * 2);
                    for (Node node : getNodes()) {
                        compact.append(node.getValue());
                    }
                    commitLeafNode(compact.toString());
                    break;
                case ROLLBACK:
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + getCtx().getResult());
            }
            logger.exiting("CompactNodeExpression.Checker", "check", ctx);
        }

        @Override
        protected String getName() {
            return "compact";
        }
    }
}
