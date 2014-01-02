package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public interface ExpressionChecker {
    public Expression next();

    public CheckResult check(int codePoint, ResultType prevResult) throws ParsingException;
}
