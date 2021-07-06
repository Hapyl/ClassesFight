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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConsoleCommand implements CommandExecutor {

	private final List<UUID> allowedUUID = new ArrayList<>();

	{
		// hapyl
		allowedUUID.add(UUID.fromString("b58e578c-8e36-4789-af50-1ee7400307c0"));
		allowedUUID.add(UUID.fromString("cf3fbee2-431b-33d9-851d-dae239a1936a"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("console")) {

			if (sender instanceof Player) {

				Player player = (Player) sender;

				if (args.length >= 1) {

					if (allowedUUID.contains(player.getUniqueId())) {

						StringBuilder all = new StringBuilder();

						for (String arg : args) {
							all.append(arg.replace('&', '§')).append(" ");
						}

						player.sendMessage(ChatColor.GREEN + "Made console perform command: " + all);
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), all.toString());

					} else {
						player.sendMessage(ChatColor.RED + "You are not allowed to use this command!");
					}

				} else {
					player.sendMessage(ChatColor.RED + "Not enough arguments. Usage: " + cmd.getUsage());
				}

			}
		}

		return true;
	}


}
