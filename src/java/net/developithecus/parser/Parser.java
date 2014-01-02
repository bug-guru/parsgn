package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;
import net.developithecus.parser.exceptions.UnexpectedEOFException;
import net.developithecus.parser.expr.ReferenceExpression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private static final int INITIAL_LOG_CAPACITY = 2048;
    private final ReferenceExpression root;

    public Parser(Rule root) {
        this.root = new ReferenceExpression();
        this.root.setReference(root);
    }

    public void parse(InputStream input, NodeTreeVisitor visitor) throws ParsingException, IOException {
        NodeResultBuilder builder = new NodeResultBuilder();
        parse(input, builder);
        Node root = builder.getRoot();
        class NodeHolder {
            final Node node;
            final Iterator<Node> iterator;

            NodeHolder(Node node) {
                this.node = node;
                this.iterator = node.getChildren().iterator();
            }
        }
        Deque<NodeHolder> stack = new LinkedList<>();
        if (root.isLeaf()) {
            visitor.leafNode(root);
        } else {
            visitor.startNode(root);
            stack.push(new NodeHolder(root));
        }
        while (!stack.isEmpty()) {
            NodeHolder current = stack.peek();
            if (current.iterator.hasNext()) {
                Node node = current.iterator.next();
                if (node.isLeaf()) {
                    visitor.leafNode(node);
                } else {
                    visitor.startNode(node);
                    stack.push(new NodeHolder(node));
                }
            } else {
                stack.pop();
                visitor.endNode(current.node);
            }
        }
    }

    public <T> void parse(InputStream input, ResultBuilder<T> builder) throws ParsingException, IOException {
        long t1 = System.nanoTime();
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            List<ParsingEntry> log = new ArrayList<>(INITIAL_LOG_CAPACITY);
            ParsingContext<T> ctx = new ParsingContext<>(root, builder);
            int row = 0;
            while (true) {
                row++;
                String line = reader.readLine();
                int length = line == null ? 0 : line.length();
                int col = 0;
                for (int offset = 0; offset <= length; ) {
                    col++;
                    Position pos = new Position(row, col);
                    int codePoint;
                    if (line == null) {
                        codePoint = -1;
                        offset++;
                    } else if (offset == length) {
                        codePoint = '\n';
                        offset++;
                    } else {
                        codePoint = line.codePointAt(offset);
                        offset += Character.charCount(codePoint);
                    }
                    log.add(new ParsingEntry(pos, codePoint));
                    do {
                        ParsingEntry entry = log.get(ctx.getNextIndex());
                        ctx.next(entry);
                        if (builder.isFinished()) {
                            return;
                        }
                    } while (ctx.getNextIndex() < log.size());
                }
                if (line == null) {
                    break;
                }
            }
            throw new UnexpectedEOFException();
        } finally {
            long t2 = System.nanoTime();
            System.out.println("parsed in: " + (t2 - t1) / 1000000.0 + " ms");
        }
    }

}
