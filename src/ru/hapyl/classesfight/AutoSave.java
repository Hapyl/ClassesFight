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
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.database.Database;

public class AutoSave {

	private boolean scheduleSaveAfterGame;

	public AutoSave(JavaPlugin plugin, long period) {
		period = Math.max(1, period);

		new BukkitRunnable() {
			@Override
			public void run() {

				if (GameManager.current().isGameInProgress()) {
					scheduleSaveAfterGame = true;
					Chat.broadcast("&7&oScheduled to save data after this game.");
					return;
				}

				saveData();

			}
		}.runTaskTimer(plugin, period, period);

	}

	public boolean shouldSaveAfterGame() {
		return scheduleSaveAfterGame;
	}

	public void saveData() {
		if (shouldSaveAfterGame()) {
			scheduleSaveAfterGame = false;
		}

		Chat.broadcast("&7&oSaving data, expect some lag...");
		try {
			Bukkit.getOnlinePlayers().forEach(player -> {
				Database.saveData(player);
				Database.hashedDatabases.remove(player.getUniqueId());
				Database.createInstance(player);
			});

			ClassesFight.getPlugin().saveConfig();
			ClassesFight.getSingleton().getStatsContainer().writeToFile();
			ClassesFight.getSingleton().getStatsContainer().loadFile();

		} catch (Exception e) {
			Chat.broadcast("&cAn error occurred whilst trying to save data! Check the console.");
			e.printStackTrace();
			return;
		}

		Chat.broadcast("&7&oSuccessfully saved data!");

	}

}
