/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs
 */
public class ConvertToDefinition extends AbstractSingleRefDefinition {

    private String stringTo;

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    public ConvertToDefinition to(String str) {
        this.stringTo = str;
        return this;
    }

    private class Expr extends AbstractExpression {

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(buildNestedExpression());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            turn.result().clear();
            turn.commit(stringTo);
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            turn.rollback();
        }

        @Override
        public AbstractDefinition getDefinition() {
            return ConvertToDefinition.this;
        }

    }

}
