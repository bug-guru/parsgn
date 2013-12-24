package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private final Expression rootExpr;
    private final Rule root;
    private List<Path> alternatives = new LinkedList<>();

    public Parser(Rule root) {
        this.root = root;
        this.rootExpr = ExpressionBuilder.ref(root);
        init();
    }

    private void init() {
        Expression.ExpressionChecker checker = rootExpr.checker(0);
        List<Expression.ExpressionChecker> altExpr = checker.getAlternatives();
        generatePaths(altExpr);
    }

    private void generatePaths(List<Expression.ExpressionChecker> altExpr) {
        for (Expression.ExpressionChecker checker : altExpr) {
            Path path = new Path(checker);
            alternatives.add(path);
        }
        enrichAlternatives();
    }

    private void enrichAlternatives() {
        // TODO this method is a subject for optimisation
        List<Path> tmp = new ArrayList<>();
        do {
            tmp.clear();
            Iterator<Path> paths = alternatives.iterator();
            while (paths.hasNext()) {
                Path p = paths.next();
                Expression.ExpressionChecker leaf = p.leaf();
                List<Expression.ExpressionChecker> altExpr = leaf.getAlternatives();
                if (altExpr == null || altExpr.isEmpty()) {
                    continue;
                }
                paths.remove();
                for (Expression.ExpressionChecker checker : altExpr) {
                    Path newPath = new Path(p, checker);
                    tmp.add(newPath);
                }
            }
            alternatives.addAll(tmp);
        } while (!tmp.isEmpty());
    }


    public void parse(String input) throws ParserException {
        final int length = input.length();
        int codePoint;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(offset);
            push(codePoint);
        }
    }

    private void push(int codePoint) throws ExpressionCheckerException {
        Iterator<Path> paths = alternatives.iterator();
        while (paths.hasNext()) {
            Path p = paths.next();
            p.push(codePoint);
        }
    }
}
