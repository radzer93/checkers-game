package com.radoslawzerek;

/**
 * Author: Radosław Żerek
 */

public interface Figure {
    PawnColor getColor();
    boolean getChecked();
    void setChecked(boolean checkedValue);
}
