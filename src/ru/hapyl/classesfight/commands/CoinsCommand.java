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
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.CoinsEntry;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CoinsCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("coins")) {
            if (sender.isOp()) {
                if (args.length >= 2) {

                    String operation = args[0].toLowerCase();
                    Player target = null;

                    try {
                        target = Bukkit.getPlayer(args[1]);
                    } catch (Exception ex) {
                        sender.sendMessage(ChatColor.RED + "That player is not online!");
                        return true;
                    }

                    if (target == null) {
                        sender.sendMessage(ChatColor.RED + "That player is not online!");
                        return true;
                    }

                    final CoinsEntry entry = Database.getDatabase(target).getCoinsEntry();

                    if (operation.equalsIgnoreCase("get")) {
                        sender.sendMessage(ChatColor.GREEN + target.getName() + " has " + entry.getCoins() + " coins.");
                        return true;
                    }

                    if (args.length == 3) {

                        Database.sendDiscontinuedCommandInfo(sender);
                        int value = 0;

                        try {
                            value = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ex) {
                            sender.sendMessage(ChatColor.RED + "Value must be an integer.");
                            return true;
                        }

                        if (operation.equalsIgnoreCase("set")) {
                            entry.setCoins(value);
                            sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s coins to " + value);
                        }
                        else if (operation.equalsIgnoreCase("add")) {
                            entry.addCoins(value);
                            if (value < 0) {
                                sender.sendMessage(ChatColor.GREEN + "Removed " + String.valueOf(value).replace("-", "") + " coins from " + target.getName());
                                Chat.sendMessage(target, "&6An admin removed %s coins from your account!!", value);
                            }
                            else {
                                sender.sendMessage(ChatColor.GREEN + "Added " + value + " coins for " + target.getName());
                                Chat.sendMessage(target, "&6You received %s coins from an admin!", value);
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
            else sender.sendMessage(ChatColor.RED + "Not enough permissions.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (command.getName().equalsIgnoreCase("coins")) {
            switch (strings.length) {
                case 1: {
                    return Chat.tabCompleterSort(Arrays.asList("set", "add", "get"), strings);
                }
                case 2: {
                    return null;
                }
                case 3: {
                    return Collections.emptyList();
                }
            }
            return Collections.emptyList();
        }
        return null;
    }
}
