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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import ru.hapyl.classesfight.database.Database;

public class RubyEntry extends DatabaseEntry {

	public RubyEntry(Database database) {
		super(database);
	}

	public void grantRubies(long howMuch, @Nullable String reason) {
		addRubies(howMuch);
		if (reason != null) {
			Chat.sendMessage(this.database.getPlayer(), "&c&lRUBY! &e+%s &e%s (%s)", howMuch, howMuch == 1 ? "Ruby" : "Rubies", reason);
		}
	}

	public boolean hasRubies(long atLeast) {
		return this.getRubies() >= atLeast;
	}

	public long getRubies() {
		return this.database.getYaml().getLong("rubies", 0L);
	}

	public void setRubies(long toSet) {
		this.database.getYaml().set("rubies", toSet);
	}

	public void addRubies(long toAdd) {
		setRubies(getRubies() + toAdd);
	}

	public void removeRubies(int toRemove) {
		this.setRubies(getRubies() - toRemove);
	}

}
