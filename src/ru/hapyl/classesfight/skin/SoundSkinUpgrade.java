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

package ru.hapyl.classesfight.skin;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SoundSkinUpgrade extends SkinUpgrade {

	private final Sound killSound;
	private final Sound deathSound;

	public SoundSkinUpgrade(String name, Sound killSound, Sound deathSound) {
		super(name, Type.SOUND);
		this.killSound = killSound;
		this.deathSound = deathSound;
	}

	public void affectKill(Location location) {
		location.getWorld().playSound(location, this.killSound, SoundCategory.RECORDS, 10, 1.0f);
	}

	public void affectDeath(Location location) {
		location.getWorld().playSound(location, this.deathSound, SoundCategory.RECORDS, 10, 1.0f);
	}

	@Override
	public void affect(Player player) {

	}
}
