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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.hapyl.classesfight.annotations.Singleton;

public class ClassesFightPlugin extends JavaPlugin {

	@Singleton private static ClassesFight singleton;

	@Override
	public void onEnable() {
		singleton = new ClassesFight(this);
		singleton.onEnable();
		checkReload();
	}

	@Override
	public void onDisable() {
		singleton.onDisable();
	}

	private void checkReload() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {

					final Server server = Bukkit.getServer();
					final int reloadCount = server.getClass().getField("reloadCount").getInt(server);

					if (reloadCount > 0) {
						errorMessage("&4&lServer reload detected! &cClassesFight does not support reloads, it &lwill &cbreak the plugin causing errors such as \"NoClassDefFoundError\", \"ClassNotFoundException\", etc. If you encounter them, please &e/restart &cyour server!");
					}

				} catch (Exception ignored0) {
				}
			}
		}.runTaskLater(this, 7);
	}

	private void errorMessage(String abc, Object... replacement) {
		final String message = ChatColor.translateAlternateColorCodes('&', String.format(abc, replacement));
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer.isOp()) {
				onlinePlayer.sendMessage(message);
			}
		}
		Bukkit.getConsoleSender().sendMessage(message);
	}

	public static ClassesFight getSingleton() {
		return singleton;
	}
}
