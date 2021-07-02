package ru.hapyl.classesfight.classes;

public enum ClassStatus {

    NONE(""),
    NEW("&c&lBRAND NEW!"),
    UPDATED("&a&lUPDATED!"),
    UPDATED_ULTIMATE("&a&lUPDATED ULTIMATE!"),
    REWORKED("&6&lREWORKED!");

    private final String string;

    ClassStatus(String s) {
        this.string = s;
    }

    @Override
    public String toString() {
        return string;
    }

}
