/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser.definitions;

import net.developithecus.parser.Parser;
import net.developithecus.parser.ParserException;
import net.developithecus.parser.SetOfChars;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class CharOfDefinition extends AbstractSingleRefDefinition {

    private SetOfChars chars = new SetOfChars();

    public CharOfDefinition include(String str) {
        chars.include(str);
        return this;
    }

    public CharOfDefinition exclude(String str) {
        chars.exclude(str);
        return this;
    }

    public CharOfDefinition includeAll() {
        chars.includeAll();
        return this;
    }

    public CharOfDefinition excludeAll() {
        chars.excludeAll();
        return this;
    }

    public CharOfDefinition include(SetOfChars set) {
        chars.include(set);
        return this;
    }

    public CharOfDefinition exclude(SetOfChars set) {
        chars.include(set);
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
            if (result.length() != 1) {
                turn.rollback();
            } else {
                char resultChar = result.charAt(0);
                if (chars.contains(resultChar)) {
                    turn.commit(String.valueOf(resultChar));
                } else {
                    turn.rollback();
                }
            }
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            turn.rollback();
        }

        @Override
        public AbstractDefinition getDefinition() {
            return CharOfDefinition.this;
        }

    }
}
