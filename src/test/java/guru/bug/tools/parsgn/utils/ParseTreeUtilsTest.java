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

package guru.bug.tools.parsgn.utils;

import guru.bug.tools.parsgn.processing.Position;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ParseTreeUtilsTest {
    private static final Position fakePos = new Position(1, 1);
    private static final ParseNode n111 = createNode("n111");
    private static final ParseNode n11 = createNode("n11", n111);
    private static final ParseNode n12 = createNode("n12");
    private static final ParseNode n1 = createNode("n1", n11, n12);
    private static final ParseNode n21 = createNode("n21");
    private static final ParseNode n2 = createNode("n2", n21);
    private static final ParseNode root = createNode("root", n1, n2);

    private static ParseNode createNode(String name) {
        return new ParseNode(name, null, null, fakePos, fakePos);
    }

    private static ParseNode createNode(String name, ParseNode... children) {
        List<ParseNode> childList = Arrays.asList(children);
        return new ParseNode(name, null, childList, fakePos, fakePos);
    }

    @Test
    public void testWalk() throws Exception {
        List<Object> expected = Arrays.asList(Marker.START, root, Marker.START, n1, Marker.START, n11,
                Marker.START, n111, Marker.END, n111, Marker.END, n11, Marker.START, n12, Marker.END, n12, Marker.END, n1,
                Marker.START, n2, Marker.START, n21, Marker.END, n21, Marker.END, n2, Marker.END, root);
        final List<Object> actual = new ArrayList<>();
        ParseTreeUtils.walk(root, false, new ParseNodeVisitor() {
            @Override
            public ParseNodeVisitResult startNode(ParseNode node) {
                actual.add(Marker.START);
                actual.add(node);
                return ParseNodeVisitResult.CONTINUE;
            }

            @Override
            public void endNode(ParseNode node) {
                actual.add(Marker.END);
                actual.add(node);
            }
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testWalkSkipRoot() throws Exception {
        List<Object> expected = Arrays.asList(Marker.START, n1, Marker.START, n11,
                Marker.START, n111, Marker.END, n111, Marker.END, n11, Marker.START, n12, Marker.END, n12, Marker.END, n1,
                Marker.START, n2, Marker.START, n21, Marker.END, n21, Marker.END, n2);
        final List<Object> actual = new ArrayList<>();
        ParseTreeUtils.walk(root, true, new ParseNodeVisitor() {
            @Override
            public ParseNodeVisitResult startNode(ParseNode node) {
                actual.add(Marker.START);
                actual.add(node);
                return ParseNodeVisitResult.CONTINUE;
            }

            @Override
            public void endNode(ParseNode node) {
                actual.add(Marker.END);
                actual.add(node);
            }
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testSkipSubtree() throws Exception {
        List<Object> expected = Arrays.asList(Marker.START, root, Marker.START, n1, Marker.START, n11,
                Marker.END, n11, Marker.START, n12, Marker.END, n12, Marker.END, n1,
                Marker.START, n2, Marker.START, n21, Marker.END, n21, Marker.END, n2, Marker.END, root);
        final List<Object> actual = new ArrayList<>();
        ParseTreeUtils.walk(root, false, new ParseNodeVisitor() {
            @Override
            public ParseNodeVisitResult startNode(ParseNode node) {
                actual.add(Marker.START);
                actual.add(node);
                return node == n11 ? ParseNodeVisitResult.SKIP_SUBTREE : ParseNodeVisitResult.CONTINUE;
            }

            @Override
            public void endNode(ParseNode node) {
                actual.add(Marker.END);
                actual.add(node);
            }
        });

        assertEquals(expected, actual);
    }

    @Test
    public void testTerminate() throws Exception {
        List<Object> expected = Arrays.asList(Marker.START, root, Marker.START, n1,
                Marker.START, n11, Marker.END, n11, Marker.END, n1, Marker.END, root);
        final List<Object> actual = new ArrayList<>();
        ParseTreeUtils.walk(root, false, new ParseNodeVisitor() {
            @Override
            public ParseNodeVisitResult startNode(ParseNode node) {
                actual.add(Marker.START);
                actual.add(node);
                return node == n11 ? ParseNodeVisitResult.TERMINATE : ParseNodeVisitResult.CONTINUE;
            }

            @Override
            public void endNode(ParseNode node) {
                actual.add(Marker.END);
                actual.add(node);
            }
        });

        assertEquals(expected, actual);
    }

    private enum Marker {
        START,
        END
    }
}
