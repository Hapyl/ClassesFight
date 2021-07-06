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
import ru.hapyl.classesfight.quest.relic.Relic;

import java.util.List;

public class CollectibleEntry extends DatabaseEntry {
	public CollectibleEntry(Database database) {
		super(database);
	}

	private final String RELIC_PATH = "collectibles.relic";

	public List<Integer /*id*/> getFoundRelics() {
		return this.database.getYaml().getIntegerList(RELIC_PATH);
	}

	public void addFoundRelic(Relic relic) {
		final List<Integer> foundRelics = getFoundRelics();
		foundRelics.add(relic.getId());
		setFoundRelic(foundRelics);
	}

	public boolean hasFoundRelic(Relic relic) {
		return getFoundRelics().contains(relic.getId());
	}

	public void removeFoundRelic(Relic relic) {
		final List<Integer> foundRelics = getFoundRelics();
		foundRelics.remove((Integer)relic.getId());
		setFoundRelic(foundRelics);
	}

	private void setFoundRelic(List<Integer> ids) {
		this.database.getYaml().set(RELIC_PATH, ids);
	}

}
