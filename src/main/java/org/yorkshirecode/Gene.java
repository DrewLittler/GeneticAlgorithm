package org.yorkshirecode;

public enum Gene {

    START_RED(0),
    START_GREEN(1),
    START_BLUE(2),
    COLUMNS(3),
    DELTA_HUE(4),
    DELTA_SATURATION(5),
    DELTA_BRIGHTNESS(6);

    private int value;

    Gene(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
