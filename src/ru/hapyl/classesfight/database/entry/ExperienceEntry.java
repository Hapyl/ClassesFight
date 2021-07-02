package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

public class ExperienceEntry extends DatabaseEntry {
    public ExperienceEntry(Database database) {
        super(database);
    }

    public int getLvl() {
        return this.database.getYaml().getInt("experience.lvl");
    }

    public void setLvl(int lvl) {
        lvl = Math.max(0, lvl);
        this.database.getYaml().set("experience.lvl", lvl);
    }

    public long getExp() {
        return this.database.getYaml().getLong("experience.exp");
    }

    public void setExp(long toSet) {
        toSet = Math.max(0, toSet);
        this.database.getYaml().set("experience.exp", toSet);
    }

    public void addExp(long toAdd) {
        setExp(getExp() + toAdd);
    }

}
