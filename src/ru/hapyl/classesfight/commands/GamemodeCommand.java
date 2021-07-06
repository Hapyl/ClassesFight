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

import com.google.common.collect.Sets;
import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GamemodeCommand implements CommandExecutor {

	public static final Map<GameMode, Set<String>> values = new HashMap<>();

	static {
		values.put(GameMode.SURVIVAL, Sets.newHashSet("0", "s", "survival"));
		values.put(GameMode.CREATIVE, Sets.newHashSet("1", "c", "creative"));
		values.put(GameMode.ADVENTURE, Sets.newHashSet("2", "a", "adventure"));
		values.put(GameMode.SPECTATOR, Sets.newHashSet("3", "sp", "spectator", "spec"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("gm")) {

				if (args.length < 1) {
					Chat.sendMessage(player, "&cThere must be one argument!");
					return true;
				}

				final GameMode gamemode = getGameMode(args[0]);

				if (gamemode == null) {
					Chat.sendMessage(player, "&cInvalid GameMode.");
					return true;
				}

				player.setGameMode(gamemode);
				Chat.sendMessage(player, "&aChanged your gamemode to %s", Chat.capitalize(gamemode.name()));

			}
		} else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

	private GameMode getGameMode(String arg) {
		for (GameMode mode : values.keySet()) {
			final Set<String> strings = values.get(mode);
			if (strings.contains(arg.toLowerCase())) return mode;
		}
		return null;
	}

}