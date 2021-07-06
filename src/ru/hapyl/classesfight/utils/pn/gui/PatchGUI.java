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

import kz.hapyl.spigotutils.module.inventory.gui.PlayerGUI;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.utils.pn.PatchNotes;

public class PatchGUI extends PlayerGUI {


	protected final PatchNotes patch;

	public PatchGUI(Player player, PatchNotes patch, String suffix, int size) {
		super(player, "Patch Notes " + patch.getVersion() + (suffix.isEmpty() ? "" : " " + suffix), size);
		this.patch = patch;
		this.updateMenu();
		this.openInventory();
	}

	protected void updateMenu() {

	}

	protected static final Icon ICONS = new Icon() {
		@Override
		public String getChangeIcon() {
			return "&f&l♻ &7";
		}

		@Override
		public String getBuffIcon() {
			return "&a&l↑ &7";
		}

		@Override
		public String getNerfIcon() {
			return "&c&l↓ &7";
		}

		@Override
		public String getBugFixIcon() {
			return "&2&l⛏ &7";
		}
	};

	protected interface Icon {
		String getChangeIcon();

		String getBuffIcon();

		String getNerfIcon();

		String getBugFixIcon();
	}

}
