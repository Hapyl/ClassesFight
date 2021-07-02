package ru.hapyl.classesfight.classes;

public enum ClassRating {

    SS("&c&lSS"),
    S("&2&lS"),
    A("&a&lA"),
    B("&e&lB"),
    C("&6&lC"),
    D("&4&lD"),

    NOT_YET("&8Not yet rated...");

    private final String string;

    ClassRating(String string) {
        this.string = string;
    }

    public String getTier() {
        return string;
    }
}
