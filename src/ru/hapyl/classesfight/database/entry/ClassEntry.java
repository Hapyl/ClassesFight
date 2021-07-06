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

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.Validator;

public class ClassEntry extends DatabaseEntry {
	public ClassEntry(Database database) {
		super(database);
	}

	public void setClass(ClassManager clazz) {
		this.database.getYaml().set("class", enumToJson(clazz));
	}

	public ClassManager getClazz() {
		final ClassManager enumValue = Validator.getEnumValue(ClassManager.class, this.database.getYaml().getString("class", "hunter"));
		if (enumValue == null) {
			return ClassManager.HUNTER;
		}
		else {
			return enumValue;
		}
	}

	public static void handleClassRejoin() {
		Bukkit.getOnlinePlayers().forEach(ClassEntry::handleClassRejoin);
	}

	public static void handleClassRejoin(Player player) {
		final ClassEntry entry = Database.getDatabase(player).getClassEntry();
		ClassManager.setClass(player, entry.getClazz(), false);
	}

}
