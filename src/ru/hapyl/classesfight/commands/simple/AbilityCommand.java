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
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.GameManager;
import ru.hapyl.classesfight.ability.Abilities;

import java.util.List;

public class AbilityCommand extends SimpleAdminCommand {

	public AbilityCommand(String str) {
		super(str);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		final Player player = (Player)sender;
		final GameManager current = GameManager.current();

		if (current.isGameInProgress() && !current.isDebugMode()) {
			Chat.sendMessage(player, "&cYou can only use this in debug mode or in lobby!");
			return;
		}

		if (args.length != 1) {
			Chat.sendMessage(player, "&cNot enough arguments!");
			return;
		}

		final Abilities ability = Validate.getEnumValue(Abilities.class, args[0]);

		if (ability == null) {
			Chat.sendMessage(player, "&cInvalid ability, use tab-completer!");
			return;
		}

		Chat.sendMessage(player, "&aUsed %s! (Ignoring Cooldown)", ability.getName());
		ability.useAbility(player);

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return super.completerSort(super.arrayToList(Abilities.values()), args);
		}
		return null;
	}
}