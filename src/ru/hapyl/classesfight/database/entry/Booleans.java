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

package ru.hapyl.classesfight.database.entry;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;

import java.util.Locale;

public enum Booleans {

	HAS_TALKED_TO_THE_EYE;

	Booleans() {
	}

	public boolean isTrue(Player player) {
		return isTrue(player, this.toPath());
	}

	public void setValue(Player player, boolean flag) {
		setValue(player, this.toPath(), flag);
	}

	public boolean getValue(Player player, boolean def) {
		return getValue(player, this.toPath(), def);
	}

	public static boolean isTrue(Player player, String path) {
		return Database.getDatabase(player).getBooleanEntry().getBoolean(path, false);
	}

	public static void setValue(Player player, String path, boolean flag) {
		Database.getDatabase(player).getBooleanEntry().setBoolean(path, flag);
	}

	public boolean getValue(Player player, String path, boolean def) {
		return Database.getDatabase(player).getBooleanEntry().getBoolean(path, def);
	}

	private String toPath() {
		return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
	}

}
