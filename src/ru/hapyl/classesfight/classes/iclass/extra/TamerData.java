package ru.hapyl.classesfight.classes.iclass.extra;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TamerData {

    private final Player player;
    private TamerPack pack;

    public TamerData(Player player) {
        this.player = player;
    }

    public void removeMinions() {
        if (this.pack != null) {
            this.pack.recall();
        }
    }

    public void newTamerPack(TamerPacks pack) {
        this.removeMinions();
        this.pack = pack.getPack();
        this.pack.spawn(player);
        Chat.sendMessage(player, "&a☀ You just summoned &e%s&a!", pack.getPack().getName());
    }

    public boolean hasPackAndEntityIsInPack(Entity entity) {
        return this.pack != null && this.pack.isInPack(entity);
    }

    public TamerPack getPack() {
        return pack;
    }

    public Player getPlayer() {
        return player;
    }
}
