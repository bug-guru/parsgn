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

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.ReferenceExpression;
import guru.bug.tools.parsgn.processing.CodePointSource;
import guru.bug.tools.parsgn.processing.ParsingContext;
import guru.bug.tools.parsgn.processing.debug.DebugInjection;

import java.io.IOException;
import java.io.Reader;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class Parser {
    private final ReferenceExpression root;

    public Parser(ReferenceExpression root) {
        this.root = root;
    }

    public <T> void parse(Reader input, ResultBuilder<T> builder) throws ParsingException, IOException {
        this.parse(input, builder, null);
    }

    public <T> void parse(Reader input, ResultBuilder<T> builder, DebugInjection debugInjection) throws ParsingException, IOException {
        CodePointSource src = new CodePointSource(input);
        ParsingContext<T> ctx = new ParsingContext<>(root, builder, src);
        ctx.parse(debugInjection);
    }

}
