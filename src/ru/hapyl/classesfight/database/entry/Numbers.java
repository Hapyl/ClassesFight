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

public enum Numbers {

	RELIC_EXCHANGE_LEVEL;

	Numbers() {
	}

	public int getInt(Player player, int def) {
		return getInt(player, this.toPath(), def);
	}

	public void setValue(Player player, int value) {
		setValue(player, this.toPath(), value);
	}

	public int addValue(Player player, int toAdd) {
		return addValue(player, this.toPath(), toAdd);
	}

	public static int addValue(Player player, String path, int toAdd) {
		final int old = getInt(player, path, 0);
		setValue(player, path, old + toAdd);
		return old;
	}

	public static int getInt(Player player, String path, int def) {
		return Database.getDatabase(player).getYaml().getInt(path, def);
	}

	public static void setValue(Player player, String path, int value) {
		Database.getDatabase(player).getYaml().set(path, value);
	}

	private String toPath() {
		return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
	}


}
