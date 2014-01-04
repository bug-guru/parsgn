package net.developithecus.parser.expr;

import net.developithecus.parser.*;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class StringExpression extends Expression {
    private String value;
    private String result;
    private int[] codePoints;
    private int len;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.codePoints = StringUtils.toCodePoints(value);
        this.len = codePoints.length;
    }

    public StringExpression value(String value) {
        setValue(value);
        return this;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public StringExpression result(String result) {
        setResult(result);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends StringExpressionChecker {
        private int offset;

        @Override
        protected String getResult() {
            return result;
        }

        @Override
        public ResultType check(int codePoint) throws ParsingException {
            if (codePoints[offset] != codePoint) {
                return ResultType.ROLLBACK;
            } else {
                offset++;
                if (offset == len) {
                    return ResultType.COMMIT;
                } else {
                    return ResultType.CONTINUE;
                }
            }
        }
    }
}
