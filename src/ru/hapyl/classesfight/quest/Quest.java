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

package ru.hapyl.classesfight.quest;

import org.bukkit.entity.Player;

/**
 * This will be used to create and store Quests
 */
public abstract class Quest {

	private int id;
	private final String name;
	private final String questInfo;

	public Quest(String name, String questInfo) {
		this.id = -1;
		this.name = name;
		this.questInfo = questInfo;
	}

	public abstract void startQuest(Player player);

	public final void setId(int id) {
		this.id = id;
	}

	public final int getId() {
		return id;
	}

	public final boolean isRegistered() {
		return this.id != -1;
	}

	public final String getName() {
		return name;
	}

	public final String getQuestInfo() {
		return questInfo;
	}
}
