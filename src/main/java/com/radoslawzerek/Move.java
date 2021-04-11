package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

public class Move {
    private final transient int currentColumn;
    private final transient int currentRow;
    private final transient int targetColumn;
    private final transient int targetRow;


    public Move(int currentColumn, int currentRow, int targetColumn, int targetRow) {
        this.currentColumn = currentColumn;
        this.currentRow = currentRow;
        this.targetColumn = targetColumn;
        this.targetRow = targetRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getTargetColumn() {
        return targetColumn;
    }

    public int getTargetRow() {
        return targetRow;
    }
}

