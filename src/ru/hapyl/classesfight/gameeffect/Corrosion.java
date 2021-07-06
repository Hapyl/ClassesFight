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

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Corrosion implements GameEffect {
	@Override
	public void onEffectStart(Player player, Object... extra) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 999999, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 999999, 4));
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 999999, 4));
	}

	@Override
	public void onEffectStop(Player player) {
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.POISON);
		player.removePotionEffect(PotionEffectType.BLINDNESS);
	}

	@Override
	public String getEffectName() {
		return "Corrosion";
	}
}
