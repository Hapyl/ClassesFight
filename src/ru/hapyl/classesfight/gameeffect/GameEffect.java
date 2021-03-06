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

package ru.hapyl.classesfight.gameeffect;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public interface GameEffect {

	/**
	 * Executes whenever effect first applied.
	 *
	 * @param player - Receiver.
	 */
	void onEffectStart(Player player, Object... extra);

	default void onEffectUpdate(Player player, Object... extra) {
	}

	/**
	 * Executes whenever effect stops.
	 *
	 * @param player - Receiver.
	 */
	void onEffectStop(Player player);

	/**
	 * Returns a string name of effect.
	 */
	String getEffectName();

	/**
	 * If value greater than 0L then 'affectTask' will be played after delay until effect will stop.
	 */
	default long getTaskDelay() {
		return 0L;
	}

	/**
	 * Executes if 'getTaskDelay' greater than 0.
	 *
	 * @param player - Receiver.
	 */
	default void affectTask(Player player) {

	}

	/**
	 * Will be displayed in TAB after the time, can be used to show charged of the shield, etc.
	 */
	default String getExtraInfo(Player player) {
		return "";
	}

	static void displayParticleForBut(Player player, Particle particle, Location loc, int amount, double oX, double oY, double oZ, float speed) {
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online == player) {
				continue;
			}
			online.spawnParticle(particle, loc, amount, oX, oY, oZ, speed);
		}
	}


}
