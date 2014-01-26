/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
    private boolean hidden;
    private boolean template;
    private String transform;

    public RuleRef(String name, Map<String, RuleDef> definitions) {
        super(name);
        this.definitions = definitions;
        init();
    }

    private void init() {
        if (rule == null) {
            rule = definitions.get(getName());
            if (rule == null) {
                return;
            }
            hidden = rule.isHidden();
            template = rule.isTemplate();
            transform = rule.getTransform();
            expression = rule.getExpression();
        }
    }

    public Expression getExpression() {
        init();
        return expression;
    }

    @Override
    public boolean isHidden() {
        init();
        return hidden;
    }

    @Override
    public boolean isTemplate() {
        init();
        return template;
    }

    @Override
    public String getTransform() {
        init();
        return transform;
    }
}
