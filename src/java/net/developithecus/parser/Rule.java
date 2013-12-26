package net.developithecus.parser;

import net.developithecus.parser.expr.SequentialGroupExpression;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Rule extends SequentialGroupExpression {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
