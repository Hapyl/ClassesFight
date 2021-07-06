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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;

public class WinCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player player) {
			if (cmd.getName().equalsIgnoreCase("testwinfx")) {
				if (player.isOp()) {
					final GameManager current = GameManager.current();
					if (current.isGameInProgress()) {
						current.spawnFireworks(player.getLocation(), false);
					}
					else {
						Chat.sendMessage(player, "&cThe game must be in progress to use this.");
					}
				}
				else {
					Chat.sendMessage(player, "&cYou must be OP to use this.");
				}
			}
		}
		else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

}