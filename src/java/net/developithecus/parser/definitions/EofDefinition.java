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
public class EofDefinition extends AbstractDefinition {

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        @Override
        public AbstractDefinition getDefinition() {
            return EofDefinition.this;
        }

        @Override
        public void turn(Turn turn, int ch) throws ParserException {
            if (ch == -1) {
                turn.commit();
            } else {
                turn.rollback();
            }
        }
    }

}
