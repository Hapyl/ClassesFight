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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.ExpGUI;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.utils.Validator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExpCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player playerSender = (Player)sender;

			if (cmd.getName().equalsIgnoreCase("Exp")) {

				if (args.length == 0) {
					new ExpGUI(playerSender);
				}
				else if (!playerSender.isOp()) {
					new ExpGUI(playerSender);
					return true;
				}

				final Experience instance = Experience.getInstance();
				final Experience experience = instance;

				// levelup
				if (args.length >= 2) {

					final Player target = Bukkit.getPlayer(args[1]);

					if (target == null) {
						Chat.sendMessage(playerSender, "&cThis player is not online!");
						return true;
					}

					final String firstArgument = args[0].toLowerCase();

					switch (args.length) {

						/* LEVELUP */
						case 2: {
							switch (firstArgument) {
								case "levelup": {
									Chat.sendMessage(playerSender, "&aLevelling up %s...", target.getName());
									final int nextLvl = experience.getLvl(target) + 1;
									experience.grantExp(target, experience.getExpNeededFor(nextLvl) - experience.getExp(target));
									Chat.sendMessage(target, "&6An admin levelled up you! You are now lvl %s.", nextLvl);
									break;
								}

								case "leveldown": {
									Chat.sendMessage(playerSender, "&aLevelling down %s...", target.getName());
									final int downLvl = Math.max(0, experience.getLvl(target) - 1);
									experience.setLvl(target, downLvl);
									experience.setExp(target, experience.getExpNeededFor(downLvl));
									Chat.sendMessage(target, "&6An admin put you one level down! You are now lvl %s.", downLvl);
									break;
								}

								case "fixrewards": {
									Chat.sendMessage(playerSender, "&aFixing missing rewards for %s...", target.getName());
									experience.fixRewards(target);
									Chat.sendMessage(target, "&6An admin fixed your rewards, you should not have anything missing now!");
									break;
								}
								default: {
									Chat.sendMessage(playerSender, "&cInvalid usage! " + cmd.getUsage());
									break;
								}
							}
							break;
						}

						case 3: {
							if (firstArgument.equals("get")) {
								ExpType type = Validator.getEnumValue(ExpType.class, args[2]);

								if (type == null) {
									Chat.sendMessage(playerSender, "&cInvalid type! Try these:\n " + Arrays.toString(ExpType.values()));
								}

								if (type == ExpType.EXP) {
									final long exp = instance.getExp(target);
									Chat.sendMessage(playerSender, "%s has %s experience points.", target.getName(), exp);
								}
								else {
									final int lvl = instance.getLvl(target);
									Chat.sendMessage(playerSender, "%s has %s experience lvl.", target.getName(), lvl);
								}

							}
							else {
								Chat.sendMessage(playerSender, "&cInvalid usage! " + cmd.getUsage());
							}
							break;
						}

						case 4: {
							if (firstArgument.equals("set") || firstArgument.equals("add")) {

								boolean setValues = firstArgument.equals("set");

								ExpType type = Validator.getEnumValue(ExpType.class, args[2]);
								long amount = NumberConversions.toLong(args[3]);

								if (amount < 0) {
									Chat.sendMessage(playerSender, "Amount cannot be negative.");
									return true;
								}

								if (type == null) {
									Chat.sendMessage(playerSender, "&cInvalid type! Try these:\n " + Arrays.toString(ExpType.values()));
								}

								if (type == ExpType.EXP) {
									if (setValues) {
										Database.getDatabase(target).getExperienceEntry().setExp(amount);
										instance.grantExp(target, amount);
										if (experience.canLevelUp(target)) {
											experience.levelUp(target);
										}
										Chat.sendMessage(playerSender, "&aSet %s's experience points to %s.", target.getName(), amount);
										Chat.sendMessage(target, "&6An admin set your experience points to %s.", amount);
									}
									else {
										experience.grantExp(target, amount);
										// fix missed rewards
										experience.fixRewards(target);
										Chat.sendMessage(playerSender, "&aAdded %s experience points to %s, they now have %s experience points in total.", amount, target.getName(), instance.getExp(target));
										Chat.sendMessage(target, "&6An admin gave you %s experience points, you now have %s in total.", amount, experience.getExp(target));
									}
								}
								else {
									experience.setLvl(target, (int)amount);
									Chat.sendMessage(playerSender, "&aSet %s's experience lvl to %s.", target.getName(), amount);
									Chat.sendMessage(target, "&6An admin set your experience lvl to %s.", amount);
								}
							}
							else {
								Chat.sendMessage(playerSender, "&cInvalid usage! " + cmd.getUsage());
							}
							break;
						}
					}
				}


			}
		}
		else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {

		// exp set (Player) (Exp/Lvl) (Value)
		// exp add (Player) (Exp/Lvl) (Value)
		// exp get (Player) (Exp/Lvl)
		// exp levelup (Player)

		if (command.getName().equalsIgnoreCase("exp")) {
			if (args.length >= 1) {
				if (args.length >= 2) {
					if (args.length >= 3) {
						if (args.length == 4) {
							return Collections.emptyList();
						}
						return Chat.tabCompleterSort(Arrays.asList("exp", "lvl"), args);
					}
					// return players
					return null;
				}
				return Chat.tabCompleterSort(Arrays.asList("set", "add", "get", "levelup", "leveldown", "fixrewards"), args);
			}
		}

		return Collections.emptyList();
	}

	private enum ExpType {
		EXP, LVL
	}

}