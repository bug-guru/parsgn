package net.developithecus.parser;

import net.developithecus.parser.expr.ReferenceExpression;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    public static final int INITIAL_LOG_CAPACITY = 2048;
    private final ReferenceExpression root;

    public Parser(Rule root) {
        this.root = new ReferenceExpression();
        this.root.setReference(root);
    }

    public Node parse(InputStream input) throws ParsingException, IOException {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(input))
        ) {
            List<ParsingEntry> log = new ArrayList<>(INITIAL_LOG_CAPACITY);
            ParsingContext ctx = new ParsingContext(root);
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
                        if (ctx.getResultTree() != null) {
                            return ctx.getResultTree();
                        }
                    } while (ctx.getNextIndex() < log.size());
                }
                if (line == null) {
                    break;
                }
            }
            throw new ParsingException("Parsing error");
        }
    }

}
