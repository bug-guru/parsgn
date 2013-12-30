package net.developithecus.parser;

import java.util.Map;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public class RuleRef extends Rule {
    private final Map<String, RuleDef> definitions;
    private RuleDef rule;
    private Expression expression;

    public RuleRef(String name, Map<String, RuleDef> definitions) {
        super(name);
        this.definitions = definitions;
        init();
    }

    private void init() {
        if (rule == null) {
            rule = definitions.get(getName());
            if (rule != null) {
                setCompact(rule.isCompact());
            }
        }
        if (expression == null) {
            expression = rule == null ? null : rule.getExpression();
        }
    }

    public Expression getExpression() {
        init();
        return expression;
    }

    @Override
    public boolean isCompact() {
        init();
        return super.isCompact();
    }
}
