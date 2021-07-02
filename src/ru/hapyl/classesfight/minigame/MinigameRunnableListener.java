package ru.hapyl.classesfight.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class MinigameRunnableListener implements Runnable, Listener {

    private final MinigameManager manager = MinigameManager.current();

    @Override
    public void run() {
        final Map<UUID, AbstractMinigame> hash = MinigameManager.current().getRunningMinigames();
        if (!hash.isEmpty()) {
            hash.forEach((uuid, ref) -> ref.onTick());
        }
    }
    
    @EventHandler()
    public void handlePlayerLeave(PlayerQuitEvent ev) {
        final Player player = ev.getPlayer();
        final AbstractMinigame target = manager.getAbstractMinigame(player);
        if (target != null) {
            target.forceKick(false);
            target.clearTrash();
        }
    }

}
