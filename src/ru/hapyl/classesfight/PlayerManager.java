/*
 * ClassesFight, a Minecraft plugin.
 * Copyright (C) 2021 hapyl
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see https://www.gnu.org/licenses/.
 */

package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;

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
