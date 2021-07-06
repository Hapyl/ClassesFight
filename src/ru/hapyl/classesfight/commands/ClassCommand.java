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
import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.menu.clazz.ClassRoleSelectGUI;
import ru.hapyl.classesfight.utils.Validator;

import java.util.ArrayList;
import java.util.List;

public class ClassCommand implements CommandExecutor, TabCompleter {

	public static final String theFormat = ChatColor.GREEN + ChatColor.BOLD.toString() + "♦ " + ChatColor.DARK_GREEN;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (cmd.getName().equalsIgnoreCase("class")) {

				if (GameManager.current().isGameInProgress()) {
					Chat.sendMessage(player, "&cUnable to change class during a game!");
					return true;
				}

				if (args.length == 1) {

					if (args[0].equalsIgnoreCase("reset")) {
						player.sendMessage(theFormat + (ClassManager.resetClass(player) ?
								ChatColor.DARK_GREEN + "Your class has been reset." :
								ChatColor.RED + "You don't have class selected!"));
						return true;
					}

					final ClassManager classValue = Validator.getEnumValue(ClassManager.class, args[0]);

					if (classValue == null) {
						sender.sendMessage(
								ChatColor.RED + "Cannot find class " + args[0] + ". Available classes: \n" + ClassManager.getValidClasses());
						return true;
					}

					if (ClassManager.getClass(player) != null && ClassManager.getClass(player) == classValue) {
						player.sendMessage(theFormat + ChatColor.RED + "You have already chosen that class.");
						return true;
					}

					if (!classValue.isUnlocked(player)) {
						Chat.sendMessage(player, theFormat + "&cYou have not yet unlocked this class!");
						PlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 1.0f);
						return true;
					}

					ClassManager.setClass(player, classValue, true);
					return true;

				}
				else {
					new ClassRoleSelectGUI(player);
				}

			}

		}
		else {
			sender.sendMessage("You must be a player to use this.");
			return true;
		}

		return true;
	}

	private List<String> filter(List<String> current, String[] args) {
		List<String> a = new ArrayList<>();
		for (String s : current) {
			if (s.startsWith(args[args.length - 1].toLowerCase()))
				a.add(s);
		}
		return a;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {

		List<String> list = new ArrayList<>();
		list.add("reset");
		for (ClassManager v : ClassManager.values()) {
			list.add(v.name().toLowerCase());
		}

		if (cmd.getName().equalsIgnoreCase("class")) {
			if (args.length == 1) {
				return filter(list, args);
			}
		}
		return null;
	}
}
