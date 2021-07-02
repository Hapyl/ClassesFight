package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import ru.hapyl.classesfight.utils.GameUtils;

import javax.naming.event.NamingEvent;

public class PlayerManager implements Listener {

    private static final PlayerManager classInstance = new PlayerManager();
    private static final String PLUGIN_TAG = "cfBuildMode";

    public static PlayerManager current() {
        return classInstance;
    }

    public boolean hasBuildMode(Player player) {
        return player.getScoreboardTags().contains(PLUGIN_TAG);
    }

    public boolean canBuild(Player player) {
        return hasBuildMode(player) && player.getGameMode() == GameMode.CREATIVE;
    }

    public void setBuildMode(Player player, boolean mode) {
        if (mode) {
            player.addScoreboardTag(PLUGIN_TAG);
        }
        else {
            if (hasBuildMode(player)) {
                player.getScoreboardTags().remove(PLUGIN_TAG);
            }
        }
    }

    private void sendErrorMessage(Player player) {
        if (hasBuildMode(player)) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                Chat.sendMessage(player, "&cYou must be in creative mode to build!");
            }
        }
        else {
            Chat.sendMessage(player, "&cYou must first enable build mode! &e/build");
        }
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.RECORDS, 5, 1);
    }

    @EventHandler()
    public void handleBlockPlace(BlockPlaceEvent ev) {
        // Auto-Generated
        final Player player = ev.getPlayer();
        if (!canBuild(player)) {
            ev.setCancelled(true);
            if (!GameManager.current().isGameInProgress()) {
                sendErrorMessage(player);
            }
        }
    }

    @EventHandler()
    public void handleBlockBreak(BlockBreakEvent ev) {
        // Auto-Generated
        final Player player = ev.getPlayer();
        if (!canBuild(player)) {
            ev.setCancelled(true);
            if (!GameManager.current().isGameInProgress()) {
                sendErrorMessage(player);
            }
        }
    }

    @EventHandler()
    public void handleUnleash(PlayerUnleashEntityEvent ev) {
        // Auto-Generated
        final Player player = ev.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            ev.setCancelled(true);
        }
    }

}
