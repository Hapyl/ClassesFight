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

package ru.hapyl.classesfight;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.database.Database;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class PackagedReward {

	private long coins;
	private long rubies;
	private long exp;

	public PackagedReward() {
	}

	public static PackagedReward lazy(long coins, long rubies, long exp) {
		final PackagedReward packagedReward = new PackagedReward();
		packagedReward.setCoins(coins);
		packagedReward.setRubies(rubies);
		packagedReward.setExp(exp);
		return packagedReward;
	}

	public static PackagedReward lazy(long coins, long rubies) {
		final PackagedReward packagedReward = new PackagedReward();
		packagedReward.setCoins(coins);
		packagedReward.setRubies(rubies);
		return packagedReward;
	}

	public static PackagedReward lazy(long coins) {
		final PackagedReward packagedReward = new PackagedReward();
		packagedReward.setCoins(coins);
		return packagedReward;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public void setCoins(long coins) {
		this.coins = coins;
	}

	public long getCoins() {
		return coins;
	}

	public void setRubies(long rubies) {
		this.rubies = rubies;
	}

	public long getRubies() {
		return rubies;
	}

	public void grantReward(Player player) {

		final long coins = this.getCoins();
		final long rubies = this.getRubies();
		final long exp = this.getExp();

		Chat.sendMessage(player, "&a&lRewards Claimed!");
		if (coins > 0) {
			Chat.sendMessage(player, " &e+%s %s", coins, coins == 1 ? "Coin" : "Coins");
		}
		if (rubies > 0) {
			Chat.sendMessage(player, " &e+%s %s", rubies, rubies == 1 ? "Ruby" : "Rubies");
		}
		if (exp > 0) {
			Chat.sendMessage(player, " &e+%s Experience", exp);
		}

		Database.getExp(player).addExp(exp);
		Database.getCoins(player).addCoins(coins);
		Database.getRubies(player).addRubies(rubies);
		OldPlayerLib.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 2.0f);

	}

	public String getRewardInfoLore() {
		return getRewardInfoLore("__");
	}

	public String getRewardInfoLore(String spliterator) {
		final StringBuilder builder = new StringBuilder();
		if (this.getCoins() > 0) {
			builder.append(" &e").append(this.getCoins()).append(" Coins");
		}
		if (this.getRubies() > 0) {
			builder.append(spliterator);
			builder.append(" &e").append(this.getRubies()).append(" Rubies");
		}
		if (this.getExp() > 0) {
			builder.append(spliterator);
			builder.append(" &e").append(this.getExp()).append(" Experience");
		}
		return builder.toString();
	}

}
