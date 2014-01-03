package net.developithecus.parser.expr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author dima
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        QuantityExpressionOneOrMoreTest.class,
        QuantityExpressionTwoOrMoreTest.class,
        QuantityExpressionZeroOrMoreTest.class,
        QuantityExpressionZeroOrOneTest.class,
        QuantityExpressionNoMore2TimesTest.class,
        QuantityExpressionAtLeast2ButNotMoreThan4TimesTest.class,
        QuantityExpressionExactly1TimeTest.class,
        QuantityExpressionExactly2TimesTest.class,
        QuantityExpressionExactly3TimesTest.class
})
public class QuantityExpressionTestSuite {
}
