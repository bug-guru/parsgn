package net.developithecus.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Rule {
    private String name;
    private List<Expression> expressions = new ArrayList<>();

    public String name() {
        return name;
    }

    public Rule name(String name) {
        this.name = name;
        return this;
    }

    public void addExpression(Expression expression) {
        expressions.add(expression);
    }
}
