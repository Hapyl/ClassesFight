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
import ru.hapyl.classesfight.feature.DamageFeature;
import ru.hapyl.classesfight.utils.OldPlayerLib;

public class DamageBlessing implements Blessing {

	public static final double DAMAGE_AMOUNT = 6.0d;

	@Override
	public void bless(Player player) {
		DamageFeature.addHealth(player, -DAMAGE_AMOUNT);
	}

	@Override
	public String message() {
		return "You will suffer of pain!";
	}

	@Override
	public void playSound(Player player) {
		OldPlayerLib.playSound(player, Sound.ENTITY_VILLAGER_NO, 0.75f);
	}

}
