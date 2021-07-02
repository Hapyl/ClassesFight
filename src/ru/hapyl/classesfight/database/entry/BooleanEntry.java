package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

public class BooleanEntry extends DatabaseEntry {
    public BooleanEntry(Database database) {
        super(database);
    }

    public void setBoolean(String path, boolean bool) {
        this.database.getYaml().set(path, bool);
    }

    public boolean getBoolean(String path, boolean def) {
        return this.database.getYaml().getBoolean(path, def);
    }

}
