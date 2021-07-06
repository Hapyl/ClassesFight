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

package ru.hapyl.classesfight.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class MinigameRunnableListener implements Runnable, Listener {

	private final MinigameManager manager = MinigameManager.current();

	@Override
	public void run() {
		final Map<UUID, AbstractMinigame> hash = MinigameManager.current().getRunningMinigames();
		if (!hash.isEmpty()) {
			hash.forEach((uuid, ref) -> ref.onTick());
		}
	}

	@EventHandler()
	public void handlePlayerLeave(PlayerQuitEvent ev) {
		final Player player = ev.getPlayer();
		final AbstractMinigame target = manager.getAbstractMinigame(player);
		if (target != null) {
			target.forceKick(false);
			target.clearTrash();
		}
	}

}
