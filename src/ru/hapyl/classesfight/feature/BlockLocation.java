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

package ru.hapyl.classesfight.feature;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockLocation {

	private final int x;
	private final int y;
	private final int z;
	private final float yaw;
	private final float pitch;

	public BlockLocation(int x, int y, int z) {
		this(x, y, z, 0.0f, 0.0f);
	}

	public BlockLocation(int x, int y, int z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public Block getBlock() {
		return this.toLocation().getBlock();
	}

	public void setBlock(Material material) {
		this.toLocation().getBlock().setType(material, false);
	}

	public BlockLocation(Location loc) {
		this(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	public Location toLocation() {
		return toLocation(false);
	}

	public Location toLocation(boolean includeRotation) {
		final Location location = new Location(Bukkit.getWorlds().get(0), this.x + 0.5d, this.y, this.z + 0.5d);
		if (includeRotation) {
			location.setYaw(this.getYaw());
			location.setPitch(this.getPitch());
		}
		return location;
	}

	public Location centralize() {
		// center the location so it doesn't spawn at the corner of a block
		return new Location(Bukkit.getWorlds().get(0), this.x + .5, this.y + .5, this.z + .5);
	}

	public boolean compare(BlockLocation item) {
		return this.x == item.x && this.y == item.y && this.z == item.z;
	}

	public boolean compare(int[] array) {
		if (array.length != 3) {
			throw new ArrayIndexOutOfBoundsException("Array length must be 3, not " + array.length);
		}
		else {
			return compare(array[0], array[1], array[2]);
		}
	}

	public boolean compare(int x, int y, int z) {
		return this.x == x && this.y == y && this.z == z;
	}

}