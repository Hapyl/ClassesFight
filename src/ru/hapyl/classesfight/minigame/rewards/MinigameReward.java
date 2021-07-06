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

package ru.hapyl.classesfight.minigame.rewards;

import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.experience.Experience;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.minigame.AbstractMinigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinigameReward implements Reward {

	private final Map<Integer, ExtraReward> rewards;

	public MinigameReward() {
		this.rewards = new HashMap<>();
	}

	public MinigameReward putReward(int level, ExtraReward reward) {
		this.rewards.put(level, reward);
		return this;
	}

	public Map<Integer, ExtraReward> getRewards() {
		return rewards;
	}

	public ExtraReward getAtOr(int lvl, ExtraReward def) {
		return rewards.getOrDefault(lvl, def);
	}

	public void grantAll(Player player, int upTo, AbstractMinigame ref) {
		List<ExtraReward> inRewards = new ArrayList<>();
		for (Integer lvl : this.rewards.keySet()) {
			if (lvl <= upTo) {
				inRewards.add(this.rewards.get(lvl));
			}
		}
		if (!inRewards.isEmpty()) {
			final Database database = Database.getDatabase(player);
			final StringBuilder builder = new StringBuilder();
			long coins = 0;
			long rubies = 0;
			long exp = 0;
			List<RareReward> perks = new ArrayList<>();
			for (ExtraReward reward : inRewards) {
				coins += reward.getCoins();
				rubies += reward.getRubies();
				exp += reward.getExp();
				if (reward.getRareReward() != null) {
					perks.add(reward.getRareReward());
				}
			}
			if (coins > 0) {
				builder.append(String.format("&6+%s Coins", coins));
				database.getCoinsEntry().grantCoins(coins, false, null);
			}
			if (rubies > 0) {
				builder.append(String.format("&6, +%s Rub%s", rubies, rubies == 1 ? "y" : "ies"));
				database.getRubyEntry().grantRubies(rubies, null);
			}
			if (exp > 0) {
				builder.append(String.format("&6, +%s Exp", exp));
				Experience.getInstance().grantExp(player, exp);
			}
			if (!perks.isEmpty()) {
				for (RareReward rareReward : perks) {
					builder.append(", &6").append(rareReward.getName());
					rareReward.grantReward(player);
				}
			}
			ref.sendMessage(builder.toString());
		}
	}

	public boolean hasAnyRewards() {
		return !this.rewards.isEmpty();
	}

}
