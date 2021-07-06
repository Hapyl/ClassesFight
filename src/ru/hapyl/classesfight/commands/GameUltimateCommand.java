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
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.classes.ClassUltimate;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class GameUltimateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player)sender;

			if (!player.isOp()) return true;

			if (cmd.getName().equalsIgnoreCase("ultimate")) {
				if (GameManager.current().isDebugMode()) {

					Chat.sendMessage(player, "&aCharged your ultimate!");
					OldPlayerLib.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 2.0f);
					ClassUltimate.setPoints(player, ClassManager.getClass(player).getUltimate().getPoints());

				} else player.sendMessage("You can only use that in debug mode!");
			}
		} else {
			sender.sendMessage("You must be a player to use this.");
		}
		return true;
	}

}