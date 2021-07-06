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

package ru.hapyl.classesfight.gamemode.mode;

import kz.hapyl.spigotutils.module.chat.Chat;
import org.bukkit.entity.Player;
import ru.hapyl.classesfight.gamemode.CFGameMode;
import ru.hapyl.classesfight.runnable.GameTask;

public class DeathMatch extends CFGameMode {

	public DeathMatch() {
		super("Death Match FFA", 300);
	}

	@Override
	public void processBeforeGameStart() {

	}

	@Override
	public void processPlayerBeforeGameStart(Player player) {

	}

	@Override
	public void processAfterGameEnd() {

	}

	@Override
	public void processPlayerAfterGameEnd(Player player) {

	}

	private final int respawnTimeSec = 5;

	@Override
	public void processPlayerDeath(Player player, Player killer) {
		new GameTask() {

			int respawnAt = respawnTimeSec + 1;

			@Override
			public void run() {

				// respawn
				if (++respawnAt <= 0) {
					Chat.sendTitle(player, "&aRespawned!", "", 0, 10, 5);
				}
				else {
					Chat.sendTitle(player, "&aYou will respawn", String.format("&ain %s...", respawnAt), 0, 40, 0);
				}


			}
		}.runTaskTimer(0, 20);
	}

	public void spawnPlayer(Player player) {

	}

}
