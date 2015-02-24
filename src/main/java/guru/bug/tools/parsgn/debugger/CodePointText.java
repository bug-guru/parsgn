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

import guru.bug.tools.parsgn.processing.CodePoint;
import guru.bug.tools.parsgn.processing.Position;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CodePointText extends Text {
    private CodePoint codePoint;
    private Set<String> styles = new HashSet<>();

    public CodePointText(CodePoint codePoint) {
        if (codePoint.isNewLine()) {
            setText(codePoint.toString() + "\n");
        } else {
            setText(codePoint.toString());
        }
        this.codePoint = codePoint;
        if (codePoint.toString().length() > 1) {
            styles.add("specChar");
        }
        styles.add("char");
        updateStyles();
    }

    public void highlight(boolean highlight) {
        if (highlight) {
            styles.add(MainView.HIGHLIGHT);
        } else {
            styles.remove(MainView.HIGHLIGHT);
        }
        updateStyles();
    }

    private void updateStyles() {
        getStyleClass().setAll(styles);
    }

    public Position getPosition() {
        return codePoint.getPosition();
    }
}
