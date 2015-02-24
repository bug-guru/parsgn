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
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class MainView extends VBox {

    public static final String HIGHLIGHT = "highlight";
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
    @FXML
    private Slider indexSlider;
    @FXML
    private Label sourceRowLabel;
    @FXML
    private Label sourceColLabel;

    private ProcessDebugger debugger;

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
        navigateFirstButton.disableProperty().bind(Bindings.not(debugger.hasPreviousProperty()));
        navigatePreviousButton.disableProperty().bind(Bindings.not(debugger.hasPreviousProperty()));
        navigateNextButton.disableProperty().bind(Bindings.not(debugger.hasNextProperty()));
        navigateLastButton.disableProperty().bind(debugger.isLastFrameProperty());
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
        indexSlider.valueProperty().bindBidirectional(debugger.indexProperty());
        indexSlider.maxProperty().bind(debugger.lastIndexProperty());
        expressionStack.getSelectionModel().selectFirst();
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
                    if (last.compareTo(sc.charEntity.getPosition()) <= 0) {
                        sc.highlight(false);
                    } else if (first.compareTo(sc.charEntity.getPosition()) <= 0) {
                        sc.highlight(true);
                    } else {
                        sc.highlight(false);
                    }
                });
    }

    private List<Text> createFlow() {
        Position nextPos = new Position(1, 1);
        List<CharEntity> source = debugger.getContent();
        List<Text> flow = new ArrayList<>(source.size());
        int prevRow = 1;
        for (CharEntity ce : source) {
            if (ce.getPosition().getRow() > prevRow) {
                flow.add(new Text("\n"));
                prevRow = ce.getPosition().getRow();
            }
            flow.add(new SourceChar(ce));
        }
        return flow;
    }

    @FXML
    private void navigatePrevious(ActionEvent e) {
        debugger.previous();
    }

    @FXML
    private void navigateNext(ActionEvent e) {
        debugger.next();
    }

    @FXML
    private void navigateFirst(ActionEvent e) {
        debugger.first();
    }

    @FXML
    private void navigateLast(ActionEvent e) {
        debugger.last();
    }


    private class SourceChar extends Text {
        private CharEntity charEntity;
        private Set<String> styles = new HashSet<>();

        public SourceChar(CharEntity charEntity) {
            super(charEntity.toString());
            this.charEntity = charEntity;
            if (charEntity.toString().length() > 1) {
                styles.add("specChar");
            }
            styles.add("char");
            this.setOnMouseEntered(e -> {
                sourceRowLabel.setText(String.valueOf(charEntity.getPosition().getRow()));
                sourceColLabel.setText(String.valueOf(charEntity.getPosition().getCol()));
            });
            this.setOnMouseClicked(e -> {
                debugger.moveTo(charEntity.getPosition());
            });
            updateStyles();
        }

        public void highlight(boolean highlight) {
            if (highlight) {
                styles.add(HIGHLIGHT);
            } else {
                styles.remove(HIGHLIGHT);
            }
            updateStyles();
        }

        private void updateStyles() {
            getStyleClass().setAll(styles);
        }
    }
}
