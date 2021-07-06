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

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class UpgradableWrappedSkin extends WrappedSkin implements Upgradable {

	private final Map<Integer, SkinUpgrade> upgrades = new HashMap<>();

	public UpgradableWrappedSkin(String headTexture, Color chestPlateColor, Color leggingsColor, Color bootsColor) {
		super(headTexture, chestPlateColor, leggingsColor, bootsColor);
	}

	public UpgradableWrappedSkin(ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots) {
		super(helmet, chestPlate, leggings, boots);
	}

	public void setUpgrade(int lvl, SkinUpgrade upgrade) {
		this.upgrades.putIfAbsent(lvl, upgrade);
	}

	public Map<Integer, SkinUpgrade> getUpgrades() {
		return upgrades;
	}
}
