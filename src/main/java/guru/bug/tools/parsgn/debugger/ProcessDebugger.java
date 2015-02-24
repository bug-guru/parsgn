/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.debugger;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.ebnf.EBNFParser;
import guru.bug.tools.parsgn.processing.Position;
import guru.bug.tools.parsgn.processing.debug.Debugger;
import guru.bug.tools.parsgn.processing.debug.State;
import guru.bug.tools.parsgn.utils.FileUtils;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;
import javafx.collections.FXCollections;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ProcessDebugger {
    private final List<State> history = new ArrayList<>(4096);
    private Parser parser;
    private boolean updateRequired = false;
    private boolean started = false;
    private boolean eof = false;
    private int idx = 0;
    private int lastIdx = 0;
    private State curStt = null;


    public ProcessDebugger(String rulesFileName, String sourceFileName) throws IOException {
        setupParser(rulesFileName);
        init(sourceFileName);
    }

    private void init(String sourceFileName) throws IOException {
        synchronized (history) {
            String txt = FileUtils.readFileContent(sourceFileName);
            createContent(txt);
            Reader reader = new StringReader(txt);
            ParseTreeResultBuilder resultBuilder = new ParseTreeResultBuilder();
            Thread t = new Thread(() -> {
                try {
                    parser.parse(reader, resultBuilder, new DebuggerImpl());
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
            t.setDaemon(true);
            t.start();
            try {
                while (!started) {
                    history.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tryMore();
            updateState();
        }
    }

    private void createContent(String source) {
        Position nextPos = new Position(1, 1);
        ArrayList<CharEntity> flow = new ArrayList<>(2048);
        boolean crlfSuspect = false;
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            if (crlfSuspect) {
                crlfSuspect = false;
                if (ch == '\n') {
                    flow.add(new CharEntity('\r', nextPos));
                    nextPos = Position.newCol(nextPos);
                    flow.add(new CharEntity('\n', nextPos));
                    nextPos = Position.newRow(nextPos);
                    continue;
                } else {
                    flow.add(new CharEntity('\r', nextPos));
                    nextPos = Position.newRow(nextPos);
                }
            }
            if (ch == '\r') {
                crlfSuspect = true;
                continue;
            }
            flow.add(new CharEntity(ch, nextPos));
            if (ch == '\n') {
                nextPos = Position.newRow(nextPos);
            } else {
                nextPos = Position.newCol(nextPos);
            }
        }
        flow.trimToSize();
        this.source.set(FXCollections.unmodifiableObservableList(FXCollections.observableList(flow)));
    }

    private void updateState() {
        lastIdx = history.size() - (eof ? 1 : 2);
        curStt = idx >= history.size() ? null : history.get(idx);
    }

    private void tryMore() {
        try {
            updateRequired = true;
            history.notify();
            while (updateRequired) {
                history.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void first() {
        synchronized (history) {
            idx = 0;
            updateState();
        }
    }

    public void last() {
        synchronized (history) {
            idx = history.size() - (eof ? 1 : 2);
            updateState();
        }
    }

    public void next() {
    // TODO: something wrong when it comes to EOF (endless loop)
        synchronized (history) {
            while (true) {
                if (idx < history.size() - 1) {
                    idx++;
                    updateState();
                    return;
                } else if (eof) {
                    throw new NoSuchElementException();
                } else {
                    tryMore();
                }
            }
        }
    }

    public void previous() {
        synchronized (history) {
            if (idx == 0) {
                throw new NoSuchElementException();
            }
            idx--;
            updateState();
        }
    }

    public void moveTo(Position target) {
        synchronized (history) {
            bulkUpdate = true;
            Position current = currentState.get().getCurrentPosition();
            int rel = target.compareTo(current);
            if (rel > 0) {
                forward(target);
            } else if (rel < 0) {
                rewind(target);
            }
            bulkUpdate = false;
            updateState();
        }
    }

    private void rewind(Position target) {
        while (idx > 0 && curStt.getCurrentPosition().compareTo(target) > 0) {
            previous();
        }
    }

    private void forward(Position target) {
        while (idx < history.size() && curStt.getCurrentPosition().compareTo(target) < 0) {
            next();
        }
        updateState();
    }

    private void addState(State state) {
        synchronized (history) {
            try {
                history.add(state);
                updateRequired = false;
                history.notify();
                while (!updateRequired) {
                    history.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setupParser(String rulesFileName) throws IOException {
        Parser parser;
        String rules;
        if (rulesFileName == null) {
            rules = readEBNFRulesContent();
        } else {
            rules = readContent(rulesFileName);
            DefaultParserBuilder builder = new DefaultParserBuilder();
            parser = builder.createParser(new FileReader(new File(rulesFileName)));
        }
        this.parser = parser;
    }

    private String readEBNFRulesContent() throws IOException {
        try (InputStream is = EBNFParser.class.getResourceAsStream("config.rules")) {

        }
    }


    private class DebuggerImpl implements Debugger {

        @Override
        public void afterCheck(State state) {
            addState(state);
        }

        @Override
        public void onStart() {
            synchronized (history) {
                started = true;
                history.notify();
            }
        }

        @Override
        public void onFinish() {
            synchronized (history) {
                eof = true;
                updateRequired = false;
                history.notify();
            }
        }

    }
}
