/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.Parser;
import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ValueOfDefinition extends AbstractSingleRefDefinition {

    private String string;

    public void string(String str) {
        this.string = str;
    }

    @Override
    public ValueOfDefinition definition(AbstractDefinition d) {
        super.definition(d);
        return this;
    }

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {
        private StringBuilder result = new StringBuilder();

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(buildNestedExpression());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            Parser.buildCompactValue(turn.result(), result);
            turn.result().clear();
            String strResult = result.toString();
            int resultLen = strResult.length();
            int expectLen = string.length();
            if (resultLen > expectLen) {
                turn.rollback();
                return;
            } else if (resultLen < expectLen) {
                if (!string.startsWith(strResult)) {
                    turn.rollback();
                }
            } else if (string.equals(result.toString())) {
                turn.commit(result.toString());
            } else {
                turn.rollback();
            }
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            turn.rollback();
        }

        @Override
        public AbstractDefinition getDefinition() {
            return ValueOfDefinition.this;
        }

    }
}
