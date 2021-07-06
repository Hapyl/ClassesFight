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

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Resistance implements GameEffect {

	@Override
	public void onEffectStart(Player player, Object... extra) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 69));
	}

	@Override
	public void onEffectStop(Player player) {
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
	}

	@Override
	public long getTaskDelay() {
		return 4;
	}

	@Override
	public void affectTask(Player player) {
		player.getWorld()
				.spawnParticle(Particle.CRIT_MAGIC, player.getLocation().clone().add(0, 1, 0), 7, 0.2, 0.2, 0.2, 0.5);
	}

	@Override
	public String getEffectName() {
		return "Resistance";
	}
}
