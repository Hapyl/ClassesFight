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

package ru.hapyl.classesfight.tablist;

import kz.hapyl.spigotutils.SpigotUtilsPlugin;
import kz.hapyl.spigotutils.module.player.song.Song;
import kz.hapyl.spigotutils.module.player.song.SongPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.ClassesFight;
import ru.hapyl.classesfight.quest.DailyLoginQuestReward;

public enum Activity {

	DAILY_REWARD("Daily Reward", new ActivityAction() {
		@Override
		public String getString(Player player) {
			final DailyLoginQuestReward daily = ClassesFight.getSingleton().getDailyRewards();
			return daily.nextDailyIn(player) <= 0 ? "&6&lAvailable!" : daily.nextDailyInString(player);
		}

		@Override
		public boolean isEnabled(Player player) {
			return true;
		}
	}),

	RADIO("Radio", new ActivityAction() {
		private final SongPlayer radio = SpigotUtilsPlugin.getPlugin().getSongPlayer();

		@Override
		public String getString(Player player) {

			final Song song = radio.getCurrentSong();
			if (song != null) {
				return String.format("&f%s\n&8%s", song.getName(), getProgress());
			}
			return "";

		}

		@Override
		public boolean isEnabled(Player player) {
			return radio.isPlaying() && radio.isListener(player);
		}

		private String getProgress() {
			final StringBuilder builder = new StringBuilder();
			final int frame = (int) (radio.getCurrentFrame() * 30 / radio.getMaxFrame());
			for (int i = 0; i < 30; i++) {
				builder.append(i < frame ? ChatColor.DARK_AQUA : ChatColor.DARK_GRAY);
				builder.append("|");
			}
			return radio.isPaused() ? "&e&lPAUSED" : builder.toString();
		}

	});

	private final String display;
	private final ActivityAction about;

	Activity(String display, ActivityAction action) {
		this.display = display;
		this.about = action;
	}

	public String getName() {
		return display;
	}

	public String getAbout(Player player) {
		return about.getString(player);
	}

	public boolean isEnabled(Player player) {
		return this.about.isEnabled(player);
	}

	public interface ActivityAction {

		String getString(Player player);

		boolean isEnabled(Player player);

	}

}
