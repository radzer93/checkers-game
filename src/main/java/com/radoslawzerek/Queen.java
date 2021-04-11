package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

public class Queen implements Figure {
    boolean checked = false;

    private final PawnColor color;

    Queen(PawnColor color) {
        this.color = color;
    }

    @Override
    public PawnColor getColor() {
        return color;
    }

    @Override
    public boolean getChecked() {
        return checked;
    }

    @Override
    public void setChecked(boolean checkedValue) {
        this.checked = checkedValue;
    }

    @Override
    public String toString() {
        return getColorSymbol() + "Q";
    }

    private String getColorSymbol() {
        return (color == PawnColor.WHITE) ? "w" : "b";
    }
}
