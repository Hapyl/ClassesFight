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

package ru.hapyl.classesfight.commands.simple;

import kz.hapyl.spigotutils.module.chat.Chat;
import kz.hapyl.spigotutils.module.command.SimpleAdminCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.feature.record.Record;

import java.util.List;
import java.util.Locale;

public class RecordCommand extends SimpleAdminCommand {

	public RecordCommand(String str) {
		super(str);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		final Player player = (Player)sender;

		if (args.length == 1) {

			final Record record = Record.getRecord(player);
			switch (args[0].toLowerCase(Locale.ROOT)) {

				case "start": {
					new Record(player);
					break;
				}

				case "stop": {
					if (record == null) {
						Chat.sendMessage(player, "&cNo record to stop!");
						return;
					}
					record.setFinished(true);
					Chat.sendMessage(player, "&aFinished Recording! (%s frames total). &e/record play &ato play it.", record.getMaxFrame());
					break;
				}

				case "play": {
					if (record == null) {
						Chat.sendMessage(player, "&cNo record to play!");
						return;
					}
					if (record.isPlaying()) {
						Chat.sendMessage(player, "&aStopping record...");
						record.stopPlayingRecord();
						return;
					}
					Chat.sendMessage(player, "&aPlaying record...");
					record.playReplay();
					break;
				}

			}

		}

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}