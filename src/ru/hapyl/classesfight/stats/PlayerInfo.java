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

package ru.hapyl.classesfight.stats;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.classes.ClassManager;

public class PlayerInfo {

	private final Player player;
	private ClassManager bestClass;
	private long kills;
	private long deaths;
	private long wins;

	public PlayerInfo(Player player) {
		this.player = player;
		this.getBestClass();
	}

	public Player getPlayer() {
		return player;
	}

	public long getDeaths() {
		return deaths;
	}

	public long getKills() {
		return kills;
	}

	public long getWins() {
		return wins;
	}

	public ClassManager getBest() {
		return this.bestClass;
	}

	private void getBestClass() {
		ClassManager bestClass = null;

		for (ClassManager validClass : ClassManager.getValidClasses()) {
			if (bestClass == null) {
				bestClass = validClass;
				continue;
			}
			if (isOtherBetter(bestClass, validClass)) {
				bestClass = validClass;
			}
		}

		this.bestClass = bestClass;
		this.kills = getKills(bestClass);
		this.deaths = getDeaths(bestClass);
		this.wins = getWins(bestClass);

	}

	private boolean isOtherBetter(ClassManager what, ClassManager other) {
		final long thisKills = getKills(what);
		final long thisWins = getWins(what);
		final long otherKills = getKills(other);
		final long otherWins = getWins(other);
		return otherKills > thisKills && otherWins > thisWins;
	}

	private long getKills(ClassManager clazz) {
		final StatsContainer stats = StatsContainer.current();
		return stats.getClassEntry(clazz, ClassEntryInfo.KILLS);
	}

	private long getWins(ClassManager clazz) {
		final StatsContainer stats = StatsContainer.current();
		return stats.getClassEntry(clazz, ClassEntryInfo.WINS);
	}

	private long getDeaths(ClassManager clazz) {
		final StatsContainer stats = StatsContainer.current();
		return stats.getClassEntry(clazz, ClassEntryInfo.DEATHS);
	}


}
