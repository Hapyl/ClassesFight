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
import ru.hapyl.classesfight.test.StringFormatter;

import java.util.List;

public class TestStringFormatCommand extends SimpleAdminCommand {

	public TestStringFormatCommand(String str) {
		super(str);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		final Player player = (Player)sender;

		if (args.length == 0) {
			Chat.sendMessage(player, "&cPlease provide a valid string!");
			return;
		}

		final StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}

		final String stringToFormat = builder.toString().trim();
		Chat.sendMessage(player, "&a&lString Formatter:");
		Chat.sendMessage(player, new StringFormatter(stringToFormat).format());

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return super.tabComplete(sender, args);
	}

}