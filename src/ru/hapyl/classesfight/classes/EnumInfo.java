package ru.hapyl.classesfight.classes;

public enum EnumInfo {

    // none is 20 to prevent
    NONE(-2, 20),
    VERY_LOW(-1, 20),
    LOW(0, 30),
    MEDIUM(1, 40),
    HIGH(2, 50),
    VERY_HIGH(3, 60);

    private final int index;
    private final double value;

    public int index() {
        return this.index;
    }

    EnumInfo(int index) {
        this(index, 0.0d);
    }

    EnumInfo(int index, double value) {
        this.index = index;
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
