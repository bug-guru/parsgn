/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class UnexpectedDefinition extends AbstractSingleRefDefinition {

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(buildNestedExpression());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            turn.exception("Unexpected");
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            turn.rollback();
        }


        @Override
        public AbstractDefinition getDefinition() {
            return UnexpectedDefinition.this;
        }

    }
}
