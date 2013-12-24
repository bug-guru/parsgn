package net.developithecus.parser.expr;

import net.developithecus.parser.ExpressionCheckerException;
import net.developithecus.parser.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialGroup extends Group<SequentialGroup> {
    @Override
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }

    private class Checker extends ExpressionChecker {
        private List<ExpressionChecker> child = new ArrayList<>();

        private Checker(int pos) {
            super(pos);
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            return null;
        }

        @Override
        protected boolean isOptional() {
            return false;
        }
    }
}
