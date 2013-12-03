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
public class ValueDefinition extends AbstractDefinition {

    private String value;

    @Override
    public AbstractExpression buildExpression() throws ParserException {
        return new Expr();
    }

    public ValueDefinition value(String value) {
        this.value = value;
        return this;
    }

    private class Expr extends AbstractExpression {
        private int index;

        @Override
        public void turn(Turn turn, int ch) throws ParserException {
            if (ch == -1) {
                turn.rollback();
                return;
            }
            char curch = value.charAt(index);
            if (curch == ch) {
                turn.accept();
                index++;
                if (index == value.length()) {
                    turn.commit(value);
                }
            } else {
                turn.rollback();
            }
        }

        @Override
        public AbstractDefinition getDefinition() {
            return ValueDefinition.this;
        }
    }
}
