package ru.hapyl.classesfight.stats;

public enum ClassEntryInfo {

    PLAYED("Total Times Played"),
    KILLS("Total Kills"),
    DEATHS("Total Deaths"),
    WINS("Total Wins");

    private final String holoPrefix;

    ClassEntryInfo(String holoPrefix) {
        this.holoPrefix = holoPrefix;
    }

    public String getPrefix() {
        return holoPrefix;
    }
}
