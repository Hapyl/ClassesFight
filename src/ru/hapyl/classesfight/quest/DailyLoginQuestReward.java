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

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.hapyl.classesfight.PackagedReward;
import ru.hapyl.classesfight.database.Database;

import java.util.HashMap;
import java.util.Map;

public class DailyLoginQuestReward {

	private final JavaPlugin plugin;
	private final Map<Long, PackagedReward> perDayRewards;

	private static final long MILLIS_WHOLE_DAY = 86400000L;

	public DailyLoginQuestReward(JavaPlugin plugin) {
		this.plugin = plugin;
		this.perDayRewards = new HashMap<>();
		this.setUpRewards();
	}

	private void setUpRewards() {
		for (int i = 0; i < 21; i++) {
			final boolean lastDay = i % 7 == 6;
			this.perDayRewards.put((long)i, PackagedReward.lazy(
					100 + (50 * i) * (lastDay ? 2 : 1),
					lastDay ? 2 : 0,
					(5 * (i + 1)) * (lastDay ? 2 : 1)));
		}
	}

	public PackagedReward getCurrentReward(long day) {
		return perDayRewards.getOrDefault(day, PackagedReward.lazy(1));
	}

	public long getLastDailyTime(Player player) {
		return Database.getDatabase(player).getYaml().getLong("daily.last-login", 0L);
	}

	public void resetMillis(Player player) {
		setLastDailyTime(player, 0L);
	}

	public void setLastDailyTime(Player player, long unit) {
		Database.getDatabase(player).getYaml().set("daily.last-login", unit);
	}

	public long getCurrentDay(Player player) {
		return Database.getDatabase(player).getYaml().getLong("daily.day", 0L);
	}

	public void addCurrentDay(Player player) {
		Database.getDatabase(player).getYaml().set("daily.day", this.getCurrentDay(player) + 1);
	}

	public void claimRewards(Player player) {
		final long currentDay = this.getCurrentDay(player);
		final PackagedReward currentReward = this.getCurrentReward(currentDay % 21);
		this.addCurrentDay(player);
		this.setLastDailyTime(player, System.currentTimeMillis());
		Chat.sendMessage(player, "&aClaimed daily rewards for day %s! &7(%s total)", (currentDay % 21) + 1, currentDay + 1);
		currentReward.grantReward(player);
	}

	public long nextDailyIn(Player player) {
		final long current = System.currentTimeMillis();
		return Math.max(0L, MILLIS_WHOLE_DAY - (current - this.getLastDailyTime(player)));
	}

	public String nextDailyInString(Player player) {
		final long value = this.nextDailyIn(player);
		final long[] time = this.getTime(value);

		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < 3; i++) {
			if (time[i] > i) {
				builder.append(time[i]).append(i == 0 ? "h" : i == 1 ? "m" : "s").append(" ");
			}
		}

		return builder.toString().trim();
	}

	private long[] getTime(long millis) {
		long totalSecs = millis / 1000;
		long hours = totalSecs / 3600;
		long minutes = (totalSecs % 3600) / 60;
		long seconds = totalSecs % 60;
		return new long[]{hours, minutes, seconds};
	}

	public Map<Long, PackagedReward> getPerDayRewards() {
		return perDayRewards;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}
}
