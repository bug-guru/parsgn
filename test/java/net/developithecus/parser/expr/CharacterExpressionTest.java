package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class CharacterExpressionTest {

    @Test
    public void testChecker_digits() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.DIGIT);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check('a'));
        assertSame(ResultType.ROLLBACK, checker.check(-1));
        assertSame(ResultType.ROLLBACK, checker.check('\n'));
        assertSame(ResultType.ROLLBACK, checker.check('S'));
        assertSame(ResultType.COMMIT, checker.check('1'));
        assertSame(ResultType.COMMIT, checker.check('2'));
        assertSame(ResultType.COMMIT, checker.check('3'));
        assertSame(ResultType.COMMIT, checker.check('0'));
    }

    @Test
    public void testChecker_eof() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.EOF);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check('a'));
        assertSame(ResultType.COMMIT, checker.check(-1));
    }

    @Test
    public void testChecker_eol() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.LINE_SEPARATOR);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check(' '));
        assertSame(ResultType.COMMIT, checker.check('\n'));
        assertSame(ResultType.COMMIT, checker.check('\r'));
    }
}
