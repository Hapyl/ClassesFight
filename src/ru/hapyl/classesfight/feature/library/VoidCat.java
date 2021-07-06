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

package ru.hapyl.classesfight.feature.library;

import kz.hapyl.spigotutils.module.player.PlayerLib;
import kz.hapyl.spigotutils.module.util.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Cat;
import ru.hapyl.classesfight.utils.entity.Entities;

public class VoidCat {

	protected static final Location[] locations = {toLocation(120.5, 21.0, 88.5), toLocation(116.5, 20.5, 88.5), toLocation(122.5, 24.0, 89.5), toLocation(117.5, 21.0, 85.5)};

	private final Cat cat;
	private int position;

	public VoidCat() {
		final Location location = locations[0];
		this.cat = (Cat)Entities.CAT.spawn(location, me -> {
			me.setSitting(true);
			me.setOwner(null);
			me.setCatType(Cat.Type.BLACK);
			me.setAdult();
			me.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1.0d);
			me.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0d);
		});
		this.position = 0;
	}

	public void remove() {
		if (this.cat != null) {
			this.cat.remove();
		}
	}

	public void tick() {
		PlayerLib.spawnParticle(this.cat.getLocation(), Particle.SPELL_MOB, 0, 0, 0, 0, 1);
	}

	public void teleportNext() {
		position = position + 1 >= locations.length ? 0 : position + 1;
		final Location location = locations[position];
		this.cat.teleport(location);
		this.cat.setSitting(true);
		PlayerLib.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 1.6f);
	}

	@Nullable
	public Cat getCat() {
		return cat;
	}

	private static Location toLocation(double x, double y, double z) {
		return BukkitUtils.defLocation(x, y, z);
	}

}
