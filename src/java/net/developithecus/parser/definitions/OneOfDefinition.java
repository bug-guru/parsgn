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
public class OneOfDefinition extends AbstractListDefinition {

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {
        private Queue<AbstractDefinition> expected = new LinkedList<AbstractDefinition>();

        public Expr() {
            expected.addAll(getEntries());
        }

        @Override
        public void init(Turn turn) throws ParserException {
            afterRollback(turn);
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            turn.commit();
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            AbstractDefinition exp = expected.poll();
            if (exp == null) {
                turn.rollback();
            } else {
                turn.push(exp.buildExpression());
            }
        }

        @Override
        public AbstractDefinition getDefinition() {
            return OneOfDefinition.this;
        }
    }
}
