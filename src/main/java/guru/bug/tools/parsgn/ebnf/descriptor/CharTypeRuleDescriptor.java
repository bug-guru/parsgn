/*
 * Copyright (c) 2024 Dimitrijs Fedotovs http://www.bug.guru
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

package guru.bug.tools.parsgn.ebnf.descriptor;

import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.processing.Position;

import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CharTypeRuleDescriptor extends AbstractExpressionTypeDescriptor {
    private final NameRuleDescriptor charTypeName;

    protected CharTypeRuleDescriptor(NameRuleDescriptor charTypeName, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.charTypeName = charTypeName;
    }

    public static CharTypeRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        NameRuleDescriptor charTypeName = null;
        for (var child : children) {
            if (child instanceof NameRuleDescriptor n) {
                charTypeName = n;
            }
        }
        if (charTypeName == null) {
            throw new IllegalArgumentException("CharType name is not specified");
        }
        return new CharTypeRuleDescriptor(charTypeName, startPosition, endPosition);
    }

    @Override
    public Expression generate(RuleFactory rf) {
        var n = charTypeName.getValue();
        var t = CharType.valueOf(n);
        return updatePosTo(rf.charType(t));
    }


    @Override
    protected void print(StringBuilder sb) {
        sb.append("#");
        charTypeName.print(sb);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharTypeRuleDescriptor that)) return false;
        return Objects.equals(charTypeName, that.charTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(charTypeName);
    }
}
