package net.developithecus.parser;

import java.util.Map;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public class RuleRef extends Rule {
    private final Map<String, RuleDef> definitions;
    private Expression expression;

    public RuleRef(String name, Map<String, RuleDef> definitions) {
        super(name);
        this.definitions = definitions;
        getExpression();
    }

    public Expression getExpression() {
        if (expression == null) {
            RuleDef def = definitions.get(getName());
            expression = def == null ? null : def.getExpression();
        }
        return expression;
    }

}
