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

package ru.hapyl.classesfight.commands;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.PlayerManager;

public class BuildCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			Player player = (Player)commandSender;
			if (command.getName().equalsIgnoreCase("build")) {
				if (commandSender.isOp()) {

					final PlayerManager manager = PlayerManager.current();

					final boolean hasBuildMode = manager.hasBuildMode(player);
					manager.setBuildMode(player, !hasBuildMode);

					if (manager.hasBuildMode(player)) {
						Chat.sendMessage(player, "&6Build mode enabled, you may now build.");
						if (player.getGameMode() != GameMode.CREATIVE) {
							Chat.sendMessage(player, "&6Changed your gamemode to creative.");
							player.setGameMode(GameMode.CREATIVE);
						}
					}
					else {
						Chat.sendMessage(player, "&6Build mode disabled, you can no longer build.");
					}

					player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, SoundCategory.RECORDS, 5, 1.25f);

				}
				else {
					commandSender.sendMessage(ChatColor.RED + "You don't have permissions to do this.");
				}
			}

			return true;
		}
		return true;
	}
}
