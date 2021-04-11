package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

public class Pawn implements Figure {
    boolean checked = false;

    private final PawnColor color;

    public Pawn(PawnColor color) {
        this.color = color;
    }

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
        return getColorSymbol() + "P";
    }

    private String getColorSymbol() {
        return (color == PawnColor.WHITE) ? "w" : "b";
    }
}
