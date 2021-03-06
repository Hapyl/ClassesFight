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

import javax.annotation.Nullable;

public class FeedbackEntry extends DatabaseEntry {
	public FeedbackEntry(Database database) {
		super(database);
	}

	@Nullable
	public String getFeedback() {
		return this.database.getYaml().getString("feedback", null);
	}

	public boolean hasFeedback() {
		return getFeedback() != null;
	}

	public void setFeedback(String args) {
		this.database.getYaml().set("feedback", args);
	}

}
