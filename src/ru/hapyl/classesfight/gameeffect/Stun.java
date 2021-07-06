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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Stun implements GameEffect {

	@Override
	public void onEffectStart(Player player, Object... extra) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 10));
		player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 999999, 10));
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999, 250));
	}

	@Override
	public void onEffectStop(Player player) {
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.WEAKNESS);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

	@Override
	public long getTaskDelay() {
		return 3;
	}

	@Override
	public void affectTask(Player player) {
		PlayerLib.spawnParticle(player.getEyeLocation().add(0, 0.25, 0), Particle.FIREWORKS_SPARK, 3, 0.1, 0, 0.1, 0.02f);
	}

	@Override
	public String getEffectName() {
		return "Stun";
	}
}
