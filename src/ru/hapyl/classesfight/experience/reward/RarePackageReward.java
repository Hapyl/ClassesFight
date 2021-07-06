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

package ru.hapyl.classesfight.experience.reward;

import org.bukkit.Material;

public class RarePackageReward extends RareReward {

	private final Material material;
	private final int amount;
	private long coins;
	private long exp;
	private long ruby;

	public RarePackageReward(String name, Material material, int amount) {
		super(name, "");
		this.material = material;
		this.amount = amount;
	}


	public RarePackageReward(String name, Material material) {
		this(name, material, 1);
	}

	public long getCoins() {
		return coins;
	}

	public int getAmount() {
		return amount;
	}

	public RarePackageReward addCoins(long coins) {
		this.coins += coins;
		return this;
	}

	public long getExp() {
		return exp;
	}

	public RarePackageReward addExp(long exp) {
		this.exp += exp;
		return this;
	}

	public long getRuby() {
		return ruby;
	}

	public Material getMaterial() {
		return material;
	}

	public RarePackageReward addRuby(long ruby) {
		this.ruby += ruby;
		return this;
	}
}
