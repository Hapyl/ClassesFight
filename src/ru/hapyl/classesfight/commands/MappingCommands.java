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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.database.entry.Setting;

public class MappingCommands implements CommandExecutor {

	private final Plugin plugin = ClassesFight.getPlugin();
	private final FileConfiguration config = plugin.getConfig();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;
			switch (cmd.getName().toLowerCase()) {

				case "start":
					String arg = "";
					if (args.length >= 1) {
						arg = args[0].toLowerCase();
					}

					if (Database.getDatabase(player).getSettingEntry().isEnabled(Setting.SPECTATE)) {
						Chat.sendMessage(player, "&cSpectators cannot start the game.");
						return true;
					}

					player.sendMessage(ChatColor.GREEN + "Starting the game..");
					GameManager.current().startGame(arg.equalsIgnoreCase("-d") || arg.equalsIgnoreCase("-u"));
					if (arg.equalsIgnoreCase("-u")) {
						GameManager.current().getPlayers().forEach(gamer -> {
							ClassUltimate.chargeUltimate(player);
							player.sendMessage(ChatColor.GREEN + "Charged your ultimate and set creative.");
							player.setGameMode(GameMode.CREATIVE);
						});
					}
					break;

				case "stop":
					player.sendMessage(ChatColor.GREEN + "Stopping the game..");
					GameManager.current().stopGame();
					break;

			}
			return true;
		}
		sender.sendMessage("You must be a player to use this.");
		return true;
	}

	public boolean contains(String map) {
		if (map.contains(".") || config.getConfigurationSection("maps") == null) return false;
		return config.getConfigurationSection("maps").getValues(true).containsKey(map);
	}
}
