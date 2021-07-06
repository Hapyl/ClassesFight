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

package ru.hapyl.classesfight.utils.pn.gui;

import kz.hapyl.spigotutils.module.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

public class PatchNotesGUI extends PatchGUI {

	public PatchNotesGUI(Player player, PatchNotes patch) {
		super(player, patch, "", 3);
	}

	@Override
	protected void updateMenu() {
		final boolean anyClassChanges = !patch.getClassPatches().isEmpty();

		this.setItem(11, new ItemBuilder(Material.BOOK).setName("&aGeneral Changes").addLore("&7Coming Soon™!").toItemStack());
		this.setItem(13, new ItemBuilder(Material.REPEATING_COMMAND_BLOCK).setName("&aSystem Changes").addLore("&7Coming Soon™!").toItemStack());

		this.setItem(15, new ItemBuilder(Material.TOTEM_OF_UNDYING).setName("&aClass Changes")
				.addSmartLore(anyClassChanges
						? String.format("&8There is &l%s &8class changes.", patch.getClassPatches().values().size())
						: "&8There was no class changes.")
				.addLore("")
				.addLore("&7About Icons:")
				.addLore(" " + ICONS.getChangeIcon() + "- General Change")
				.addLore(" " + ICONS.getBuffIcon() + "- Buff")
				.addLore(" " + ICONS.getNerfIcon() + "- Nerf")
				.addLore(" " + ICONS.getBugFixIcon() + "- Bug Fix")
				.addLore().addLore("&eClick to see class changes")
				.toItemStack(), (player) -> new ClassChangesGUI(player, patch));
	}

}
