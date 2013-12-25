package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private final Expression rootExpr;
    private final ExpressionNode rootExprNode;
    private final Rule root;

    public Parser(Rule root) {
        this.root = root;
        this.rootExpr = ExpressionBuilder.ref(root);
        this.rootExprNode = rootExpr.createNode();
    }

    public void parse(String input) throws ParserException {
        SourceEvent event = new SourceEvent(SourceEventType.INIT, 0);
        rootExprNode.push(event);
        final int length = input.length();
        int codePoint;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(offset);
            event = new SourceEvent(SourceEventType.PROCESS, offset, codePoint);
            push(event);
        }
    }

    private void push(SourceEvent event) throws ExpressionCheckerException {
        ResultEvent result = rootExprNode.push(event);
    }
}
