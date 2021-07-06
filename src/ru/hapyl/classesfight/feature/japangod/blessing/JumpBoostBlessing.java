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

package ru.hapyl.classesfight.feature.japangod.blessing;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class JumpBoostBlessing implements Blessing {

	private static final PotionEffect EFFECT = new PotionEffect(PotionEffectType.JUMP, 20 * 20, 2);


	@Override
	public void bless(Player player) {
		player.addPotionEffect(EFFECT);
	}

	@Override
	public String message() {
		return "You will jump higher.";
	}

	@Override
	public void playSound(Player player) {
		OldPlayerLib.playSound(player, Sound.ENTITY_RABBIT_DEATH, 0.75f);
	}

}
