package ru.hapyl.classesfight.database;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DatabaseListener implements Listener {

    @EventHandler
    public void handlePlayerJoin(PlayerJoinEvent ev) {
        Database.createInstance(ev.getPlayer());
    }

    @EventHandler()
    public void handlePlayerQuit(PlayerQuitEvent ev) {
        Database.saveData(ev.getPlayer());
//        Database.getDatabase(ev.getPlayer()).saveToFile();
    }

}