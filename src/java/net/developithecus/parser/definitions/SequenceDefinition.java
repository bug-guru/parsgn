/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

import java.util.Iterator;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class SequenceDefinition extends AbstractListDefinition {

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        Iterator<AbstractDefinition> sequence;
        boolean lastIsOptional = false;

        Expr() {
            sequence = getEntries().iterator();
        }

        private AbstractExpression prepareNext() throws ParserException {
            if (!sequence.hasNext()) {
                return null;
            }

            AbstractDefinition def = sequence.next();
            AbstractExpression result = def.buildExpression();
            return result;
        }

        @Override
        public void init(Turn turn) throws ParserException {
            afterCommit(turn);
        }


        @Override
        public void afterCommit(Turn turn) throws ParserException {
            AbstractExpression expr = prepareNext();
            if (expr == null) {
                turn.commit();
            } else {
                lastIsOptional = expr.isOptional();
                turn.push(expr);
            }
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            if (lastIsOptional) {
                afterCommit(turn);
            } else {
                turn.rollback();
            }
        }

        @Override
        public AbstractDefinition getDefinition() {
            return SequenceDefinition.this;
        }

    }

}
