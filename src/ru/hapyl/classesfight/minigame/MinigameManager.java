package ru.hapyl.classesfight.minigame;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.annotations.Singleton;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class MinigameManager {

    private static MinigameManager classInstance;

    private final Map<UUID, AbstractMinigame> currentMinigame;

    public MinigameManager(JavaPlugin plugin) {
        if (classInstance != null) {
            throw new IllegalStateException("Minigame Manager already created!");
        }
        classInstance = this;
        this.currentMinigame = new ConcurrentHashMap<>();

        // register it here, less code in Main
        final MinigameRunnableListener minigameRunnableListener = new MinigameRunnableListener();
        plugin.getServer().getPluginManager().registerEvents(minigameRunnableListener, plugin);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, minigameRunnableListener, 0L, 1L);
    }

    public void startGame(IMinigame gameClass, Player player, boolean debug) {
        gameClass.newInstance(player, debug);
    }

    public void setAbstractMinigame(AbstractMinigame minigame) {
        // kick from the last minigame
        final UUID uniqueId = minigame.getPlayer().getUniqueId();
        if (this.currentMinigame.containsKey(uniqueId)) {
            removeAbstractMinigame(minigame);
        }
        this.currentMinigame.put(uniqueId, minigame);
    }

    public void removeAbstractMinigame(AbstractMinigame abstractMinigame) {
        final UUID uniqueId = abstractMinigame.getPlayer().getUniqueId();
        // used to stop runnable
        abstractMinigame.setValid(false);
        // stack overflow
        abstractMinigame.getChest().removeCloseEvent();
        abstractMinigame.getPlayer().closeInventory();
        this.currentMinigame.get(uniqueId).onExit();
        this.currentMinigame.remove(uniqueId);
    }

    @Nullable
    public AbstractMinigame getAbstractMinigame(Player player) {
        return currentMinigame.getOrDefault(player.getUniqueId(), null);
    }

    public Map<UUID, AbstractMinigame> getRunningMinigames() {
        return currentMinigame;
    }

    public static MinigameManager current() {
        if (classInstance == null) {
            throw new IllegalStateException("Minigame manager haven't been created yet!");
        }
        return classInstance;
    }
}
