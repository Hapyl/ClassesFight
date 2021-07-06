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

import javax.annotation.Nullable;
import java.util.*;

public class QuestManager {

	private static final QuestManager classInstance = new QuestManager();

	protected final Map<UUID, List<QuestProgress>> playerQuests;
	protected final Map<Integer, Quest> questById;
	private int freeId;

	private QuestManager() {
		// Private Constructor
		this.playerQuests = new HashMap<>();
		this.questById = new HashMap<>();
	}

	public boolean registerQuest(Quest quest) {
		if (questById.containsValue(quest)) {
			return false;
		}
		quest.setId(this.freeId++);
		questById.put(quest.getId(), quest);
		return true;
	}

	@Nullable
	public Quest getById(int id) {
		return this.questById.getOrDefault(id, null);
	}

	public List<Quest> getActiveQuests(Player player) {
		final List<Quest> pending = new ArrayList<>();
		final UUID uuid = player.getUniqueId();
		if (playerQuests.getOrDefault(uuid, null) == null) {
			return pending;
		}
		// must not be null at this point
		for (QuestProgress progress : playerQuests.get(uuid)) {
			pending.add(progress.getQuest());
		}
		return pending;
	}

	public List<QuestProgress> getActiveProgresses(Player player) {
		return playerQuests.getOrDefault(player.getUniqueId(), new ArrayList<>());
	}

	public boolean hasQuest(Player player, QuestEnum quest) {
		return this.hasQuest(player, quest.getQuest());
	}

	public boolean hasQuest(Player player, Quest quest) {
		return getActiveQuests(player).contains(quest);
	}

	public boolean checkQuestAndIncrement(Player player, QuestEnum quest) {
		if (this.hasQuest(player, quest)) {
			for (QuestProgress activeProgress : getActiveProgresses(player)) {
				if (activeProgress.getQuest().getId() == quest.getQuest().getId()) {
					if (activeProgress.isFinished()) {
						activeProgress.finishQuest();
						return true;
					}
				}
			}
		}
		return false;
	}

	public void startQuest(Player player, QuestProgress quest) {
		this.addQuestAndPut(player, quest);
		quest.sendMessage("QUEST STARTED", quest.getQuest().getQuestInfo());
	}

	private void removeQuestAndPut(Player player, QuestProgress progress) {
		final List<QuestProgress> list = getActiveProgresses(player);
		list.remove(progress);
		playerQuests.put(player.getUniqueId(), list);
	}

	private void addQuestAndPut(Player player, QuestProgress progress) {
		final List<QuestProgress> list = getActiveProgresses(player);
		list.add(progress);
		playerQuests.put(player.getUniqueId(), list);
	}

	public static QuestManager current() {
		return classInstance;
	}
}
