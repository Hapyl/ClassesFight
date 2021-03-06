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

package ru.hapyl.classesfight.minigame.games.superpair;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum PairType {

	COINS(10, 1500, Material.GOLD_NUGGET, Material.GOLD_INGOT, Material.GOLDEN_HELMET, Material.GOLD_BLOCK, Material.SUNFLOWER, Material.YELLOW_GLAZED_TERRACOTTA),
	EXP(2, 300, Material.LIME_DYE, Material.EMERALD, Material.DIAMOND_HELMET, Material.EMERALD_BLOCK, Material.SLIME_BALL, Material.GREEN_GLAZED_TERRACOTTA),
	RUBIES(1, 10, Material.NETHER_WART, Material.REDSTONE, Material.NETHER_WART_BLOCK, Material.REDSTONE_BLOCK, Material.RED_DYE, Material.RED_GLAZED_TERRACOTTA),
	OTHER(0, 0, Material.CHEST);

	private final long minValue;
	private final long maxValue;
	private final List<Material> validMaterials;

	PairType() {
		this(-1, -1);
	}

	PairType(long i, long i1, Material... materials) {
		this.minValue = i;
		this.maxValue = i1;
		this.validMaterials = new ArrayList<>(Arrays.asList(materials));
	}

	public List<Material> getValidMaterials() {
		return validMaterials;
	}

	public long getMinValue() {
		return minValue;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public Material getMaterial() {
		if (this.validMaterials.isEmpty()) {
			return Material.CHEST;
		}
		return this.validMaterials.get(ThreadLocalRandom.current().nextInt(this.validMaterials.size()));
	}

	public long random() {
		if (this == OTHER) {
			return -1;
		}
		return ThreadLocalRandom.current().nextLong(this.getMinValue(), this.getMaxValue());
	}
}
