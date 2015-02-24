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

import guru.bug.tools.parsgn.processing.debug.State;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;

public class DebuggerFacade {
    private final ReadOnlyBooleanWrapper hasPrevious = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper hasNext = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanWrapper isLastFrame = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyObjectWrapper<State> currentState = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyListWrapper<CharEntity> source = new ReadOnlyListWrapper<>();
    private final ReadOnlyListWrapper<CharEntity> rules = new ReadOnlyListWrapper<>();
    private final SimpleIntegerProperty index = new SimpleIntegerProperty(0);
    private final ReadOnlyIntegerWrapper lastIndex = new ReadOnlyIntegerWrapper(0);

    public DebuggerFacade() {
        index.addListener((o, ov, nv) -> {
            idx = nv == null ? 0 : nv.intValue();
            updateState();
        });
    }
    private void updateState() {
        currentState.set(curStt);
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

    public ObservableList<CharEntity> getSource() {
        return source.get();
    }

    public ReadOnlyListProperty<CharEntity> sourceProperty() {
        return source.getReadOnlyProperty();
    }

    public ObservableList<CharEntity> getRules() {
        return rules.get();
    }

    public ReadOnlyListProperty<CharEntity> rulesProperty() {
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
}
