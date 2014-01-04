package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public class RuleDef extends Rule {
    private Expression expression;
    private boolean hidden;
    private boolean template;
    private String transform;

    public RuleDef(String name) {
        super(name);
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public RuleDef hide() {
        setHidden(true);
        return this;
    }

    @Override
    public boolean isTemplate() {
        return template;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

    public RuleDef template() {
        setTemplate(true);
        return this;
    }

    @Override
    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public RuleDef transform(String transform) {
        setTransform(transform);
        return this;
    }
}
