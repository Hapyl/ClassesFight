package ru.hapyl.classesfight.database.entry;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.database.Database;

public class CoinsEntry extends DatabaseEntry {

    public CoinsEntry(Database database) {
        super(database);
    }

    public void grantCoins(long amount, boolean checkForBooster, @Nullable String reason) {
        final boolean hasBooster = this.database.getBoosterEntry().hasBooster(1);
        final long finalAmount = checkForBooster ? (hasBooster ? amount * 2 : amount) : amount;
        addCoins(amount);
        if (reason != null) {
            Chat.sendMessage(this.database.getPlayer(), "&6&lCOINS! &e+%s &eCoins %s&7(%s)", finalAmount, Chat.format(hasBooster ? "&e&lBOOSTED " : ""), reason);
        }
    }

    public long getCoins() {
        return this.database.getYaml().getLong("coins", 0L);
    }

    public void setCoins(long toSet) {
        this.database.getYaml().set("coins", toSet);
    }

    public void addCoins(long toAdd) {
        setCoins(getCoins() + toAdd);
    }

    public void removeCoins(long toRemove) {
        setCoins(getCoins() - toRemove);
    }

    public boolean hasCoins(long atLeast) {
        final long coins = Math.max(0L, getCoins());
        return coins >= atLeast;
    }

}
