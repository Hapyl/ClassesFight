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
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import ru.hapyl.classesfight.utils.CFItemBuilder;

import javax.annotation.Nullable;

public abstract class WrappedSkin {

	private final ItemStack helmet, chestPlate, leggings, boots;

	public static WrappedSkin emptySkin() {
		final ItemStack air = new ItemStack(Material.AIR);
		return new WrappedSkin(air, air, air, air) {
		};
	}

	public WrappedSkin(String headTexture, Color chestPlateColor, Color leggingsColor, Color bootsColor) {
		this(CFItemBuilder.playerHead(headTexture).toItemStack(),
				CFItemBuilder.leatherTunic(chestPlateColor).toItemStack(),
				CFItemBuilder.leatherPants(leggingsColor).toItemStack(),
				CFItemBuilder.leatherBoots(bootsColor).toItemStack());
	}

	public WrappedSkin(ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots) {
		this.helmet = notNull(helmet);
		this.chestPlate = notNull(chestPlate);
		this.leggings = notNull(leggings);
		this.boots = notNull(boots);
	}

	@Nullable
	public ItemStack getLeggings() {
		return this.leggings;
	}

	@Nullable
	public ItemStack getHelmet() {
		return this.helmet;
	}

	@Nullable
	public ItemStack getBoots() {
		return this.boots;
	}

	@Nullable
	public ItemStack getChestPlate() {
		return this.chestPlate;
	}

	private ItemStack notNull(ItemStack item) {
		return item == null ? new ItemStack(Material.AIR) : item;
	}

}
