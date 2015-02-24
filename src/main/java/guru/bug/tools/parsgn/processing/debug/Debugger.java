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

package guru.bug.tools.parsgn.processing.debug;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.ebnf.EBNFParser;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.processing.CodePoint;
import guru.bug.tools.parsgn.processing.CodePointSource;
import guru.bug.tools.parsgn.processing.Position;
import guru.bug.tools.parsgn.utils.FileUtils;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class Debugger {
    private final List<DebugFrame> history = new ArrayList<>(4096);
    private Parser parser;
    private boolean updateRequired = true;
    private boolean eof = false;
    private List<CodePoint> rules;
    private List<CodePoint> source;
    private int index = 0;
    private int lastIndex = 0;
    private DebugFrame currentFrame = null;
    private boolean hasPrevious = false;
    private boolean hasNext = false;
    private boolean isLastFrame = true;
    private DebugInjectionImpl debugInjection;


    public Debugger(String rulesFileName, String sourceFileName) throws IOException {
        initParser(rulesFileName);
        initSource(sourceFileName);
    }

    public void close() {
        synchronized (history) {
            try {
                while (debugInjection.isAlive()) {
                    debugInjection.interrupt();
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                // nothing dramatic if sleep was interrupted.
            }
        }
    }

    public void goToFirstFrame() {
        synchronized (history) {
            index = 0;
            updateState();
        }
    }

    public void goToLastFrame() {
        synchronized (history) {
            index = history.size() - (eof ? 1 : 2);
            updateState();
        }
    }

    public void goToNextFrame() {
        // TODO: something wrong when it comes to EOF (endless loop?)
        synchronized (history) {
            while (true) {
                if (index < history.size() - 1) {
                    index++;
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

    public void goToPreviousFrame() {
        synchronized (history) {
            if (index == 0) {
                throw new NoSuchElementException();
            }
            index--;
            updateState();
        }
    }

    public void goToPosition(Position target) {
        synchronized (history) {
            Position current = currentFrame.getCurrentPosition();
            int rel = target.compareTo(current);
            if (rel > 0) {
                forward(target);
            } else if (rel < 0) {
                rewind(target);
            }
        }
    }

    private void rewind(Position target) {
        // TODO: not very precise - goes to char left to clicked
        while (index > 0 && currentFrame.getCurrentPosition().compareTo(target) > 0) {
            goToPreviousFrame();
        }
    }

    private void forward(Position target) {
        // TODO: not very precise - goes to char left to clicked
        while (index < history.size() && currentFrame.getCurrentPosition().compareTo(target) < 0) {
            goToNextFrame();
        }
    }

    public List<CodePoint> getRules() {
        synchronized (history) {
            return rules;
        }
    }

    public List<CodePoint> getSource() {
        synchronized (history) {
            return source;
        }
    }

    public boolean hasNext() {
        synchronized (history) {
            return hasNext;
        }
    }

    public boolean hasPrevious() {
        synchronized (history) {
            return hasPrevious;
        }
    }

    public boolean isLastFrame() {
        synchronized (history) {
            return isLastFrame;
        }
    }

    public int getIndex() {
        synchronized (history) {
            return index;
        }
    }

    public void setIndex(int index) {
        synchronized (history) {
            this.index = index;
            updateState();
        }
    }

    public DebugFrame getCurrentFrame() {
        return currentFrame;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    private void initParser(String rulesFileName) throws IOException {
        String rulesTxt =
                rulesFileName == null
                        ? readEBNFRulesContent()
                        : FileUtils.readFileContent(rulesFileName);
        rules = createContent(rulesTxt);
        DefaultParserBuilder builder = new DefaultParserBuilder();
        parser = builder.createParser(new StringReader(rulesTxt));
    }

    private String readEBNFRulesContent() throws IOException {
        try (InputStream is = EBNFParser.class.getResourceAsStream("config.rules")) {
            return FileUtils.readContent(is);
        }
    }

    private void initSource(String sourceFileName) throws IOException {
        synchronized (history) {
            String txt = FileUtils.readFileContent(sourceFileName);
            source = createContent(txt);
            debugInjection = new DebugInjectionImpl(txt);
            tryMore();
            updateState();
        }
    }

    private List<CodePoint> createContent(String source) throws IOException {
        ArrayList<CodePoint> result = new ArrayList<>(source.length() + 1);
        CodePointSource src = new CodePointSource(new StringReader(source));
        CodePoint cp;
        do {
            cp = src.getNextCodePoint();
            result.add(cp);
        } while (cp.getCodePoint() != -1);
        result.trimToSize();
        return Collections.unmodifiableList(result);
    }

    private void updateState() {
        lastIndex = history.size() - (eof ? 1 : 2);
        currentFrame = index >= history.size() ? null : history.get(index);
        hasPrevious = index > 0;
        if (index < history.size() - 1) {
            hasNext = true;
        } else {
            tryMore();
            hasNext = index < history.size() - 1;
        }
        lastIndex = lastIndex < 0 ? 0 : lastIndex;
        isLastFrame = index >= lastIndex;
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

    private class DebugInjectionImpl extends Thread implements DebugInjection {
        final Reader reader;

        public DebugInjectionImpl(String txt) throws IOException {
            reader = new StringReader(txt);
            setDaemon(true);
            start();
            startSync();
        }

        private void startSync() {
            try {
                while (updateRequired) {
                    history.wait();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                ParseTreeResultBuilder resultBuilder = new ParseTreeResultBuilder();
                parser.parse(reader, resultBuilder, this);
            } catch (IOException ex) {
                throw new ParsingException(ex);
            } catch (StopEvent e) {
                // nothing. StopEvent indicating, that Debugger is closed
            }
        }

        @Override
        public void afterCheck(DebugFrame frame) {
            synchronized (history) {
                try {
                    history.add(frame);
                    updateRequired = false;
                    history.notify();
                    while (!updateRequired) {
                        history.wait();
                    }
                } catch (InterruptedException e) {
                    throw new StopEvent();
                }
            }
        }

        @Override
        public void onStart() {
            synchronized (history) {
                updateRequired = false;
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

    private class StopEvent extends RuntimeException {
    }
}
