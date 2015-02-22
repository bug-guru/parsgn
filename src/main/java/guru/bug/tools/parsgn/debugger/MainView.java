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

import guru.bug.tools.parsgn.processing.Position;
import guru.bug.tools.parsgn.processing.ResultType;
import guru.bug.tools.parsgn.processing.debug.StackElement;
import guru.bug.tools.parsgn.processing.debug.State;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class MainView extends VBox {

    @FXML
    private TextFlow sourceText;
    @FXML
    private TextFlow rulesText;
    @FXML
    private ListView<StackElement> expressionStack;
    @FXML
    private Button navigateFirstButton;
    @FXML
    private Button navigatePreviousButton;
    @FXML
    private Button navigateNextButton;
    @FXML
    private Button navigateLastButton;
    @FXML
    private Label resultLabel;
    private ProcessDebugger debugger;
    private Font textFont = Font.font("Monospaced");
    private Font specFont = Font.font("Monospaced", textFont.getSize() / 2.0);

    public MainView(ProcessDebugger debugger) {
        this.debugger = debugger;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getClass().getSimpleName() + ".fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void initialize() {
        navigatePreviousButton.disableProperty().bind(Bindings.not(debugger.hasPreviousProperty()));
        navigateNextButton.disableProperty().bind(Bindings.not(debugger.hasNextProperty()));
        navigateFirstButton.setDisable(true);
        navigateLastButton.setDisable(true);
        expressionStack.setCellFactory(lv -> new ListCell<StackElement>() {
            @Override
            protected void updateItem(StackElement item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplay());
                }
            }
        });
        expressionStack.itemsProperty().bind(new ListBinding<StackElement>() {
            {
                bind(debugger.currentStateProperty());
            }

            @Override
            protected ObservableList<StackElement> computeValue() {
                State current = debugger.currentStateProperty().get();
                if (current == null) {
                    return null;
                }
                return FXCollections.observableList(debugger.getCurrentState().getStack());
            }
        });
        List<Text> flow = createFlow();
        sourceText.getChildren().setAll(flow);
        expressionStack.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> highlight(nv));
        expressionStack.itemsProperty().addListener((o, ov, nv) -> expressionStack.getSelectionModel().selectFirst());
        resultLabel.textProperty().bind(new StringBinding() {
            {
                bind(debugger.currentStateProperty());
            }

            @Override
            protected String computeValue() {
                State state = debugger.getCurrentState();
                ResultType result = state == null ? null : state.getResult();
                return result == null ? null : result.toString();
            }
        });
    }

    private void highlight(StackElement element) {
        if (element == null) {
            return;
        }
        Position tmp = element.getStartPosition();
        Position first = tmp == null ? new Position(1, 1) : tmp;
        Position last = debugger.getCurrentState().getCurrentPosition();
        sourceText.getChildren().stream()
                .filter(n -> n instanceof SourceChar)
                .map(n -> (SourceChar) n)
                .forEach(sc -> {
                    if (last.compareTo(sc.position) <= 0) {
                        sc.highlight(false);
                    } else if (first.compareTo(sc.position) <= 0) {
                        sc.highlight(true);
                    } else {
                        sc.highlight(false);
                    }
                });
    }

    private List<Text> createFlow() {
        Position nextPos = new Position(1, 1);
        String source = debugger.getContent();
        List<Text> flow = new ArrayList<>(2048);
        Character prev = null;
        for (int i = 0; i < source.length(); i++) {
            char ch = source.charAt(i);
            Text text;
            if (prev != null && prev == '\r') {
                prev = null;
                if (ch == '\n') {
                    nextPos = createCRLFText(nextPos, flow);
                    continue;
                } else {
                    nextPos = createCRText(nextPos, flow);
                }
            }
            if (ch == '\r') {
                prev = '\r';
                continue;
            }
            if (ch == '\n') {
                nextPos = createLFText(nextPos, flow);
            } else if (ch == '\t') {
                nextPos = createTabText(nextPos, flow);
            } else {
                nextPos = createCharText(nextPos, ch, flow);
            }
        }
        return flow;
    }

    private Position createCharText(Position pos, char ch, List<Text> flow) {
        Text text = new SourceChar(String.valueOf(ch), pos);
        text.setFont(textFont);
        flow.add(text);
        return Position.newCol(pos);
    }

    private Position createTabText(Position pos, List<Text> flow) {
        Text text1 = new SourceChar("\\t       ", pos);
        text1.setFont(specFont);
        flow.add(text1);
        return Position.newCol(pos);
    }

    private Position createLFText(Position pos, List<Text> flow) {
        Text text = new SourceChar("\\n\n", pos);
        text.setFont(specFont);
        flow.add(text);
        return Position.newRow(pos);
    }

    private Position createCRText(Position pos, List<Text> flow) {
        Text text = new SourceChar("\\r\n", pos);
        text.setFont(textFont);
        flow.add(text);
        return Position.newRow(pos);
    }

    private Position createCRLFText(Position pos, List<Text> flow) {
        Text text1 = new SourceChar("\\r", pos);
        text1.setFont(specFont);
        flow.add(text1);
        Text text2 = new SourceChar("\\n\n", Position.newCol(pos));
        text2.setFont(specFont);
        flow.add(text2);
        return Position.newRow(pos);
    }

    @FXML
    private void navigatePrevious(ActionEvent e) {
        debugger.previous();
    }

    @FXML
    private void navigateNext(ActionEvent e) {
        debugger.next();
    }

    private static class SourceChar extends Text {
        private Position position;

        public SourceChar(String text, Position position) {
            super(text);
            this.position = position;
        }

        public void highlight(boolean highlight) {
            if (highlight) {
                setFill(Color.RED);
                setStyle("-fx-underline: true");
            } else {
                setFill(Color.BLACK);
                setStyle("-fx-underline: false");
            }
        }
    }
}
