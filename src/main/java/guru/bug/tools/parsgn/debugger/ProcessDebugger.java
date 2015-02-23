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
import guru.bug.tools.parsgn.ebnf.EBNFParser;
import guru.bug.tools.parsgn.processing.debug.Debugger;
import guru.bug.tools.parsgn.processing.debug.State;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;
import javafx.beans.property.*;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ProcessDebugger {
    private final Parser parser;
    private final List<State> history = new ArrayList<>(4096);
    private final ReadOnlyBooleanWrapper hasPrevious = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper hasNext = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper isLastFrame = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectWrapper<State> currentState = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper content = new ReadOnlyStringWrapper();
    private final SimpleIntegerProperty index = new SimpleIntegerProperty(0);
    private final ReadOnlyIntegerWrapper lastIndex = new ReadOnlyIntegerWrapper(0);
    private boolean updateRequired = false;
    private boolean started = false;
    private boolean eof = false;
    private int idx = 0;


    public ProcessDebugger(Parser parser) {
        this.parser = parser;
        index.addListener((o, ov, nv) -> {
            idx = nv == null ? 0 : nv.intValue();
            updateState();
        });
    }

    public void debug(String sourceFileName) throws IOException {
        synchronized (history) {
            String txt = readContent(sourceFileName);
            Reader reader = new StringReader(txt);
            content.set(txt);
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

    private String readContent(String fileName) throws IOException {
        StringBuilder text = new StringBuilder(2048);
        try (
                InputStream rulesInputStream = fileName == null
                        ? EBNFParser.class.getResourceAsStream("config.rules")
                        : new FileInputStream(new File(fileName));
                Reader rulesReader = new BufferedReader(new InputStreamReader(rulesInputStream))
        ) {
            CharBuffer buf = CharBuffer.allocate(512);
            while (rulesReader.read(buf) != -1) {
                buf.flip();
                text.append(buf.array(), 0, buf.limit());
                buf.clear();
            }
            buf.flip();
            text.append(buf.array(), 0, buf.limit());
        }
        return text.toString();
    }

    private void updateState() {
        int lastIdx = history.size() - (eof ? 1 : 2);
        currentState.set(idx >= history.size() ? null : history.get(idx));
        hasPrevious.set(idx > 0);
        if (idx < history.size() - 1) {
            hasNext.set(true);
        } else {
            tryMore();
            hasNext.set(idx < history.size() - 1);
        }
        lastIndex.set(lastIdx < 0 ? 0 : lastIdx);
        index.set(idx);
        isLastFrame.set(idx >= lastIdx);
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

    public boolean getHasPrevious() {
        return hasPrevious.get();
    }

    public ReadOnlyBooleanProperty hasPreviousProperty() {
        return hasPrevious.getReadOnlyProperty();
    }

    public boolean getHasNext() {
        return hasNext.get();
    }

    public ReadOnlyBooleanProperty hasNextProperty() {
        return hasNext.getReadOnlyProperty();
    }

    public boolean getIsLastFrame() {
        return hasNext.get();
    }

    public ReadOnlyBooleanProperty isLastFrameProperty() {
        return isLastFrame.getReadOnlyProperty();
    }

    public State getCurrentState() {
        return currentState.get();
    }

    public ReadOnlyObjectProperty<State> currentStateProperty() {
        return currentState.getReadOnlyProperty();
    }

    public String getContent() {
        return content.get();
    }

    public ReadOnlyStringProperty contentProperty() {
        return content.getReadOnlyProperty();
    }

    public int getIndex() {
        return index.get();
    }

    public SimpleIntegerProperty indexProperty() {
        return index;
    }

    public void setIndex(int index) {
        this.index.set(index);
    }

    public int getLastIndex() {
        return lastIndex.get();
    }

    public ReadOnlyIntegerProperty lastIndexProperty() {
        return lastIndex.getReadOnlyProperty();
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
