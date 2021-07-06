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

package ru.hapyl.classesfight.ability.storage;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import ru.hapyl.classesfight.disaster.Disaster;

public class LowGravity extends Disaster {
	public LowGravity(Material material, String name, String about) {
		super(material, name, about);
	}

	@Override
	public void onTick() {
		manager.getPlayers().forEach(player -> {
			PlayerLib.addEffect(player, PotionEffectType.JUMP, 20, 3);
			PlayerLib.addEffect(player, PotionEffectType.SLOW_FALLING, 20, 1);
		});
	}

	@Override
	public void onStart() {

	}

	@Override
	public void onStop() {

	}
}
