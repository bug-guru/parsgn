package net.developithecus.parser;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class QuantifierExpressionTest {
    private QuantifierExpression expression;
    private MockExpression mock;
    private Expression.ExpressionChecker checker;

    @Before
    public void init() {
        mock = new MockExpression();
        expression = new QuantifierExpression().expression(mock);
        checker = expression.checker();
        assertNotNull(checker);
    }

    @Test
    public void testOneRequired_Mismatch() throws ExpressionCheckerException {
        mock.result(Result.MISMATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
    }

    @Test
    public void testOneRequired_Match() throws ExpressionCheckerException {
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneRequired_MoreAndMatch() throws ExpressionCheckerException {
        mock.result(Result.MORE);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneRequired_MoreAndMismatch() throws ExpressionCheckerException {
        mock.result(Result.MORE);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        mock.result(Result.MISMATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(0, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneOptional_Mismatch() throws ExpressionCheckerException {
        expression.minRepeats(0);
        mock.result(Result.MISMATCH);
        assertTrue(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
    }

    @Test
    public void testOneOptional_Match() throws ExpressionCheckerException {
        expression.minRepeats(0);
        mock.result(Result.MATCH);
        assertTrue(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneOptional_MoreAndMatch() throws ExpressionCheckerException {
        expression.minRepeats(0);
        mock.result(Result.MORE);
        assertTrue(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertTrue(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        mock.result(Result.MATCH);
        assertTrue(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneOptional_MoreAndMismatch() throws ExpressionCheckerException {
        expression.minRepeats(0);
        mock.result(Result.MORE);
        assertTrue(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertTrue(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        mock.result(Result.MISMATCH);
        assertTrue(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(0, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneOrTwo_Mismatch() throws ExpressionCheckerException {
        expression.maxRepeats(2);
        mock.result(Result.MISMATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
    }

    @Test
    public void testOneOrTwo_MatchMatch() throws ExpressionCheckerException {
        expression.maxRepeats(2);
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());

        assertTrue(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(2, checker.getNode().getChildren().size());
    }

    @Test
    public void testOneOrTwo_MatchMismatch() throws ExpressionCheckerException {
        expression.maxRepeats(2);
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());

        mock.result(Result.MISMATCH);
        assertTrue(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test
    public void testTwo_Mismatch() throws ExpressionCheckerException {
        expression.minRepeats(2).maxRepeats(2);
        mock.result(Result.MISMATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
    }

    @Test
    public void testTwo_MatchMatch() throws ExpressionCheckerException {
        expression.minRepeats(2).maxRepeats(2);
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());

        assertFalse(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(2, checker.getNode().getChildren().size());
    }

    @Test
    public void testTwo_MatchMismatch() throws ExpressionCheckerException {
        expression.minRepeats(2).maxRepeats(2);
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());

        mock.result(Result.MISMATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MISMATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());
    }

    @Test(expected = FlowCompleteException.class)
    public void testTwo_MatchMatchFlowComplete() throws ExpressionCheckerException {
        expression.minRepeats(2).maxRepeats(2);
        mock.result(Result.MATCH);
        assertFalse(checker.isOptional());
        assertSame(Result.MORE, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(1, checker.getNode().getChildren().size());

        assertFalse(checker.isOptional());
        assertSame(Result.MATCH, checker.check(0));
        assertNotNull(checker.getNode().getChildren());
        assertEquals(2, checker.getNode().getChildren().size());

        assertSame(Result.MATCH, checker.check(0));
    }
}
