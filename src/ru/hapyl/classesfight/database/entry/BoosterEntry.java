package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

public class BoosterEntry extends DatabaseEntry {
    public BoosterEntry(Database database) {
        super(database);
    }

    public boolean hasBooster(int atLeast) {
        return getBoosters() >= atLeast;
    }

    public int getBoosters() {
        return this.database.getYaml().getInt("boosters", 0);
    }

    public void addBooster(int amount) {
        setBoosters(getBoosters() + 1);
    }

    public void removeBooster(int amount) {
        setBoosters(getBoosters() - 1);
    }

    public void setBoosters(int amount) {
        this.database.getYaml().set("boosters", amount);
    }

}
