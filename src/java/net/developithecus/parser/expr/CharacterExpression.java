package net.developithecus.parser.expr;

import net.developithecus.parser.CodePointExpressionChecker;
import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ResultType;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class CharacterExpression extends Expression {
    private CharType charType;

    public CharType getCharType() {
        return charType;
    }

    public void setCharType(CharType charType) {
        this.charType = charType;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends CodePointExpressionChecker {
        private int result;

        @Override
        protected int getResult() {
            return result;
        }

        @Override
        public ResultType check(int codePoint) throws ParsingException {
            if (charType.apply(codePoint)) {
                result = codePoint;
                return ResultType.COMMIT;
            } else {
                return ResultType.ROLLBACK;
            }
        }

    }


}
