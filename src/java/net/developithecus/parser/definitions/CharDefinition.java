/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.SetOfChars;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class CharDefinition extends AbstractDefinition {

    private SetOfChars chars = new SetOfChars();

    public CharDefinition include(String str) {
        chars.include(str);
        return this;
    }

    public CharDefinition exclude(String str) {
        chars.exclude(str);
        return this;
    }

    public CharDefinition includeAll() {
        chars.includeAll();
        return this;
    }

    public CharDefinition excludeAll() {
        chars.excludeAll();
        return this;
    }

    public CharDefinition include(SetOfChars set) {
        chars.include(set);
        return this;
    }

    public CharDefinition exclude(SetOfChars set) {
        chars.include(set);
        return this;
    }

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        @Override
        public AbstractDefinition getDefinition() {
            return CharDefinition.this;
        }

        @Override
        public void turn(Turn turn, int ch) throws ParserException {
            if (ch == -1) {
                turn.rollback();
                return;
            }
            if (chars.contains((char) ch)) {
                turn.accept();
                turn.commit(String.valueOf((char) ch));
            } else {
                turn.rollback();
            }
        }
    }
}
