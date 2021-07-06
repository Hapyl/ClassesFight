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

package ru.hapyl.classesfight.utils;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public interface OldPlayerLib {

	static void playSound(Player player, Sound sound, float pitch) {
		player.playSound(player.getLocation(), sound, SoundCategory.RECORDS, 5, pitch);
	}

	static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {
		player.addPotionEffect(new PotionEffect(type, duration, amplifier));
	}

	static void removePotionEffect(Player player, PotionEffectType type) {
		player.removePotionEffect(type);
	}

}
