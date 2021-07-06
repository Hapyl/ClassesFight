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

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.experience.reward.RareReward;
import ru.hapyl.classesfight.minigame.AbstractMinigame;

public class ExtraReward implements Reward {

	private final long coins;
	private final long rubies;
	private final long exp;
	private final RareReward rareReward;

	public ExtraReward() {
		this(0, 0, 0, null);
	}

	public ExtraReward(long coins) {
		this(coins, 0, 0, null);
	}

	public ExtraReward(long coins, long rubies) {
		this(coins, rubies, 0, null);
	}

	public ExtraReward(long coins, long rubies, long exp) {
		this(coins, rubies, exp, null);
	}

	public ExtraReward(long coins, long rubies, long exp, RareReward rare) {
		this.coins = coins;
		this.exp = exp;
		this.rubies = rubies;
		this.rareReward = rare;
	}

	public long getCoins() {
		return coins;
	}

	public long getRubies() {
		return rubies;
	}

	@Nullable
	public RareReward getRareReward() {
		return rareReward;
	}

	public long getExp() {
		return this.exp;
	}

	@Override
	public boolean hasAnyRewards() {
		return this.getCoins() > 0 || this.getExp() > 0 || this.getRubies() > 0 || this.getRareReward() != null;
	}

	@Override
	public void grantAll(Player player, int upTo, AbstractMinigame minigame) {

	}
}
