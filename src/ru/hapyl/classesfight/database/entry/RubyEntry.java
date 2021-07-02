package ru.hapyl.classesfight.database.entry;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.database.Database;

public class RubyEntry extends DatabaseEntry {

    public RubyEntry(Database database) {
        super(database);
    }

    public void grantRubies(long howMuch, @Nullable String reason) {
        addRubies(howMuch);
        if (reason != null) {
            Chat.sendMessage(this.database.getPlayer(), "&c&lRUBY! &e+%s &e%s (%s)", howMuch, howMuch == 1 ? "Ruby" : "Rubies", reason);
        }
    }

    public boolean hasRubies(long atLeast) {
        return this.getRubies() >= atLeast;
    }

    public long getRubies() {
        return this.database.getYaml().getLong("rubies", 0L);
    }

    public void setRubies(long toSet) {
        this.database.getYaml().set("rubies", toSet);
    }

    public void addRubies(long toAdd) {
        setRubies(getRubies() + toAdd);
    }

    public void removeRubies(int toRemove) {
        this.setRubies(getRubies() - toRemove);
    }

}
