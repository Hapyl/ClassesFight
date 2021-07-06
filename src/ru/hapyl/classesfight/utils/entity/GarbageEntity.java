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

package ru.hapyl.classesfight.utils.entity;

import kz.hapyl.spigotutils.module.reflect.Reflect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Consumer;
import ru.hapyl.classesfight.GarbageCollector;

public class GarbageEntity<T extends Entity> {

	private final Class<T> clazz;
	private Entity entity;

	public GarbageEntity(Class<T> entClass) {
		this.clazz = entClass;
	}

	public Entity spawn(Location location, Consumer<T> beforeSpawn) {
		this.kill();
		this.entity = GarbageCollector.spawnEntity(location, clazz, beforeSpawn);
		return this.entity;
	}

	public void spawn(Location location) {
		this.spawn(location, null);
	}

	public GarbageEntity<T> cloneEntity() {
		return new GarbageEntity<>(clazz);
	}

	public void kill() {
		if (this.entity == null) {
			return;
		}
		GarbageCollector.entityCollector.remove(this.entity);
		this.entity.remove();
	}

	public void hide(Player... viewers) {
		this.validateSpawned();
		Reflect.hideEntity(this.entity, viewers);
	}

	public void show(Player... viewers) {
		this.validateSpawned();
		Reflect.showEntity(this.entity, viewers);
	}

	public Entity getEntity() {
		return entity;
	}

	private void validateSpawned() {
		if (this.entity == null) {
			throw new IllegalStateException("Entity must be spawned to use this!");
		}
	}

	private void validateNotSpawned() {
		if (this.entity != null) {
			throw new IllegalStateException("Entity must not be spawned to use this!");
		}
	}

}
