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
public class Rule extends AbstractSingleRefDefinition {
    private final String name;

    public Rule(String name) {
        this.name = name;
    }

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Rule{" + name + '}';
    }


    private class Expr extends AbstractExpression {

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(buildNestedExpression());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            turn.commit();
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            turn.rollback();
        }

        @Override
        public AbstractDefinition getDefinition() {
            return Rule.this;
        }

    }

}
