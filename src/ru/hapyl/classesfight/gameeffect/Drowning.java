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
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;

public class Drowning implements GameEffect {

	@Override
	public void onEffectStart(Player player, Object... extra) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 3));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 9999999, 3));
	}

	@Override
	public void onEffectStop(Player player) {
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
	}

	@Override
	public String getEffectName() {
		return "Drowning";
	}

	@Override
	public long getTaskDelay() {
		return 10;
	}

	@Override
	public void affectTask(Player player) {
		DamageFeature.damage(player, player, 0.5d, EnumDamageCause.ENTITY_ATTACK, true);
		GameEffect.displayParticleForBut(player, Particle.BUBBLE_POP, player.getLocation(), 10, 0.25d, 0.5d, 0.25d, 0.1f);
	}

}
