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

package ru.hapyl.classesfight.consumable;

import org.bukkit.Location;
import ru.hapyl.classesfight.GameMap;

public class ConsumableLocation {

	private final GameMap map;
	private final double x, y, z;

	public ConsumableLocation(GameMap map, double x, double y, double z) {
		this.map = map;
		this.x = x + 0.5;
		this.y = y - 1.416;
		this.z = z + 0.5;
	}

	public boolean compareMap() {
		return GameMap.getCurrentMap() == this.map;
	}

	public Location toLocation() {
		return new Location(this.map.getLocation().getWorld(), this.x, this.y, this.z);
	}

	public GameMap getMap() {
		return map;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}


}
