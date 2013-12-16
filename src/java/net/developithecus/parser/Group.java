package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Group<T extends Group<T>> extends Expression {
    private List<Expression> expressionList = new ArrayList<>();

    public T addAll(Collection<Expression> expressionList) {
        this.expressionList.addAll(expressionList);
        return (T) this;
    }

    public T addAll(Expression... expressionList) {
        this.expressionList.addAll(Arrays.asList(expressionList));
        return (T) this;
    }

    public void add(Expression expression) {
        expressionList.add(expression);
    }

    public List<Expression> getExpressions() {
        return expressionList;
    }
}
