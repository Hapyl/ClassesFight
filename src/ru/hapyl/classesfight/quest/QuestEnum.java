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
import ru.hapyl.classesfight.quest.quest.UseAbility;

public enum QuestEnum {

	INVALID_QUEST(new Quest("Invalid Quest", "You should have never able to see this!") {
		@Override
		public void startQuest(Player player) {
			player.sendMessage("Invalid Quest Invokation Error");
		}
	}),
	USE_ABILITY(new UseAbility());

	private final Quest quest;

	QuestEnum(Quest quest) {
		this.quest = quest;
	}

	public void startQuest(Player player) {
		this.quest.startQuest(player);
	}

	public QuestEnum byQuest(Quest quest) {
		for (QuestEnum value : values()) {
			if (value.getQuest() == quest) {
				return value;
			}
		}
		return INVALID_QUEST;
	}

	public boolean isValid() {
		return this != INVALID_QUEST;
	}

	public Quest getQuest() {
		return quest;
	}
}
