/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
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

package guru.bug.tools.parsgn.builder;

import guru.bug.tools.parsgn.annotations.RuleValue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 * @version 1.0.0
 * @since 1.0.0
 */
public class ModelBuilderFactory {


    public static ModelBuilderFactory newInstance(Class<?>... classes) {
        ModelBuilderFactory result = new ModelBuilderFactory();
        for (Class<?> clazz : classes) {
            result.add(clazz);
        }
        return result;
    }

    private void add(Class<?> clazz) {
        RuleValue ruleName = clazz.getAnnotation(RuleValue.class);
        List<ObjectBuilder> builders = new ArrayList<>();
        for (Method method : clazz.getMethods()) {
            RuleValue methodRuleName = method.getAnnotation(RuleValue.class);
            if (methodRuleName == null || !Modifier.isStatic(method.getModifiers())) {
                continue;
            }
            ObjectBuilder builder = new ObjectBuilder(clazz, ruleName.value(), methodRuleName);
            builders.add(builder);
        }
    }


}
