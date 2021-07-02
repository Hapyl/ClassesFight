package ru.hapyl.classesfight.database.entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.Validator;

public class ClassEntry extends DatabaseEntry {
    public ClassEntry(Database database) {
        super(database);
    }

    public void setClass(ClassManager clazz) {
        this.database.getYaml().set("class", enumToJson(clazz));
    }

    public ClassManager getClazz() {
        final ClassManager enumValue = Validator.getEnumValue(ClassManager.class, this.database.getYaml().getString("class", "hunter"));
        if (enumValue == null) {
            return ClassManager.HUNTER;
        }
        else {
            return enumValue;
        }
    }

    public static void handleClassRejoin() {
        Bukkit.getOnlinePlayers().forEach(ClassEntry::handleClassRejoin);
    }

    public static void handleClassRejoin(Player player) {
        final ClassEntry entry = Database.getDatabase(player).getClassEntry();
        ClassManager.setClass(player, entry.getClazz(), false);
    }

}
