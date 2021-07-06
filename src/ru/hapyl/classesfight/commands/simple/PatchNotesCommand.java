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
import kz.hapyl.spigotutils.module.command.SimplePlayerCommand;
import kz.hapyl.spigotutils.module.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;
import ru.hapyl.classesfight.utils.pn.gui.PatchNotesGUI;

import java.util.ArrayList;
import java.util.List;

public class PatchNotesCommand extends SimplePlayerCommand {

	public PatchNotesCommand(String str) {
		super(str);
	}

	@Override
	protected void execute(Player player, String[] strings) {
		if (strings.length == 0) {
			Chat.sendMessage(player, "&cProvide version to see patch notes.");
			return;
		}

		final double version = Validate.getDouble(strings[0]);
		final PatchNotes patchNotes = PatchNotes.getByVersion(version);

		if (patchNotes == null) {
			Chat.sendMessage(player, "&cCouldn't find patch notes for version \"%s\"!", version);
			return;
		}

		new PatchNotesGUI(player, patchNotes);

	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		final List<String> strings = new ArrayList<>();
		for (final Double ver : PatchNotes.byVersion.keySet()) {
			strings.add(ver + "");
		}
		return super.completerSort(strings, args);
	}

}