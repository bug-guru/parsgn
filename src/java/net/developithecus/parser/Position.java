/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class Position implements Cloneable {
    private int index;
    private int column = 1;
    private int line = 1;
    private boolean isCrRead;
    private boolean isLfRead;

    @Override
    protected Position clone() {
        try {
            return (Position) super.clone();
        } catch (CloneNotSupportedException ex) {
            assert false : ex;
            return null;
        }
    }

    private void nextLine() {
        column = 1;
        line++;
    }

    public void next(char ch) {
        index++;
        boolean lf = ch == '\n';

        if (ch == '\r') {
            if (isCrRead || isLfRead) {
                nextLine();
            }
            isCrRead = true;
            isLfRead = false;
        } else if (ch == '\n') {
            if (isLfRead) {
                nextLine();
            }
            isCrRead = false;
            isLfRead = true;
        } else {
            if (isCrRead || isLfRead) {
                nextLine();
            }
            isCrRead = false;
            isLfRead = false;
            column++;
        }
    }

    public int getColumn() {
        return column;
    }

    public int getIndex() {
        return index;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }

}
