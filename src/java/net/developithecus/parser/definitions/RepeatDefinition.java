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
public class RepeatDefinition extends AbstractDefinition {

    private enum Stage {
        FROM {
            @Override
            public Stage next() {
                return CHECK;
            }
        },
        CHECK {
            @Override
            public Stage next() {
                return BODY;
            }
        },
        BODY {
            @Override
            public Stage next() {
                return CHECK;
            }
        };

        public abstract Stage next();
    }

    private AbstractDefinition bodyDef;
    private AbstractDefinition untilDef;
    private AbstractDefinition whileDef;
    private AbstractDefinition fromDef;

    public RepeatDefinition definition(AbstractDefinition definition) {
        this.bodyDef = definition;
        return this;
    }

    public RepeatDefinition until(AbstractDefinition until) {
        if (whileDef != null) {
            throw new IllegalStateException("WHILE already set");
        }
        this.untilDef = until;
        return this;
    }

    public RepeatDefinition setWhile(AbstractDefinition whileDef) {
        if (untilDef != null) {
            throw new IllegalStateException("UNTIL already set");
        }
        this.whileDef = whileDef;
        return this;
    }

    public RepeatDefinition from(AbstractDefinition entry) {
        this.fromDef = entry;
        return this;
    }

    @Override
    public AbstractExpression buildExpression() {
        return new Expr();
    }

    private class Expr extends AbstractExpression {

        public Stage stage;
        public boolean firstIterationDone = false;

        AbstractExpression nextStage() throws ParserException {
            AbstractDefinition def = null;

            while (def == null) {
                if (stage == null) {
                    stage = Stage.FROM;
                } else {
                    stage = stage.next();
                }
                switch (stage) {
                    case FROM:
                        def = fromDef;
                        break;
                    case CHECK:
                        def = untilDef == null ? whileDef : untilDef;
                        break;
                    case BODY:
                        def = bodyDef;
                        break;
                }
            }
            return def.buildExpression();
        }

        @Override
        public void init(Turn turn) throws ParserException {
            turn.push(nextStage());
        }

        @Override
        public void afterCommit(Turn turn) throws ParserException {
            switch (stage) {
                case FROM:
                case BODY:
                    firstIterationDone = true;
                    turn.push(nextStage());
                    break;
                case CHECK:
                    if (untilDef == null) {
                        turn.push(nextStage());
                    } else if (firstIterationDone) {
                        turn.commit();
                    } else {
                        turn.rollback();
                    }
                    break;
            }
        }

        @Override
        public void afterRollback(Turn turn) throws ParserException {
            switch (stage) {
                case FROM:
                    turn.rollback();
                    break;
                case BODY:
                    if (firstIterationDone && untilDef == null && whileDef == null) {
                        turn.commit();
                    } else {
                        turn.rollback();
                    }
                    break;
                case CHECK:
                    if (whileDef == null) {
                        turn.push(nextStage());
                    } else if (firstIterationDone) {
                        turn.commit();
                    } else {
                        turn.rollback();
                    }
                    break;
            }
        }

        @Override
        public AbstractDefinition getDefinition() {
            return RepeatDefinition.this;
        }
    }
}
