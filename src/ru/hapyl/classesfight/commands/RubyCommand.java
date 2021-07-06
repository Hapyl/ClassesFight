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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.RubyEntry;

public class RubyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;

			if (!player.isOp()) {
				Chat.sendMessage(player, "&cYou must be OP to use this!");
				return true;
			}

			if (cmd.getName().equalsIgnoreCase("rubies")) {
				if (args.length >= 2) {
					String operation = args[0].toLowerCase();
					Player target = Bukkit.getPlayer(args[1]);

					if (target == null) {
						sender.sendMessage(ChatColor.RED + "That player is not online!");
						return true;
					}

					final RubyEntry entry = Database.getDatabase(target).getRubyEntry();

					if (operation.equalsIgnoreCase("get")) {
						Chat.sendMessage(player, "%s has %s rubies.", target.getName(), entry.getRubies());
						return true;
					}

					if (args.length == 3) {

						int value = 0;

						try {
							value = Integer.parseInt(args[2]);
						} catch (NumberFormatException ex) {
							sender.sendMessage(ChatColor.RED + "Value must be an integer.");
							return true;
						}

						if (operation.equalsIgnoreCase("set")) {
							entry.setRubies(value);
							Chat.sendMessage(player, "&aSet %s's rubies to %s.", target.getName(), value);
						}
						else if (operation.equalsIgnoreCase("add")) {
							entry.addRubies(value);
							if (value < 0) {
								Chat.sendMessage(player, "&aRemoved %s rubies from %s.", String.valueOf(value).replace("-", ""), target.getName());
								Chat.sendMessage(target, "&6An admin removed %s rubies from your account!!", value);
							}
							else {
								Chat.sendMessage(player, "&aAdded %s rubies to %s.", value, target.getName());
								Chat.sendMessage(target, "&6You received %s rubies from an admin!", value);
							}
						}
						else {
							sender.sendMessage(ChatColor.RED + "Incorrect Usage. " + cmd.getUsage());
							return true;
						}

					}
					else {
						sender.sendMessage(ChatColor.RED + "Not enough arguments. Usage: " + cmd.getUsage());
					}


				}
				else {
					sender.sendMessage(ChatColor.RED + "Not enough arguments. Usage: " + cmd.getUsage());
				}
			}
		}
		else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

}