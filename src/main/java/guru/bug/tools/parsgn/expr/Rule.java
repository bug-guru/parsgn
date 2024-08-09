/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class Rule extends BaseExpression {
    private final String name;
    private Expression expression;
    private final List<String> params = new ArrayList<>(2);
    private boolean hidden;
    private boolean compressed;

    public Rule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public Rule hidden(boolean hidden) {
        setHidden(hidden);
        return this;
    }

    public Rule hide() {
        setHidden(true);
        return this;
    }

    public Rule compressed(boolean compress) {
        setCompressed(compress);
        return this;
    }

    public Rule compress() {
        setCompressed(true);
        return this;
    }

    public List<String> getParams() {
        return Collections.unmodifiableList(params);
    }

    public void setParams(List<String> params) {
        this.params.clear();
        if (params == null) {
            return;
        }
        this.params.addAll(params);
    }

    public Rule params(List<String> params) {
        setParams(params);
        return this;
    }

    public Rule params(String... params) {
        setParams(Arrays.asList(params));
        return this;
    }
}
