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
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.feature.EnumDamageCause;

public class Burning implements GameEffect {
	@Override
	public void onEffectStart(Player player, Object... extra) {

	}

	@Override
	public void onEffectStop(Player player) {

	}

	@Override
	public String getEffectName() {
		return "Burning";
	}

	@Override
	public long getTaskDelay() {
		return 10;
	}

	@Override
	public void affectTask(Player player) {
		DamageFeature.damage(player, player, 1.0d, EnumDamageCause.FIRE, true);
		GameEffect.displayParticleForBut(player, Particle.FLAME, player.getLocation(), 10, 0.25d, 0.5d, 0.25d, 0.01f);
	}
}
