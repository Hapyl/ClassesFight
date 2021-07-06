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
import kz.hapyl.spigotutils.module.command.SimpleCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.quest.relic.Relic;
import ru.hapyl.classesfight.quest.relic.RelicHunt;

import java.util.Arrays;
import java.util.List;

public class RelicCommand extends SimpleCommand {

	public RelicCommand(String str) {
		super(str);
		this.setAllowOnlyOp(true);
		this.setAllowOnlyPlayer(true);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		Player player = (Player)sender;

		if (args.length == 3) {

			final String value = args[0].toLowerCase();
			final Player target = Bukkit.getPlayer(args[1]);
			final int id = Validate.getInt(args[2]);
			final Relic relic = RelicHunt.current().getById(id);

			if (target == null) {
				Chat.sendMessage(player, "&cThis player is not online!");
				return;
			}

			if (relic == null) {
				Chat.sendMessage(player, "&cCannot find Relic with Id %s!", id);
				return;
			}

			switch (value) {

				case "grant": {
					if (relic.hasFound(target)) {
						Chat.sendMessage(player, "&c%s has already found this Relic!", target.getName());
						return;
					}
					relic.grant(target, false);
					Chat.sendMessage(player, "&aGranted Relic with %s to %s.", id, target.getName());
					return;
				}

				case "revoke": {
					if (!relic.hasFound(target)) {
						Chat.sendMessage(player, "&c%s hasn't found this Relic!", target.getName());
						return;
					}
					relic.revoke(target);
					Chat.sendMessage(player, "&aRevoked Relic with Id %s from %s.", id, target.getName());
					return;
				}

				case "list": {
					final List<Integer> foundRelics = Database.getDatabase(player).getCollectibleEntry().getFoundRelics();
					Chat.sendMessage(player, "&a%s found Relics with these Ids %s", target.getName(), foundRelics);
					return;
				}

				default: {
					Chat.sendMessage(player, "&cInvalid usage!");
					return;
				}

			}

		}

		Chat.sendMessage(player, "&cUsage: /relic grant/revoke (Player) (int:Id)");

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return super.completerSort(Arrays.asList("grant", "revoke", "list"), args);
		}
		return null;
	}

}