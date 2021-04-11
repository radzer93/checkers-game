package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

public class None implements Figure {
    boolean checked = false;

    @Override
    public PawnColor getColor() {
        return PawnColor.NONE;
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
        return "  ";
    }
}
