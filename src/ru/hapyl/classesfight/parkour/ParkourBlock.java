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

package ru.hapyl.classesfight.parkour;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import ru.hapyl.classesfight.feature.BlockLocation;

public class ParkourBlock extends BlockLocation {

	private Material oldBlockMaterial;
	private final ParkourBlockType type;

	public ParkourBlock(ParkourBlockType type, int x, int y, int z) {
		this(type, x, y, z, 0.0f, 0.0f);
	}

	public ParkourBlock(ParkourBlockType type, int x, int y, int z, float yaw, float pitch) {
		super(x, y, z, yaw, pitch);
		this.type = type;
	}

	public boolean isCheckpoint() {
		return this.type == ParkourBlockType.CHECKPOINT;
	}

	public ParkourBlockType getType() {
		return type;
	}

	public void remove(World world) {
		getBlockAt(world).setType(oldBlockMaterial, false);
	}

	private Block getBlockAt(World world) {
		return world.getBlockAt(this.getX(), this.getY(), this.getZ());
	}

	public void spawn(World world) {
		final Block blockAt = getBlockAt(world);
		this.oldBlockMaterial = blockAt.getType();
		blockAt.setType(this.getType().getMaterial(), false);
	}

}
