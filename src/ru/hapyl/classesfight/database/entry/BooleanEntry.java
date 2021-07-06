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

import ru.hapyl.classesfight.database.Database;

public class BooleanEntry extends DatabaseEntry {
	public BooleanEntry(Database database) {
		super(database);
	}

	public void setBoolean(String path, boolean bool) {
		this.database.getYaml().set(path, bool);
	}

	public boolean getBoolean(String path, boolean def) {
		return this.database.getYaml().getBoolean(path, def);
	}

}
