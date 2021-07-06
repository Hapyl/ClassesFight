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
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.StatType;
import ru.hapyl.classesfight.database.entry.StatisticsEntry;
import ru.hapyl.classesfight.utils.Validator;

import java.util.Arrays;
import java.util.List;

public class StatCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player)sender;

            if (!player.isOp()) {
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("Stat")) {

                // stat <player> <stat> <set/get> <i>

                if (args.length >= 3) {

                    Player target = Bukkit.getPlayer(args[0]);
                    StatType stat = Validator.getEnumValue(StatType.class, args[1]);
                    boolean set = args[2].equalsIgnoreCase("set");
                    int value = 0;

                    if (args.length == 4) {
                        value = Validator.getInt(args[3]);
                    }

                    if (stat == null || target == null) {
                        player.sendMessage("Invalid stat or player");
                        return true;
                    }

                    if (value == -999) {
                        player.sendMessage("Invalid integer");
                        return true;
                    }

                    final StatisticsEntry entry = Database.getDatabase(target).getStatistics();

                    if (set) {
                        if (args.length != 4) {
                            player.sendMessage("Cannot set stat without integer value");
                            return true;
                        }

                        entry.setStat(stat, value);
                        Chat.sendMessage(player, "&aSet %s's %s stat to %s", target.getName(), stat.name(), value);
                        return true;
                    }

                    Chat.sendMessage(player, "&a%s's %s has value of %s", target.getName(), stat.name(), entry.getStat(stat));
                }


            }
        }
        else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("stat")) {

            if (args.length == 2) {
                return Chat.tabCompleterSort(Chat.arrayToList(StatType.values()), args);
            }

            if (args.length == 3) {
                return Arrays.asList("set", "get");
            }

        }
        return null;
    }
}