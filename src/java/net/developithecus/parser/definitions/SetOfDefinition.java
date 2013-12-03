/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class SetOfDefinition extends AbstractListDefinition {

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        Queue<AbstractDefinition> expected = new LinkedList<AbstractDefinition>();
        Queue<AbstractDefinition> expectedBack = new LinkedList<AbstractDefinition>();
        boolean atLeastOneFound = false;
        boolean success = false;

        Expr() {
            expected.addAll(getEntries());
        }

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(expected.peek().buildExpression());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            expected.remove();
            atLeastOneFound = true;
            success = true;
            check(turn);
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            expectedBack.add(expected.remove());
            check(turn);
        }

        public void check(Turn turn) throws ParserException {
            if (expected.isEmpty()) {
                if (!atLeastOneFound) {
                    if (success) {
                        turn.commit();
                    } else {
                        turn.rollback();
                    }
                    return;
                }
                expected.addAll(expectedBack);
                expectedBack.clear();
                atLeastOneFound = false;
            }
            AbstractDefinition next = expected.peek();
            if (next == null) {
                turn.commit();
            } else {
                turn.push(next.buildExpression());
            }
        }

        @Override
        public AbstractDefinition getDefinition() {
            return SetOfDefinition.this;
        }

    }

}
