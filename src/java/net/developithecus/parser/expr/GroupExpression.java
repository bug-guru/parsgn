package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class GroupExpression extends Expression {
    private List<Expression> expressionList = new ArrayList<>();

    public void addAll(Collection<Expression> expressionList) {
        this.expressionList.addAll(expressionList);
    }

    public void addAll(Expression... expressionList) {
        this.expressionList.addAll(Arrays.asList(expressionList));
    }

    public void add(Expression expression) {
        expressionList.add(expression);
    }

    public List<Expression> getExpressions() {
        return expressionList;
    }
}
