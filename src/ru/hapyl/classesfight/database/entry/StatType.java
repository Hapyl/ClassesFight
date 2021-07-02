package ru.hapyl.classesfight.database.entry;

public enum StatType {

    KILLS,
    WINS,
    DEATHS,
    ABILITY_USED,
    ULTIMATE_USED,
    TOTAL_TIME_PLAYED;

    public String getPath() {
        return "stat." + DatabaseEntry.enumToJson(this);
    }

}
