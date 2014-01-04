package net.developithecus.parser.expr;

import net.developithecus.parser.*;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class ReferenceExpression extends Expression {
    private Rule reference;

    public ReferenceExpression(Rule reference) {
        this.reference = reference;
    }

    public ReferenceExpression() {
    }

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) {
        this.reference = reference;
    }

    @Override
    public boolean isHidden() {
        return super.isHidden() || reference.isHidden();
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends GroupingExpressionChecker {

        @Override
        public Expression next() {
            return getReference().getExpression();
        }

        @Override
        public String getGroupName() {
            return reference.getName();
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            return ResultType.COMMIT;
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return ResultType.ROLLBACK_OPTIONAL;
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return ResultType.ROLLBACK;
        }
    }
}
