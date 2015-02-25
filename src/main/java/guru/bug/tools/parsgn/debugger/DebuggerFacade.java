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

import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.processing.CodePoint;
import guru.bug.tools.parsgn.processing.Position;
import guru.bug.tools.parsgn.processing.debug.DebugFrame;
import guru.bug.tools.parsgn.processing.debug.Debugger;
import guru.bug.tools.parsgn.processing.debug.StackElement;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.stream.Collectors;

public class DebuggerFacade {
    private final ReadOnlyBooleanWrapper hasPrevious = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper hasNext = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper isLastFrame = new ReadOnlyBooleanWrapper(true);
    private final ReadOnlyObjectWrapper<DebugFrame> currentFrame = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyListWrapper<CodePointText> source = new ReadOnlyListWrapper<>();
    private final ReadOnlyListWrapper<CodePointText> rules = new ReadOnlyListWrapper<>();
    private final ReadOnlyIntegerWrapper lastIndex = new ReadOnlyIntegerWrapper(0);
    private final SimpleIntegerProperty index = new SimpleIntegerProperty(0);
    private final SimpleObjectProperty<Debugger> debugger = new SimpleObjectProperty<>();
    private final SimpleObjectProperty<StackElement> highlighted = new SimpleObjectProperty<>();
    private final ReadOnlyObjectWrapper<Position> hoveredSourcePosition = new ReadOnlyObjectWrapper<>();

    public DebuggerFacade() {
        index.addListener((o, ov, nv) -> {
            Debugger deb = debugger.get();
            if (deb != null) {
                deb.setIndex(nv == null ? 0 : nv.intValue());
                updateState();
            }
        });
        debugger.addListener((o, ov, nv) -> resetDebugger(ov, nv));
        highlighted.addListener((o, ov, nv) -> highlight(nv));
    }

    public void goToFirstFrame() {
        debugger.get().goToFirstFrame();
        updateState();
    }

    public void goToLastFrame() {
        debugger.get().goToLastFrame();
        updateState();
    }

    public void goToNextFrame() {
        debugger.get().goToNextFrame();
        updateState();
    }

    public void goToPreviousFrame() {
        debugger.get().goToPreviousFrame();
        updateState();
    }

    private void resetDebugger(Debugger ov, Debugger nv) {
        if (ov != null) {
            ov.close();
        }
        if (nv == null) {
            source.set(FXCollections.emptyObservableList());
            rules.set(FXCollections.emptyObservableList());
        } else {
            source.set(convertText(nv.getSource(), this::handleGoToPosition, this::handleShowPosition));
            rules.set(convertText(nv.getRules(), null, null));
        }
        updateState();
    }

    private void handleShowPosition(MouseEvent mouseEvent) {
        CodePointText src = (CodePointText) mouseEvent.getSource();
        Position pos = src.getPosition();
        hoveredSourcePosition.set(pos);
    }

    private void handleGoToPosition(MouseEvent e) {
        CodePointText src = (CodePointText) e.getSource();
        Position pos = src.getPosition();
        debugger.get().goToPosition(pos);
        updateState();
    }

    private ObservableList<CodePointText> convertText(List<CodePoint> points, EventHandler<? super MouseEvent> onMouseClick, EventHandler<? super MouseEvent> onMouseEnter) {
        List<CodePointText> tmp = points.stream()
                .map(CodePointText::new)
                .peek(e -> e.setOnMouseClicked(onMouseClick))
                .peek(e -> e.setOnMouseEntered(onMouseEnter))
                .collect(Collectors.toList());
        return FXCollections.unmodifiableObservableList(FXCollections.observableList(tmp));
    }

    private void updateState() {
        Debugger deb = debugger.get();
        if (deb == null) {
            index.set(0);
            lastIndex.set(0);
            currentFrame.set(null);
            isLastFrame.set(true);
            hasNext.set(false);
            hasPrevious.set(false);
        } else {
            lastIndex.set(deb.getLastIndex());
            index.set(deb.getIndex());
            currentFrame.set(deb.getCurrentFrame());
            isLastFrame.set(deb.isLastFrame());
            hasNext.set(deb.hasNext());
            hasPrevious.set(deb.hasPrevious());
        }
    }

    private void highlight(StackElement element) {
        if (element == null) {
            getSource().stream().forEach(e -> e.highlight(false));
            getRules().stream().forEach(e -> e.highlight(false));
            return;
        }
        Position tmp = element.getStartPosition();
        Position first = tmp == null ? new Position(1, 1) : tmp;
        Position last = currentFrame.get().getCurrentPosition();
        highlight(getSource(), first, last);

        Expression.ExpressionChecker checker = element.getChecker();
        Expression expression = checker == null ? null : element.getChecker().getExpression();
        Position ruleStart = expression == null ? null : expression.getStartPosition();
        Position ruleEnd = expression == null ? null : expression.getEndPosition();
        highlight(getRules(), ruleStart, ruleEnd);
    }

    private void highlight(ObservableList<CodePointText> src, Position startPosition, Position endPosition) {
        src.stream()
                .forEach(sc -> {
                    if (startPosition == null || endPosition == null || endPosition.compareTo(sc.getPosition()) <= 0) {
                        sc.highlight(false);
                    } else if (startPosition.compareTo(sc.getPosition()) <= 0) {
                        sc.highlight(true);
                    } else {
                        sc.highlight(false);
                    }
                });
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

    public DebugFrame getCurrentFrame() {
        return currentFrame.get();
    }

    public ReadOnlyObjectProperty<DebugFrame> currentFrameProperty() {
        return currentFrame.getReadOnlyProperty();
    }

    public ObservableList<CodePointText> getSource() {
        return source.get();
    }

    public ReadOnlyListProperty<CodePointText> sourceProperty() {
        return source.getReadOnlyProperty();
    }

    public ObservableList<CodePointText> getRules() {
        return rules.get();
    }

    public ReadOnlyListProperty<CodePointText> rulesProperty() {
        return rules.getReadOnlyProperty();
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

    public Debugger getDebugger() {
        return debugger.get();
    }

    public SimpleObjectProperty<Debugger> debuggerProperty() {
        return debugger;
    }

    public void setDebugger(Debugger debugger) {
        this.debugger.set(debugger);
    }

    public StackElement getHighlighted() {
        return highlighted.get();
    }

    public SimpleObjectProperty<StackElement> highlightedProperty() {
        return highlighted;
    }

    public void setHighlighted(StackElement highlighted) {
        this.highlighted.set(highlighted);
    }

    public Position getHoveredSourcePosition() {
        return hoveredSourcePosition.get();
    }

    public ReadOnlyObjectProperty<Position> hoveredSourcePositionProperty() {
        return hoveredSourcePosition;
    }
}
