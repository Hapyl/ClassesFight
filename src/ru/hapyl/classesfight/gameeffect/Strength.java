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

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Strength implements GameEffect {
	@Override
	public void onEffectStart(Player player, Object... extra) {

	}

	@Override
	public void onEffectStop(Player player) {

	}

	@Override
	public long getTaskDelay() {
		return 10;
	}

	@Override
	public void affectTask(Player player) {
		PlayerLib.spawnParticle(player.getLocation(), Particle.CRIMSON_SPORE, 15, 0, 0.5, 0, 0);
	}

	@Override
	public String getEffectName() {
		return "Strength";
	}
}
