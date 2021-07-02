package ru.hapyl.classesfight.database.entry;

import ru.hapyl.classesfight.database.Database;

public class SettingEntry extends DatabaseEntry {
    public SettingEntry(Database database) {
        super(database);
    }

    public boolean isEnabled(Setting setting) {
        return this.database.getYaml().getBoolean("setting." + enumToJson(setting), setting.isEnabledByDefault());
    }

    public void setEnabled(Setting setting, boolean bool) {
        this.database.getYaml().set("setting." + enumToJson(setting), bool);
    }


}
