package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

import java.util.ArrayList;
import java.util.List;

public class BoardRow {
    private final List<Figure> columns = new ArrayList<>();

    public BoardRow() {
        for (int i = 0; i < 8; i++) {
            columns.add(new None());
        }
    }
    public List<Figure> getColumns() {
        return columns;
    }
    public void setFigure(int column, Figure figure) {
        this.columns.set(column, figure);
    }
}
