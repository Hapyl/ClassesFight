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

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.hapyl.classesfight.feature.BlockLocation;
import ru.hapyl.classesfight.feature.PiggyBooster;
import ru.hapyl.classesfight.utils.Validator;

public class VectorCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.isOp()) return true;

            if (cmd.getName().equalsIgnoreCase("vector")) {

                if (args.length == 3) {

                    double[] vec = new double[3];

                    for (int i = 0; i < vec.length; i++) {
                        final double arg0 = Validator.getDouble(args[i]);
                        if (arg0 == -999D) {
                            player.sendMessage("invalid value at " + i);
                            return true;
                        }
                        vec[i] = arg0;
                    }

                    final Vector vector = new Vector(vec[0], vec[1], vec[2]);
                    final BlockLocation blockLoc = new BlockLocation(player.getLocation());

                    String click = blockLoc.getX() + ", " + blockLoc.getY() + ", " + blockLoc.getZ() + ", " + vec[0] + ", " + vec[1] + ", " + vec[2];

                    final BaseComponent[] baseComponents = new ComponentBuilder(ChatColor.GREEN + "Launching piggy with vector " + vector)
                            .event(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, click))
                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, click)).create();

                    player.spigot().sendMessage(ChatMessageType.CHAT, baseComponents);
                    new PiggyBooster(blockLoc, vector).spawn(true);

                    return true;
                }

                player.sendMessage("not enough arguments");

            }
        } else {
            sender.sendMessage("You must be a player to use this.");
        }
        return true;
    }

}