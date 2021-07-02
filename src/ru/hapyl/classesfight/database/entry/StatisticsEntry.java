package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;

import java.text.DecimalFormat;

public class StatisticsEntry extends DatabaseEntry {

    public StatisticsEntry(Database database) {
        super(database);
    }

    public int getStat(StatType type) {
        return this.database.getYaml().getInt(type.getPath(), 0);
    }

    public void setStat(StatType type, int toSet) {
        this.database.getYaml().set(type.getPath(), toSet);
    }

    public void addStat(StatType type, int toAdd) {
        setStat(type, getStat(type) + toAdd);
    }

    public String getKda() {
        return new DecimalFormat("##.##")
                .format(Math.min((double)this.getStat(StatType.KILLS) / this.getStat(StatType.DEATHS), this.getStat(StatType.KILLS)));
    }

    private static final String PATH_CLASS_KILLS = "stat.class.%s.kills";
    private static final String PATH_CLASS_DEATHS = "stat.class.%s.deaths";
    private static final String PATH_CLASS_WINS = "stat.class.%s.wins";

    // kills
    public int getClassKills(ClassManager clazz) {
        return this.database.getYaml().getInt(String.format(PATH_CLASS_KILLS, enumToJson(clazz)), 0);
    }

    public void setClassKills(ClassManager clazz, int kills) {
        this.database.getYaml().set(String.format(PATH_CLASS_KILLS, enumToJson(clazz)), kills);
    }

    public void addClassKills(ClassManager clazz, int kills) {
        setClassKills(clazz, getClassKills(clazz) + kills);
    }

    // deaths
    public int getClassDeaths(ClassManager clazz) {
        return this.database.getYaml().getInt(String.format(PATH_CLASS_DEATHS, enumToJson(clazz)), 0);
    }

    public void setClassDeaths(ClassManager clazz, int deaths) {
        this.database.getYaml().set(String.format(PATH_CLASS_DEATHS, enumToJson(clazz)), deaths);
    }

    public void addClassDeaths(ClassManager clazz, int deaths) {
        setClassDeaths(clazz, getClassDeaths(clazz) + deaths);
    }


    // wins
    public int getClassWins(ClassManager clazz) {
        return this.database.getYaml().getInt(String.format(PATH_CLASS_WINS, enumToJson(clazz)), 0);
    }

    public void setClassWins(ClassManager clazz, int wins) {
        this.database.getYaml().set(String.format(PATH_CLASS_WINS, enumToJson(clazz)), wins);
    }

    public void addClassWins(ClassManager clazz, int wins) {
        setClassWins(clazz, getClassWins(clazz) + wins);
    }

}
