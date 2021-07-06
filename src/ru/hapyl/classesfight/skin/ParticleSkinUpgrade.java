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
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleSkinUpgrade extends SkinUpgrade {

	private final Particle particle;
	private final int amount;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	private final float speed;

	public ParticleSkinUpgrade(String name, Particle particle, int amount, float speed) {
		super(name, Type.PARTICLE);
		this.particle = particle;
		this.amount = amount;
		this.offsetX = 0.0d;
		this.offsetY = 0.0d;
		this.offsetZ = 0.0d;
		this.speed = speed;
	}

	@Override
	public void affect(Player player) {
		final Location location = player.getLocation();
		location.getWorld().spawnParticle(this.particle, location, this.amount, this.offsetX, this.offsetY, this.offsetZ, this.speed);
	}

}
