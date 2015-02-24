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

import guru.bug.tools.parsgn.processing.ResultType;
import guru.bug.tools.parsgn.processing.debug.DebugFrame;
import guru.bug.tools.parsgn.processing.debug.Debugger;
import guru.bug.tools.parsgn.processing.debug.StackElement;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.io.IOException;

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

    private DebuggerFacade debugger = new DebuggerFacade();

    public MainView(Debugger debugger) {
        this.debugger.setDebugger(debugger);
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
                bind(debugger.currentFrameProperty());
            }

            @Override
            protected ObservableList<StackElement> computeValue() {
                DebugFrame current = debugger.getCurrentFrame();
                if (current == null) {
                    return null;
                }
                return FXCollections.observableList(current.getStack());
            }
        });
        debugger.debuggerProperty().addListener((o, ov, nv) -> updateTexts(nv));
        debugger.highlightedProperty().bind(expressionStack.getSelectionModel().selectedItemProperty());
        expressionStack.itemsProperty().addListener((o, ov, nv) -> expressionStack.getSelectionModel().selectFirst());
        resultLabel.textProperty().bind(new StringBinding() {
            {
                bind(debugger.currentFrameProperty());
            }

            @Override
            protected String computeValue() {
                DebugFrame frame = debugger.getCurrentFrame();
                ResultType result = frame == null ? null : frame.getResult();
                return result == null ? null : result.toString();
            }
        });
        indexSlider.valueProperty().bindBidirectional(debugger.indexProperty());
        indexSlider.maxProperty().bind(debugger.lastIndexProperty());
        expressionStack.getSelectionModel().selectFirst();
        updateTexts(debugger.getDebugger());
    }

    private void updateTexts(Debugger nv) {
        if (nv == null) {
            sourceText.getChildren().clear();
            rulesText.getChildren().clear();
        } else {
            sourceText.getChildren().setAll(debugger.getSource());
            rulesText.getChildren().setAll(debugger.getRules());
        }
    }

    @FXML
    private void navigatePrevious(ActionEvent e) {
        debugger.goToPreviousFrame();
    }

    @FXML
    private void navigateNext(ActionEvent e) {
        debugger.goToNextFrame();
    }

    @FXML
    private void navigateFirst(ActionEvent e) {
        debugger.goToFirstFrame();
    }

    @FXML
    private void navigateLast(ActionEvent e) {
        debugger.goToLastFrame();
    }


}
